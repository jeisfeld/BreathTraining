package de.jeisfeld.breathtraining.exercise;

import java.util.Iterator;
import java.util.Random;

import android.content.Intent;
import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * Exercise data for hold exercise.
 */
public class HoldExerciseData extends ExerciseData {
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The breath end duration.
	 */
	private final long mBreathEndDuration;
	/**
	 * The in/out relation.
	 */
	private final double mInOutRelation;
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
	private final transient Iterator<Double> mDoubleIterator = new Random().doubles().iterator();

	/**
	 * Constructor.
	 *
	 * @param repetitions The number of repetitions
	 * @param breathStartDuration The breath duration
	 * @param breathEndDuration The breath end duration.
	 * @param inOutRelation The in/out relation
	 * @param holdStartDuration The hold start duration
	 * @param holdEndDuration The hold end duration
	 * @param holdPosition The hold position
	 * @param holdVariation The hold variation
	 * @param soundType The sound type
	 * @param playStatus The playing status
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public HoldExerciseData(final Integer repetitions, final Long breathStartDuration, final Long breathEndDuration, // SUPPRESS_CHECKSTYLE
							final Double inOutRelation, final Long holdStartDuration, final Long holdEndDuration, final HoldPosition holdPosition,
							final Double holdVariation, final SoundType soundType, final PlayStatus playStatus, final int currentRepetitionNumber) {
		super(repetitions, breathStartDuration, soundType, playStatus, currentRepetitionNumber);
		mBreathEndDuration = breathEndDuration;
		mInOutRelation = inOutRelation;
		mHoldStartDuration = holdStartDuration;
		mHoldEndDuration = holdEndDuration;
		mHoldPosition = holdPosition;
		mHoldVariation = holdVariation;
	}

	/**
	 * Get the breath end duration.
	 *
	 * @return The breath end duration
	 */
	public long getBreathEndDuration() {
		return mBreathEndDuration;
	}
	/**
	 * Get the in/out relation.
	 *
	 * @return The in/out relation.
	 */
	public double getInOutRelation() {
		return mInOutRelation;
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
		return ExerciseType.STANDARD;
	}

	@Override
	public final void addToIntent(final Intent serviceIntent) {
		super.addToIntent(serviceIntent);
		serviceIntent.putExtra(EXTRA_BREATH_END_DURATION, mBreathEndDuration);
		serviceIntent.putExtra(EXTRA_IN_OUT_RELATION, mInOutRelation);
		serviceIntent.putExtra(EXTRA_HOLD_START_DURATION, mHoldStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_END_DURATION, mHoldEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_POSITION, mHoldPosition);
		serviceIntent.putExtra(EXTRA_HOLD_VARIATION, mHoldVariation);
	}

	/**
	 * Get the hold duration for a certain repetition.
	 *
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
	protected final ExerciseStep[] getStepsForRepetition(final int repetition) {
		long currentBreathDuration = getRepetitions() < 2 ? mBreathEndDuration
				: getBreathStartDuration() + (mBreathEndDuration - getBreathStartDuration()) * (repetition - 1) / (getRepetitions() - 1);

		switch (mHoldPosition) {
		case IN:
			return new ExerciseStep[] {
					new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition), repetition),
					new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition)
			};
		case OUT:
			return new ExerciseStep[] {
					new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition),
					new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition), repetition)
			};
		case BOTH:
			return new ExerciseStep[] {
					new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition) / 2, repetition),
					new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition),
					new ExerciseStep(StepType.HOLD, getHoldDuration(repetition) / 2, repetition),
			};
		default:
			return null;
		}
	}
}
