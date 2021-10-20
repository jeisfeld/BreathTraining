package de.jeisfeld.breathcontrol.exercise;

import android.content.Intent;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

import de.jeisfeld.breathcontrol.sound.SoundType;
import de.jeisfeld.breathcontrol.util.Logger;

/**
 * Exercise data for hold exercise.
 */
public class HoldExerciseData extends ExerciseData {
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The hold start duration.
	 */
	private final long mHoldStartDuration;
	/**
	 * The hold start duration.
	 */
	private final long mHoldEndDuration;
	/**
	 * The hold position.
	 */
	private final HoldPosition mHoldPosition;
	/**
	 * The hold variation.
	 */
	private final double mHoldVariation;
	/**
	 * A random stream of doubles.
	 */
	private final Iterator<Double> mDoubleIterator = new Random().doubles().iterator();

	/**
	 * Constructor.
	 *
	 * @param repetitions       The number of repetitions
	 * @param breathDuration    The breath duration
	 * @param inOutRelation     The in/out relation
	 * @param holdStartDuration The hold start duration
	 * @param holdEndDuration   The hold end duration
	 * @param holdPosition      The hold position
	 * @param holdVariation     The hold variation
	 * @param soundType         The sound type
	 */
	public HoldExerciseData(final Integer repetitions, final Long breathDuration, final Double inOutRelation, // SUPPRESS_CHECKSTYLE
							final Long holdStartDuration, final Long holdEndDuration, final HoldPosition holdPosition, final Double holdVariation,
							final SoundType soundType) {
		super(repetitions, breathDuration, inOutRelation, soundType);
		mHoldStartDuration = holdStartDuration;
		mHoldEndDuration = holdEndDuration;
		mHoldPosition = holdPosition;
		mHoldVariation = holdVariation;
	}

	/**
	 * Get the hold start duration.
	 *
	 * @return The hold start duration
	 */
	public long getHoldStartDuration() {
		return mHoldStartDuration;
	}

	/**
	 * Get the hold end duration.
	 *
	 * @return The hold end duration
	 */
	public long getHoldEndDuration() {
		return mHoldEndDuration;
	}

	/**
	 * Get the hold position.
	 *
	 * @return The hold position
	 */
	public HoldPosition getHoldPosition() {
		return mHoldPosition;
	}

	/**
	 * Get the hold variation.
	 *
	 * @return The hold variation
	 */
	public double getHoldVariation() {
		return mHoldVariation;
	}

	@Override
	public final ExerciseType getType() {
		return ExerciseType.HOLD;
	}

	@Override
	public final void addToIntent(final Intent serviceIntent) {
		super.addToIntent(serviceIntent);
		serviceIntent.putExtra(EXTRA_HOLD_START_DURATION, mHoldStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_END_DURATION, mHoldEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_POSITION, mHoldPosition);
		serviceIntent.putExtra(EXTRA_HOLD_VARIATION, mHoldVariation);
	}

	/**
	 * Get the hold duration for a certain repetition.
	 * @param repetition The repetition
	 * @return The hold duration
	 */
	private long getHoldDuration(final int repetition) {
		long holdDuration = getRepetitions() < 2 ? mHoldEndDuration
				: mHoldStartDuration + (mHoldEndDuration - mHoldStartDuration) * (repetition - 1) / (getRepetitions() - 1);
		double variation = (mDoubleIterator.next() * 2 - 1) * mHoldVariation;
		holdDuration += holdDuration * variation;
		return holdDuration;
	}

	@Override
	protected ExerciseStep[] getStepsForRepetition(int repetition) {
		switch (mHoldPosition) {
		case IN:
			return new ExerciseStep[]{
					new ExerciseStep(StepType.INHALE, (long) (getBreathDuration() * getInOutRelation())),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition)),
					new ExerciseStep(StepType.EXHALE, (long) (getBreathDuration() * (1 - getInOutRelation())))
			};
		case OUT:
			return new ExerciseStep[]{
					new ExerciseStep(StepType.INHALE, (long) (getBreathDuration() * getInOutRelation())),
					new ExerciseStep(StepType.EXHALE, (long) (getBreathDuration() * (1 - getInOutRelation()))),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition))
			};
		case BOTH:
			return new ExerciseStep[]{
					new ExerciseStep(StepType.INHALE, (long) (getBreathDuration() * getInOutRelation())),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition) / 2),
					new ExerciseStep(StepType.EXHALE, (long) (getBreathDuration() * (1 - getInOutRelation()))),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition) / 2),
			};
		default:
			return null;
		}
	}
}
