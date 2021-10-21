package de.jeisfeld.breathcontrol.exercise;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import de.jeisfeld.breathcontrol.MainActivity;
import de.jeisfeld.breathcontrol.R;
import de.jeisfeld.breathcontrol.sound.MediaPlayer;
import de.jeisfeld.breathcontrol.sound.MediaTrigger;
import de.jeisfeld.breathcontrol.ui.home.ServiceReceiver;

/**
 * A service handling Exercises in the background.
 */
public class ExerciseService extends Service {
	/**
	 * The id for the service.
	 */
	private static final int SERVICE_ID = 1;
	/**
	 * The request code for the main notification.
	 */
	private static final int REQUEST_CODE_START_APP = 1;
	/**
	 * Intent key for the service command.
	 */
	private static final String EXTRA_SERVICE_COMMAND = "de.jeisfeld.breathcontrol.SERVICE_COMMAND";
	/**
	 * The id of the notification channel.
	 */
	public static final String CHANNEL_ID = "BreathControlChannel";
	/**
	 * The wait duration at the end, before closing.
	 */
	private static final long END_WAIT_DURATION = 2000;
	/**
	 * Delay time to allow sound pre-preparation.
	 */
	private static final long SOUND_PREPARE_DELAY = 100;

	/**
	 * The running threads.
	 */
	private final List<Thread> mRunningThreads = new ArrayList<>();
	/**
	 * Flag indicating if exercise is pausing.
	 */
	private boolean mIsPausing = false;
	/**
	 * Flag indicating if exercise is skipping to next breath.
	 */
	private boolean mIsSkipping = false;

	/**
	 * Trigger the exercise service.
	 *
	 * @param context The context.
	 * @param serviceCommand The service command.
	 * @param exerciseData The exercise data.
	 */
	public static void triggerExerciseService(final Context context, final ServiceCommand serviceCommand, final ExerciseData exerciseData) {
		Intent serviceIntent = new Intent(context, ExerciseService.class);
		serviceIntent.putExtra(EXTRA_SERVICE_COMMAND, serviceCommand);
		exerciseData.addToIntent(serviceIntent);
		ContextCompat.startForegroundService(context, serviceIntent);
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		createNotificationChannel();
	}

