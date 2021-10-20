package de.jeisfeld.breathcontrol.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import java.io.IOException;

import de.jeisfeld.breathcontrol.Application;
import de.jeisfeld.breathcontrol.util.Logger;

/**
 * A singleton media player used by the app.
 */
public class MediaPlayer extends android.media.MediaPlayer {
	/**
	 * The media player instance.
	 */
	private static MediaPlayer mInstance = null;

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
	 */
	public static synchronized void releaseInstance() {
		if (mInstance != null) {
			mInstance.release();
			mInstance = null;
		}
	}

	/**
	 * Play a sound resource.
	 *
	 * @param context The context.
	 * @param resourceId The sound resource.
	 */
	public void play(final Context context, final int resourceId) {
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

		start();
	}

	/**
	 * Stop the player.
	 */
	public void stop() {
		if (isPlaying()) {
			super.stop();
		}
	}
}
