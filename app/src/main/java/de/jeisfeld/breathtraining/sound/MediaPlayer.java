package de.jeisfeld.breathtraining.sound;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import de.jeisfeld.breathtraining.Application;
import de.jeisfeld.breathtraining.exercise.StepType;

/**
 * A singleton media player used by the app.
 */
public class MediaPlayer extends android.media.MediaPlayer {
	/**
	 * The media player instance.
	 */
	private static MediaPlayer mInstance = null;
	/**
	 * The triggerer of the media play.
	 */
	private MediaTrigger mTrigger;

	/**
	 * Get the media player singleton.
	 *
	 * @return The media player singleton.
	 */
	public static synchronized MediaPlayer getInstance() {
		if (mInstance == null) {
			mInstance = new MediaPlayer();
		}
		return mInstance;
	}

	/**
	 * Release the media player instance.
	 *
	 * @param trigger The triggerer of the media play.
	 */
	public static synchronized void releaseInstance(final MediaTrigger trigger) {
		if (mInstance != null && (trigger == null || trigger == mInstance.mTrigger)) {
			mInstance.release();
			mInstance = null;
		}
	}

	/**
	 * Play the sound for a certain step.
	 *
	 * @param context The context.
	 * @param trigger The triggerer of the media play.
	 * @param soundType The sound type.
	 * @param stepType The step type.
	 * @param delay A delay in ms.
	 */
	public void play(final Context context, final MediaTrigger trigger, final SoundType soundType, final StepType stepType, final long delay) {
		play(context, trigger, soundType.getSoundResource(stepType), delay);
	}

	/**
	 * Play a sound resource.
	 *
	 * @param context The context.
	 * @param resourceId The sound resource.
	 * @param trigger The trigger of the audio playing.
	 * @param delay A delay in ms.
	 */
	public synchronized void play(final Context context, final MediaTrigger trigger, final int resourceId, final long delay) {
		final long startTimeStamp = System.currentTimeMillis();
		mTrigger = trigger;
		stop();
		reset();
		if (context == null || resourceId == 0) {
			return;
		}

		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(resourceId);
			if (afd == null) {
				return;
			}
			setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			prepare();
		}
		catch (IOException | IllegalArgumentException | SecurityException ex) {
			Log.e(Application.TAG, "Failed to open sound resource", ex);
			return;
		}
		long passedTime = System.currentTimeMillis() - startTimeStamp;
		if (passedTime < delay) {
			try {
				Thread.sleep(delay - passedTime);
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
		start();
	}

	/**
	 * Stop the player.
	 */
	@Override
	public void stop() {
		if (isPlaying()) {
			super.stop();
		}
	}
}