	@Override
	public final int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent == null) {
			return START_REDELIVER_INTENT;
		}

		final ServiceCommand serviceCommand = (ServiceCommand) intent.getSerializableExtra(EXTRA_SERVICE_COMMAND);
		final ExerciseData exerciseData = ExerciseData.fromIntent(intent);
		assert exerciseData != null;
		startNotification(exerciseData, null);

		switch (serviceCommand) {
		case START:
			Thread newThread = getDeviceAnimationThread(exerciseData);

			synchronized (mRunningThreads) {
				mIsPausing = false;
				mIsSkipping = false;
				mRunningThreads.notifyAll();
				if (mRunningThreads.size() > 0) {
					mRunningThreads.get(0).interrupt();
				}
				mRunningThreads.add(newThread);
			}
			newThread.start();
			return START_STICKY;
		case STOP:
			synchronized (mRunningThreads) {
				mIsPausing = false;
				mIsSkipping = false;
				mRunningThreads.notifyAll();
				if (mRunningThreads.size() > 0) {
					mRunningThreads.get(0).interrupt();
				}
			}
			return START_STICKY;
		case PAUSE:
			synchronized (mRunningThreads) {
				mIsPausing = true;
				mIsSkipping = true;
			}
			return START_STICKY;
		case RESUME:
			synchronized (mRunningThreads) {
				mIsPausing = false;
				mRunningThreads.notifyAll();
			}
			return START_STICKY;
		case NEXT:
			synchronized (mRunningThreads) {
				if (mRunningThreads.size() > 0) {
					mIsSkipping = true;
					mRunningThreads.get(0).interrupt();
				}
			}
			return START_STICKY;
		default:
			return START_STICKY;
		}
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
	}

	@Override
	public final IBinder onBind(final Intent intent) {
		return null;
	}

	/**
	 * Get an exercise thread.
	 *
	 * @param exerciseData The animation data.
	 * @return The exercise thread.
	 */
	private Thread getDeviceAnimationThread(final ExerciseData exerciseData) {
		return new Thread() {
			@Override
			public void run() {
				final WakeLock wakeLock = acquireWakelock(this);
				long nextDelay = SOUND_PREPARE_DELAY;

				ExerciseStep exerciseStep = exerciseData.getNextStep();
				while (exerciseStep != null) {
					if (!(mIsSkipping && exerciseStep.getStepType() == StepType.HOLD)) {
						// Execute the step, except in case of hold while skipping
						mIsSkipping = false;
						MediaPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE,
								exerciseData.getSoundType(), exerciseStep.getStepType(), nextDelay);
						sendBroadcast(ServiceReceiver.createIntent(exerciseStep));
						startNotification(exerciseData, exerciseStep);
						try {
							if (exerciseStep.getDuration() > SOUND_PREPARE_DELAY) {
								Thread.sleep(exerciseStep.getDuration() - SOUND_PREPARE_DELAY);
								nextDelay = SOUND_PREPARE_DELAY;
							}
							else {
								nextDelay = exerciseStep.getDuration();
							}

							synchronized (mRunningThreads) {
								if (mIsPausing) {
									mRunningThreads.wait();
								}
							}
						}
						catch (InterruptedException e) {
							if (!mIsSkipping) {
								updateOnEndExercise(wakeLock, exerciseData, this);
								return;
							}
						}
					}
					exerciseStep = exerciseData.getNextStep();
				}

				try {
					MediaPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE, exerciseData.getSoundType(),
							StepType.RELAX, nextDelay);
					Thread.sleep(END_WAIT_DURATION);
				}
				catch (InterruptedException e) {
					// Ignore
				}
				updateOnEndExercise(wakeLock, exerciseData, this);
			}
		};
	}

	/**
	 * Get a wakelock and acquire it.
	 *
	 * @param thread The thread aquiring the wakelock.
	 * @return The wakelock.
	 */
	@SuppressLint("WakelockTimeout")
	private WakeLock acquireWakelock(final Thread thread) {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		assert powerManager != null;
		WakeLock wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "de.jeisfeld.breathcontrol:" + thread.hashCode());
		wakelock.acquire();
		return wakelock;
	}

	/**
	 * Create the channel for service animation notifications.
	 */
	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel animationChannel = new NotificationChannel(
					CHANNEL_ID, getString(R.string.notification_channel), NotificationManager.IMPORTANCE_DEFAULT);
			NotificationManager manager = getSystemService(NotificationManager.class);
			assert manager != null;
			manager.createNotificationChannel(animationChannel);
		}
	}

	/**
	 * Start the notification.
	 *
	 * @param exerciseData The exercise data.
	 * @param exerciseStep The current exercise step.
	 */
	private void startNotification(final ExerciseData exerciseData, final ExerciseStep exerciseStep) {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		exerciseData.addToIntent(notificationIntent);
		notificationIntent.putExtra(ServiceReceiver.EXTRA_EXERCISE_STEP, exerciseStep);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				REQUEST_CODE_START_APP, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setContentTitle(getString(R.string.notification_title_exercise))
				.setContentText(getString(
						exerciseStep == null ? R.string.notification_text_exercise_running : exerciseStep.getStepType().getDisplayResource()))
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.mipmap.ic_launcher)
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
		synchronized (mRunningThreads) {
			mRunningThreads.remove(thread);
			if (mRunningThreads.size() == 0) {
				MediaPlayer.releaseInstance(MediaTrigger.SERVICE);
				stopService(new Intent(this, ExerciseService.class));
				sendBroadcast(ServiceReceiver.createIntent(PlayStatus.STOPPED));
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
		START,
		/**
		 * Stop.
		 */
		STOP,
		/**
		 * Pause.
		 */
		PAUSE,
		/**
		 * Resume.
		 */
		RESUME,
		/**
		 * Next step.
		 */
		NEXT
	}
}
