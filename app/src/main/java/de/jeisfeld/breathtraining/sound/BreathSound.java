package de.jeisfeld.breathtraining.sound;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.StepType;

/**
 * Sounds for playing breath with adjusted length.
 */
public enum BreathSound {
	/**
	 * Inhale of 1s.
	 */
	INHALE_1(StepType.INHALE, 1000, R.raw.br_inhale_1),
	/**
	 * Inhale of 2s.
	 */
	INHALE_2(StepType.INHALE, 2000, R.raw.br_inhale_2),
	/**
	 * Inhale of 4s.
	 */
	INHALE_4(StepType.INHALE, 4000, R.raw.br_inhale_4),
	/**
	 * Inhale of 8s.
	 */
	INHALE_8(StepType.INHALE, 8000, R.raw.br_inhale_8),
	/**
	 * Inhale of 16s.
	 */
	INHALE_16(StepType.INHALE, 16000, R.raw.br_inhale_16),
	/**
	 * Inhale of 32s.
	 */
	INHALE_32(StepType.INHALE, 32000, R.raw.br_inhale_32),
	/**
	 * Inhale of 64s.
	 */
	INHALE_64(StepType.INHALE, 64000, R.raw.br_inhale_64),
	/**
	 * Inhale of 128s.
	 */
	INHALE_128(StepType.INHALE, 128000, R.raw.br_inhale_128),
	/**
	 * Exhale of 1s.
	 */
	EXHALE_1(StepType.EXHALE, 1000, R.raw.br_exhale_1),
	/**
	 * Exhale of 2s.
	 */
	EXHALE_2(StepType.EXHALE, 2000, R.raw.br_exhale_2),
	/**
	 * Exhale of 4s.
	 */
	EXHALE_4(StepType.EXHALE, 4000, R.raw.br_exhale_4),
	/**
	 * Exhale of 8s.
	 */
	EXHALE_8(StepType.EXHALE, 8000, R.raw.br_exhale_8),
	/**
	 * Exhale of 16s.
	 */
	EXHALE_16(StepType.EXHALE, 16000, R.raw.br_exhale_16),
	/**
	 * Exhale of 32s.
	 */
	EXHALE_32(StepType.EXHALE, 32000, R.raw.br_exhale_32),
	/**
	 * Exhale of 64s.
	 */
	EXHALE_64(StepType.EXHALE, 64000, R.raw.br_exhale_64),
	/**
	 * Exhale of 128s.
	 */
	EXHALE_128(StepType.EXHALE, 128000, R.raw.br_exhale_128);

	/**
	 * The factor up to which sounds are stretched instead of shrinked.
	 */
	private static final double STRETCH_FACTOR = 1.4;

	/**
	 * The StepType.
	 */
	private StepType mStepType;
	/**
	 * The duration (in ms).
	 */
	private long mDuration;
	/**
	 * The sound resource id.
	 */
	private int mResourceId;

	/**
	 * Constructor.
	 *
	 * @param stepType The StepType.
	 * @param duration The duration (in ms).
	 * @param resource The sound resource id.
	 */
	BreathSound(final StepType stepType, final long duration, final int resource) {
		mStepType = stepType;
		mDuration = duration;
		mResourceId = resource;
	}

	/**
	 * Get the planned pause duration after the breath.
	 *
	 * @param duration The breath duration.
	 * @return The duration of the pause.
	 */
	private static long getPauseDuration(final long duration) {
		if (duration <= 1000) { // MAGIC_NUMBER
			return 0;
		}
		else if (duration <= 4000) { // MAGIC_NUMBER
			return (duration - 1000) / 10; // MAGIC_NUMBER
		}
		else if (duration <= 39000) { // MAGIC_NUMBER
			return 300 + (duration - 4000) / 50; // MAGIC_NUMBER
		}
		else {
			return 1000; // MAGIC_NUMBER
		}
	}

	/**
	 * Get the breath sounds for a certain stepType in ascending order of duration.
	 *
	 * @param stepType The stepType.
	 * @return The breath sounds for this stepType.
	 */
	private static BreathSound[] getBreathSounds(final StepType stepType) {
		switch (stepType) {
		case INHALE:
			return new BreathSound[] {INHALE_1, INHALE_2, INHALE_4, INHALE_8, INHALE_16, INHALE_32, INHALE_64, INHALE_128};
		case EXHALE:
			return new BreathSound[] {EXHALE_1, EXHALE_2, EXHALE_4, EXHALE_8, EXHALE_16, EXHALE_32, EXHALE_64, EXHALE_128};
		default:
			return null;
		}
	}

	/**
	 * Get the breath sound info for certain stepType and duration.
	 *
	 * @param stepType The step type.
	 * @param duration The duration (in ms).
	 * @return The breath sound to be used.
	 */
	public static BreathSoundInfo getBreathSoundInfo(final StepType stepType, final long duration) {
		BreathSound[] breathSounds = getBreathSounds(stepType);
		if (breathSounds == null || breathSounds.length == 0) {
			return null;
		}
		long soundDuration = duration - getPauseDuration(duration);

		if (soundDuration < breathSounds[0].mDuration / 2) {
			// Max speed increase is doubling.
			return new BreathSoundInfo(breathSounds[0], 2);
		}

		for (BreathSound breathSound : breathSounds) {
			if (soundDuration < breathSound.mDuration * STRETCH_FACTOR) {
				return new BreathSoundInfo(breathSound, breathSound.mDuration / (double) soundDuration);
			}
		}

		// Stretch longest breathSound
		BreathSound breathSound = breathSounds[breathSounds.length - 1];
		return new BreathSoundInfo(breathSound, breathSound.mDuration / (double) soundDuration);
	}

	/**
	 * The required info for playing a breath sound.
	 */
	public static class BreathSoundInfo {
		/**
		 * The breath sound.
		 */
		private final BreathSound mBreathSound;
		/**
		 * The speed.
		 */
		private final double mSpeed;

		/**
		 * Constructor.
		 *
		 * @param breathSound The breath sound.
		 * @param speed The speed.
		 */
		public BreathSoundInfo(final BreathSound breathSound, final double speed) {
			mBreathSound = breathSound;
			mSpeed = speed;
		}

		/**
		 * Get the sound resource id.
		 *
		 * @return The sound resource id.
		 */
		public int getSoundResourceId() {
			return mBreathSound.mResourceId;
		}

		/**
		 * Get the play speed.
		 *
		 * @return The play speed.
		 */
		public float getSpeed() {
			return (float) mSpeed;
		}
	}
}
