package de.jeisfeld.breathtraining.exercise.data;

import java.io.Serializable;

/**
 * An exercise step.
 */
public class ExerciseStep implements Serializable {
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The step type.
	 */
	private final StepType mStepType;
	/**
	 * The step duration.
	 */
	private final long mDuration;
	/**
	 * The sound duration.
	 */
	private final long mSoundDuration;
	/**
	 * The repetition data.
	 */
	private final RepetitionData mRepetition;

	/**
	 * Constructor.
	 *
	 * @param stepType      The step type.
	 * @param duration      The duration.
	 * @param soundDuration The sound duration.
	 * @param repetition    The repetition data.
	 */
	public ExerciseStep(final StepType stepType, final long duration, final long soundDuration, final RepetitionData repetition) {
		mStepType = stepType;
		mDuration = duration;
		mSoundDuration = soundDuration;
		mRepetition = repetition;
	}

	/**
	 * Constructor.
	 *
	 * @param stepType   The step type.
	 * @param duration   The duration.
	 * @param repetition The repetition data.
	 */
	public ExerciseStep(final StepType stepType, final long duration, final RepetitionData repetition) {
		this(stepType, duration, duration, repetition);
	}

	/**
	 * Get the step type.
	 *
	 * @return The step type.
	 */
	public StepType getStepType() {
		return mStepType;
	}

	/**
	 * Get the duration.
	 *
	 * @return The duration.
	 */
	public long getDuration() {
		return mDuration;
	}

	/**
	 * Get the sound duration.
	 *
	 * @return The sound duration.
	 */
	public long getSoundDuration() {
		return mSoundDuration;
	}

	/**
	 * Get the repetition number.
	 *
	 * @return The repetition number.
	 */
	public RepetitionData getRepetition() {
		return mRepetition;
	}

}
