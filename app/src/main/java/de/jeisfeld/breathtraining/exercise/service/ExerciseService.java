package de.jeisfeld.breathtraining.exercise.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import de.jeisfeld.breathtraining.MainActivity;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.data.RepetitionData;
import de.jeisfeld.breathtraining.exercise.data.StepType;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.sound.SoundPlayer;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * A service handling Exercises in the background.
 */
public class ExerciseService extends Service {
	/**
	 * The id for the service.
	 */
	private static final int SERVICE_ID = 1;
	/**
	 * The request code for starting the app from notification.
	 */
	private static final int REQUEST_CODE_START_APP = 1;
	/**
	 * The request code for stopping the service.
	 */
	private static final int REQUEST_CODE_STOP = 2;
	/**
	 * The request code for pausing the service.
	 */
	private static final int REQUEST_CODE_PAUSE = 3;
	/**
	 * The request code for resuming the service.
	 */
	private static final int REQUEST_CODE_RESUME = 4;
	/**
	 * Intent key for the service command.
	 */
	private static final String EXTRA_SERVICE_COMMAND = "de.jeisfeld.breathtraining.SERVICE_COMMAND";
	/**
	 * The id of the notification channel.
	 */
	public static final String CHANNEL_ID = "BreathTrainingChannel";

	/**
	 * The running threads.
	 */
	private static final List<ExerciseAnimationThread> RUNNING_THREADS = new ArrayList<>();
	/**
	 * The service query receiver.
	 */
	private ServiceQueryReceiver mServiceQueryReceiver = null;

	/**
	 * Trigger the exercise service.
	 *
	 * @param context The context.
	 * @param serviceCommand The service command.
	 * @param exerciseData The exercise data.
	 */
	public static void triggerExerciseService(final Context context, final ServiceCommand serviceCommand, final ExerciseData exerciseData) {
		ContextCompat.startForegroundService(context, getTriggerIntent(context, serviceCommand, exerciseData));
	}

