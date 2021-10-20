package de.jeisfeld.breathcontrol.exercise;

import android.content.Intent;

import de.jeisfeld.breathcontrol.sound.SoundType;

/**
 * Exercise data for simple exercise.
 */
public class SimpleExerciseData extends ExerciseData {
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The breath end duration.
	 */
	private final long mBreathEndDuration;

	/**
	 * Constructor.
	 *
	 * @param repetitions       The number of repetitions.
	 * @param breathDuration    The breath start duration.
	 * @param breathEndDuration The breath end duration.
	 * @param inOutRelation     The in/out relation.
	 * @param soundType         The sound type.
	 */
	public SimpleExerciseData(final Integer repetitions, final Long breathDuration, final Long breathEndDuration,
							  final Double inOutRelation, final SoundType soundType) {
		super(repetitions, breathDuration, inOutRelation, soundType);
		mBreathEndDuration = breathEndDuration;
	}

	/**
	 * Get the breath end duration.
	 *
	 * @return The breath end duration.
	 */
	public long getBreathEndDuration() {
		return mBreathEndDuration;
	}

	@Override
	public final ExerciseType getType() {
		return ExerciseType.SIMPLE;
	}

	@Override
	public final void addToIntent(final Intent serviceIntent) {
		super.addToIntent(serviceIntent);
		serviceIntent.putExtra(EXTRA_BREATH_END_DURATION, mBreathEndDuration);
	}

	@Override
	protected final ExerciseStep[] getStepsForRepetition(final int repetition) {
		long currentBreathDuration = getRepetitions() < 2 ? mBreathEndDuration
				: getBreathDuration() + (mBreathEndDuration - getBreathDuration()) * (repetition - 1) / (getRepetitions() - 1);

		return new ExerciseStep[]{
				new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation())),
				new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())))
		};
	}
}
