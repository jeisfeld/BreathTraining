package de.jeisfeld.breathcontrol.sound;

import de.jeisfeld.breathcontrol.R;

/**
 * The type of sound to be played.
 */
public enum SoundType {
	/**
	 * No sound.
	 */
	NONE(0, 0),
	/**
	 * Words.
	 */
	WORDS (R.raw.inhale, R.raw.exhale),
	/**
	 * Breath.
	 */
	BREATH (R.raw.in, R.raw.out);

	/**
	 * The sound resource for inhale.
	 */
	private final int mInhaleResource;
	/**
	 * The sound resource for exhale.
	 */
	private final int mExhaleResource;

	/**
	 * Constructor.
	 * @param inhaleResource The inhale resource.
	 * @param exhaleResource The exhale resource.
	 */
	SoundType(final int inhaleResource, final int exhaleResource) {
			mInhaleResource = inhaleResource;
			mExhaleResource = exhaleResource;
	}

	/**
	 * Get the inhale sound resource.
	 * @return The inhale sound resource
	 */
	public int getInhaleResource() {
		return mInhaleResource;
	}

	/**
	 * Get the exhale sound resource.
	 * @return The exhale sound resource
	 */
	public int getExhaleResource() {
		return mExhaleResource;
	}

}
