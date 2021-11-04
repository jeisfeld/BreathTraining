package de.jeisfeld.breathtraining.sound;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.StepType;

/**
 * The type of sound to be played.
 */
public enum SoundType {
	/**
	 * No sound.
	 */
	NONE(0, 0, 0, 0, 0),
	/**
	 * Words.
	 */
	WORDS(R.raw.a_inhale, R.raw.a_exhale, R.raw.a_hold, R.raw.a_relax, 2000),
	/**
	 * Breath.
	 */
	BREATH(R.raw.br_inhale, R.raw.br_exhale, R.raw.br_hold, R.raw.br_relax, 6000);

	/**
	 * The sound resource for inhale.
	 */
	private final int mInhaleResource;
	/**
	 * The sound resource for exhale.
	 */
	private final int mExhaleResource;
	/**
	 * The sound resource for hold.
	 */
	private final int mHoldResource;
	/**
	 * The sound resource for relax.
	 */
	private final int mRelaxResource;
	/**
	 * The relax duration.
	 */
	private final long mRelaxDuration;

	/**
	 * Constructor.
	 *
	 * @param inhaleResource The inhale resource.
	 * @param exhaleResource The exhale resource.
	 * @param holdResource   The hold resource.
	 * @param relaxResource  The relax resource.
	 * @param relaxDuration  The relax duration.
	 */
	SoundType(final int inhaleResource, final int exhaleResource, final int holdResource, final int relaxResource, final long relaxDuration) {
		mInhaleResource = inhaleResource;
		mExhaleResource = exhaleResource;
		mHoldResource = holdResource;
		mRelaxResource = relaxResource;
		mRelaxDuration = relaxDuration;
	}

	/**
	 * Get the sound resource for a certain step type.
	 *
	 * @param stepType The step type.
	 * @return The corresponding sound resource.
	 */
	public int getSoundResource(final StepType stepType) {
		switch (stepType) {
		case INHALE:
		case CONTINUE_INHALE:
			return mInhaleResource;
		case EXHALE:
		case CONTINUE_EXHALE:
			return mExhaleResource;
		case HOLD:
			return mHoldResource;
		case RELAX:
			return mRelaxResource;
		default:
			return 0;
		}
	}

	/**
	 * Get the relax duration.
	 *
	 * @return The relax duration.
	 */
	public long getRelaxDuration() {
		return mRelaxDuration;
	}
}
