package de.jeisfeld.breathtraining.exercise;

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
	 * The repetition number.
	 */
	private final int mRepetition;

	/**
	 * Constructor.
	 *
	 * @param stepType The step type.
	 * @param duration The duration.
	 * @param repetition The repetition number.
	 */
	public ExerciseStep(final StepType stepType, final long duration, final int repetition) {
		mStepType = stepType;
		mDuration = duration;
		mRepetition = repetition;
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
	 * Get the repetition number.
	 *
	 * @return The repetition number.
	 */
	public int getRepetition() {
		return mRepetition;
	}
}