	/**
	 * Check if ExerciseService is running.
	 *
	 * @param context The context.
	 * @return true if running.
	 */
	public static boolean isServiceRunning(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo info : am.getRunningServices(Integer.MAX_VALUE)) {
			if (info.service.getClassName().equals(ExerciseService.class.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a service intent for triggering the exercise service.
	 *
	 * @param context The context.
	 * @param serviceCommand The service command.
	 * @param exerciseData The exercise data.
	 * @return The service intent.
	 */
	private static Intent getTriggerIntent(final Context context, final ServiceCommand serviceCommand, final ExerciseData exerciseData) {
		Intent serviceIntent = new Intent(context, ExerciseService.class);
		serviceIntent.putExtra(EXTRA_SERVICE_COMMAND, serviceCommand);
		serviceIntent.putExtra(ServiceReceiver.EXTRA_EXERCISE_DATA, exerciseData);
		return serviceIntent;
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		mServiceQueryReceiver = new ServiceQueryReceiver(this);
		ContextCompat.registerReceiver(this, mServiceQueryReceiver, new IntentFilter(ServiceQueryReceiver.RECEIVER_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED);
		createNotificationChannel();
	}

	@Override
	public final int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent == null) {
			return START_REDELIVER_INTENT;
		}

		final ServiceCommand serviceCommand = (ServiceCommand) intent.getSerializableExtra(EXTRA_SERVICE_COMMAND);
		final ExerciseData exerciseData = (ExerciseData) intent.getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_DATA);

		switch (serviceCommand) {
		case START:
			ExerciseAnimationThread newThread = new ExerciseAnimationThread(exerciseData);
			synchronized (RUNNING_THREADS) {
				RUNNING_THREADS.notifyAll();
				if (RUNNING_THREADS.size() > 0) {
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).stopExercise();
				}
				RUNNING_THREADS.add(newThread);
			}
			newThread.start();
			break;
		case STOP:
			synchronized (RUNNING_THREADS) {
				RUNNING_THREADS.notifyAll();
				if (RUNNING_THREADS.size() > 0) {
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).stopExercise();
				}
			}
			break;
		case PAUSE:
			synchronized (RUNNING_THREADS) {
				if (RUNNING_THREADS.size() > 0) {
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).pause(exerciseData);
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).updateExerciseData(exerciseData, PlayStatus.PAUSED, false);
				}
			}
			break;
		case RESUME:
			synchronized (RUNNING_THREADS) {
				if (RUNNING_THREADS.size() > 0) {
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).resume(exerciseData);
				}
				RUNNING_THREADS.notifyAll();
			}
			break;
		case SKIP:
			synchronized (RUNNING_THREADS) {
				if (RUNNING_THREADS.size() > 0) {
					RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).skipStep();
				}
			}
			break;
		default:
		}
		startNotification(exerciseData, null, serviceCommand, serviceCommand == ServiceCommand.PAUSE);
		return START_STICKY;
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mServiceQueryReceiver);
	}

	@Override
	public final IBinder onBind(final Intent intent) {
		return null;
	}

	/**
	 * Get a wakelock and acquire it.
	 *
	 * @param thread The thread aquiring the wakelock.
	 * @return The wakelock.
	 */
	@SuppressLint("WakelockTimeout")
	private WakeLock acquireWakelock(final Thread thread) {
		if (PreferenceUtil.getSharedPreferenceBoolean(R.string.key_pref_use_wakelock, true)) {
			PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
			assert powerManager != null;
			WakeLock wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "de.jeisfeld.breathtraining:" + thread.hashCode());
			wakelock.acquire();
			return wakelock;
		}
		else {
			return null;
		}
	}

	/**
	 * Create the channel for service animation notifications.
	 */
	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel animationChannel = new NotificationChannel(
					CHANNEL_ID, getString(R.string.notification_channel), NotificationManager.IMPORTANCE_LOW);
			NotificationManager manager = getSystemService(NotificationManager.class);
			assert manager != null;
			manager.createNotificationChannel(animationChannel);
		}
	}

	/**
	 * Start the notification.
	 *
	 * @param exerciseData   The exercise data.
	 * @param exerciseStep   The current exercise step.
	 * @param serviceCommand The service command.
	 * @param isPausing      Flag indicating if the exercise is pausing.
	 */
	private void startNotification(final ExerciseData exerciseData, final ExerciseStep exerciseStep,
								   final ServiceCommand serviceCommand, final boolean isPausing) {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra(ServiceReceiver.EXTRA_EXERCISE_DATA, exerciseData);
		notificationIntent.putExtra(ServiceReceiver.EXTRA_EXERCISE_STEP, exerciseStep);

		int contentTextResource = R.string.notification_text_exercise_running;
		if (exerciseStep != null) {
			contentTextResource = exerciseStep.getStepType().getDisplayResource();
		}
		else if (serviceCommand != null && serviceCommand.getDisplayResource() != 0) {
			contentTextResource = serviceCommand.getDisplayResource();
		}

		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
		remoteViews.setTextViewText(R.id.text_step_name, getString(contentTextResource));
		remoteViews.setViewVisibility(R.id.button_resume, isPausing ? View.VISIBLE : View.INVISIBLE);
		remoteViews.setViewVisibility(R.id.button_pause, isPausing ? View.INVISIBLE : View.VISIBLE);

		PendingIntent pendingIntentStop;
		PendingIntent pendingIntentPause;
		PendingIntent pendingIntentResume;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			pendingIntentStop = PendingIntent.getForegroundService(this, REQUEST_CODE_STOP,
					getTriggerIntent(this, ServiceCommand.STOP, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
			pendingIntentPause = PendingIntent.getForegroundService(this, REQUEST_CODE_PAUSE,
					getTriggerIntent(this, ServiceCommand.PAUSE, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
			pendingIntentResume = PendingIntent.getForegroundService(this, REQUEST_CODE_RESUME,
					getTriggerIntent(this, ServiceCommand.RESUME, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
		}
		else {
			pendingIntentStop = PendingIntent.getService(this, REQUEST_CODE_STOP,
					getTriggerIntent(this, ServiceCommand.STOP, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
			pendingIntentPause = PendingIntent.getService(this, REQUEST_CODE_PAUSE,
					getTriggerIntent(this, ServiceCommand.PAUSE, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
			pendingIntentResume = PendingIntent.getService(this, REQUEST_CODE_RESUME,
					getTriggerIntent(this, ServiceCommand.RESUME, exerciseData),
					PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
		}
		remoteViews.setOnClickPendingIntent(R.id.button_stop, pendingIntentStop);
		remoteViews.setOnClickPendingIntent(R.id.button_pause, pendingIntentPause);
		remoteViews.setOnClickPendingIntent(R.id.button_resume, pendingIntentResume);

		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				REQUEST_CODE_START_APP, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setCustomContentView(remoteViews)
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_notification)
				.build();
		startForeground(SERVICE_ID, notification);
	}

	/**
	 * Update the service after the exercise has ended.
	 *
	 * @param wakeLock The wakelock.
	 * @param animationData The instance of animationData which is ended.
	 * @param thread The thread which is ended.
	 */
	private void updateOnEndExercise(final WakeLock wakeLock, final ExerciseData animationData, final Thread thread) {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
		}
		synchronized (RUNNING_THREADS) {
			// noinspection SuspiciousMethodCalls
			RUNNING_THREADS.remove(thread);
			if (RUNNING_THREADS.size() == 0) {
				SoundPlayer.releaseInstance(MediaTrigger.SERVICE);
				stopService(new Intent(this, ExerciseService.class));
				sendBroadcasts(PlayStatus.STOPPED, null, null);
			}
		}
	}

	/**
	 * A command to be triggered on the service.
	 */
	public enum ServiceCommand {
		/**
		 * Start.
		 */
		START(R.string.text_starting),
		/**
		 * Stop.
		 */
		STOP(R.string.text_stopping),
		/**
		 * Pause.
		 */
		PAUSE(R.string.text_pausing),
		/**
		 * Resume.
		 */
		RESUME(R.string.text_resuming),
		/**
		 * Skip to next step.
		 */
		SKIP(R.string.text_skipping);

		/**
		 * The text resource for displaying the service command.
		 */
		private final int mTextResource;

		/**
		 * Constructor.
		 *
		 * @param textResource The text resource for displaying the service command.
		 */
		ServiceCommand(final int textResource) {
			mTextResource = textResource;
		}

		/**
		 * Get the String resource for display.
		 *
		 * @return The string resource.
		 */
		public int getDisplayResource() {
			return mTextResource;
		}
	}

	/**
	 * Get the sound delay for an exercise step. (Allow pre- delays in case of significant step durations.)
	 *
	 * @param step The step.
	 * @return The sound delay.
	 */
	private static long getDelay(final ExerciseStep step) {
		if (step.getDuration() < 500) { // MAGIC_NUMBER
			return 0;
		}
		else if (step.getDuration() < 1000) { // MAGIC_NUMBER
			return (step.getDuration() - 500) / 5; // MAGIC_NUMBER
		}
		else {
			return 100; // MAGIC_NUMBER
		}
	}

	/**
	 * Send broadcasts for change of service status.
	 *
	 * @param playStatus   The play status.
	 * @param exerciseStep The exercise step.
	 * @param exerciseData The exercise data.
	 */
	public void sendBroadcasts(final PlayStatus playStatus, final ExerciseStep exerciseStep, final ExerciseData exerciseData) {
		sendBroadcast(ServiceReceiver.createIntent(playStatus, exerciseStep, exerciseData));

		Intent intent = new Intent("de.jeisfeld.breathtraining.BREATH_EXERCISE");
		intent.putExtra("de.jeisfeld.breathTraining.playStatus", playStatus.name());
		if (exerciseStep != null) {
			intent.putExtra("de.jeisfeld.breathTraining.stepType", exerciseStep.getStepType().name());
			intent.putExtra("de.jeisfeld.breathTraining.duration", exerciseStep.getDuration());
		}
		sendBroadcast(intent);
	}

	/**
	 * An animation thread for the exercise.
	 */
	private final class ExerciseAnimationThread extends Thread {
		/**
		 * the exercise data.
		 */
		private ExerciseData mExerciseData;
		/**
		 * Flag indicating if the thread is stopping.
		 */
		private boolean mIsStopping = false;
		/**
		 * Flag indicating if exercise is skipping to next breath.
		 */
		private boolean mIsSkipping = false;
		/**
		 * Flag indicating if exercise is pausing.
		 */
		private boolean mIsPausing = false;
		/**
		 * The current exercise step.
		 */
		private ExerciseStep mExerciseStep = null;

		/**
		 * Constructor.
		 *
		 * @param exerciseData The exercise data.
		 */
		private ExerciseAnimationThread(final ExerciseData exerciseData) {
			mExerciseData = exerciseData;
		}

		/**
		 * Update the exercise data.
		 *
		 * @param exerciseData The new exercise data.
		 * @param playStatus The new playStatus.
		 * @param goToRepetitionStart Flag indicating if the repetition should be started from the beginning.
		 */
		private void updateExerciseData(final ExerciseData exerciseData, final PlayStatus playStatus, final boolean goToRepetitionStart) {
			exerciseData.retrieveStatus(mExerciseData, playStatus);
			if (goToRepetitionStart) {
				exerciseData.goBackToRepetitionStart();
			}
			mExerciseData = exerciseData;
		}

		/**
		 * Stop the exercise.
		 */
		private void stopExercise() {
			mIsStopping = true;
			mIsPausing = false;
			mIsSkipping = false;
			interrupt();
		}

		/**
		 * Skip a step.
		 */
		private void skipStep() {
			mIsSkipping = true;
			interrupt();
		}

		/**
		 * Pause the thread.
		 *
		 * @param exerciseData The new exercise data.
		 */
		private void pause(final ExerciseData exerciseData) {
			mIsPausing = true;
			mIsSkipping = true;
			interrupt();
			updateExerciseData(exerciseData, PlayStatus.PAUSED, false);
			sendBroadcasts(PlayStatus.PAUSED, mExerciseStep, exerciseData);
		}

		/**
		 * Resume the thread.
		 *
		 * @param exerciseData The new exercise data.
		 */
		private void resume(final ExerciseData exerciseData) {
			interrupt();
			updateExerciseData(exerciseData, PlayStatus.PLAYING, true);
			mIsPausing = false;
			sendBroadcasts(PlayStatus.PLAYING, mExerciseStep, exerciseData);
		}

		@Override
		public void run() {
			final WakeLock wakeLock = acquireWakelock(this);
			mExerciseStep = mExerciseData.getNextStep();

			while (mExerciseStep != null) {
				if (!(mIsSkipping && mExerciseStep.getStepType().isHold())) {
					// Execute the step, except in case of hold while skipping
					mIsSkipping = false;
					try {
						SoundPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE, mExerciseData.getSoundType(),
								mExerciseStep.getStepType(), getDelay(mExerciseStep), mExerciseStep.getSoundDuration());
						sendBroadcasts(PlayStatus.PLAYING, mExerciseStep, mExerciseData);
						startNotification(mExerciseData, mExerciseStep, null, mIsPausing);
						// noinspection BusyWait
						Thread.sleep(mExerciseStep.getDuration() - getDelay(mExerciseStep));
					}
					catch (InterruptedException e) {
						if (mIsStopping) {
							updateOnEndExercise(wakeLock, mExerciseData, this);
							return;
						}
					}
					synchronized (RUNNING_THREADS) {
						if (mIsPausing) {
							SoundPlayer.getInstance().pause();
							try {
								RUNNING_THREADS.wait();
							}
							catch (InterruptedException e) {
								if (mIsStopping) {
									updateOnEndExercise(wakeLock, mExerciseData, this);
									return;
								}
							}
						}
					}
				}
				mExerciseStep = mExerciseData.getNextStep();
			}

			try {
				SoundPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE, mExerciseData.getSoundType(), StepType.RELAX);
				mExerciseStep = new ExerciseStep(StepType.RELAX, 0, new RepetitionData());
				sendBroadcasts(PlayStatus.PLAYING, mExerciseStep, mExerciseData);
				startNotification(mExerciseData, mExerciseStep, null, mIsPausing);
				Thread.sleep(mExerciseData.getSoundType().getRelaxDuration());
			}
			catch (InterruptedException e) {
				// Ignore
			}
			updateOnEndExercise(wakeLock, mExerciseData, this);
		}
	}

	/**
	 * A broadcast receiver for receiving messages from service to update UI.
	 */
	public static class ServiceQueryReceiver extends BroadcastReceiver {
		/**
		 * The action triggering this receiver.
		 */
		public static final String RECEIVER_ACTION = "de.jeisfeld.breathtraining.SERVICE_QUERY_RECEIVER";
		/**
		 * A reference to the ExerciseService.
		 */
		private final WeakReference<ExerciseService> mExerciseService;

		/**
		 * Default constructor.
		 */
		public ServiceQueryReceiver() {
			mExerciseService = null;
		}

		/**
		 * Constructor.
		 *
		 * @param exerciseService The exerciseService.
		 */
		public ServiceQueryReceiver(final ExerciseService exerciseService) {
			mExerciseService = new WeakReference<>(exerciseService);
		}

		@Override
		public final void onReceive(final Context context, final Intent intent) {
			if (mExerciseService == null) {
				return;
			}
			ExerciseService exerciseService = mExerciseService.get();
			if (exerciseService != null) {
				synchronized (RUNNING_THREADS) {
					if (RUNNING_THREADS.size() > 0) {
						ExerciseData exerciseData = RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).mExerciseData;
						ExerciseStep exerciseStep = RUNNING_THREADS.get(RUNNING_THREADS.size() - 1).mExerciseStep;
						exerciseService.sendBroadcast(ServiceReceiver.createIntent(exerciseData.getPlayStatus(), exerciseStep, exerciseData));
					}
				}
			}
		}
	}
}
