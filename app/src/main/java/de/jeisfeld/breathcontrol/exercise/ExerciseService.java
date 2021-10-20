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
	 * The id of the notification channel.
	 */
	public static final String CHANNEL_ID = "BreathControlChannel";
	/**
	 * The wait duration at the end, before closing.
	 */
	private static final long END_WAIT_DURATION = 2000;
	/**
	 * The running threads.
	 */
	private final List<Thread> mRunningThreads = new ArrayList<>();

	/**
	 * Trigger the exercise service.
	 *
	 * @param context The context.
	 * @param exerciseData The exercise data.
	 */
	public static void triggerAnimationService(final Context context, final ExerciseData exerciseData) {
		Intent serviceIntent = new Intent(context, ExerciseService.class);
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

		final ExerciseData exerciseData = ExerciseData.fromIntent(intent);
		assert exerciseData != null;

		startNotification(exerciseData);

		Thread newThread = getDeviceAnimationThread(exerciseData);

		synchronized (mRunningThreads) {
			if (mRunningThreads.size() > 0) {
				mRunningThreads.get(0).interrupt();
			}
			mRunningThreads.add(newThread);
		}
		newThread.start();
		return START_STICKY;
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

				ExerciseStep exerciseStep = exerciseData.getNextStep();
				while (exerciseStep != null) {
					MediaPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE,
							exerciseData.getSoundType(), exerciseStep.getStepType());
					try {
						Thread.sleep(exerciseStep.getDuration());
						exerciseStep = exerciseData.getNextStep();
					}
					catch (InterruptedException e) {
						updateOnEndExercise(wakeLock, exerciseData, this);
						return;
					}
				}

				try {
					MediaPlayer.getInstance().play(ExerciseService.this, MediaTrigger.SERVICE, exerciseData.getSoundType(), StepType.RELAX);
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
	 */
	private void startNotification(final ExerciseData exerciseData) {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		exerciseData.addToIntent(notificationIntent);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				REQUEST_CODE_START_APP, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setContentTitle(getString(R.string.notification_title_exercise))
				.setContentText(getString(R.string.notification_text_exercise_running))
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
			}
		}
	}
}
