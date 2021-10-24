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
	 * The hold breath flag.
	 */
	private final boolean mHoldBreath;
	/**
	 * The hold in start duration.
	 */
	private final long mHoldInStartDuration;
	/**
	 * The hold in end duration.
	 */
	private final long mHoldInEndDuration;
	/**
	 * The hold out start duration.
	 */
	private final long mHoldOutStartDuration;
	/**
	 * The hold out end duration.
	 */
	private final long mHoldOutEndDuration;
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
	 * @param repetitions             The number of repetitions
	 * @param breathStartDuration     The breath duration
	 * @param breathEndDuration       The breath end duration.
	 * @param inOutRelation           The in/out relation
	 * @param holdInStartDuration     The hold in start duration
	 * @param holdInEndDuration       The hold in end duration
	 * @param holdOutStartDuration    The hold out start duration
	 * @param holdOutEndDuration      The hold out end duration
	 * @param holdVariation           The hold variation
	 * @param soundType               The sound type
	 * @param playStatus              The playing status
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public HoldExerciseData(final Integer repetitions, final Long breathStartDuration, final Long breathEndDuration, // SUPPRESS_CHECKSTYLE
							final Double inOutRelation, final Boolean holdBreath, final Long holdInStartDuration, final Long holdInEndDuration,
							final Long holdOutStartDuration, final Long holdOutEndDuration, final Double holdVariation,
							final SoundType soundType, final PlayStatus playStatus, final int currentRepetitionNumber) {
		super(repetitions, breathStartDuration, soundType, playStatus, currentRepetitionNumber);
		mBreathEndDuration = breathEndDuration;
		mInOutRelation = inOutRelation;
		mHoldBreath = holdBreath;
		mHoldInStartDuration = holdInStartDuration;
		mHoldInEndDuration = holdInEndDuration;
		mHoldOutStartDuration = holdOutStartDuration;
		mHoldOutEndDuration = holdOutEndDuration;
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
	 * Get the hold breath flag.
	 *
	 * @return The hold breath flag.
	 */
	public boolean isHoldBreath() {
		return mHoldBreath;
	}

	/**
	 * Get the hold start duration.
	 *
	 * @return The hold in start duration
	 */
	public long getHoldInStartDuration() {
		return mHoldInStartDuration;
	}

	/**
	 * Get the hold in end duration.
	 *
	 * @return The hold in end duration
	 */
	public long getHoldInEndDuration() {
		return mHoldInEndDuration;
	}

	/**
	 * Get the hold out start duration.
	 *
	 * @return The hold out start duration
	 */
	public long getHoldOutStartDuration() {
		return mHoldOutStartDuration;
	}

	/**
	 * Get the hold out end duration.
	 *
	 * @return The hold out end duration
	 */
	public long getHoldOutEndDuration() {
		return mHoldOutEndDuration;
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
		serviceIntent.putExtra(EXTRA_HOLD_BREATH, mHoldBreath);
		serviceIntent.putExtra(EXTRA_HOLD_IN_START_DURATION, mHoldInStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_IN_END_DURATION, mHoldInEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_OUT_START_DURATION, mHoldOutStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_OUT_END_DURATION, mHoldOutEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_VARIATION, mHoldVariation);
	}

	/**
	 * Get the hold in duration for a certain repetition.
	 *
	 * @param repetition The repetition
	 * @return The hold in duration
	 */
	private long getHoldInDuration(final int repetition) {
		long holdDuration = getRepetitions() < 2 ? mHoldInEndDuration
				: mHoldInStartDuration + (mHoldInEndDuration - mHoldInStartDuration) * (repetition - 1) / (getRepetitions() - 1);
		double variation = (mDoubleIterator.next() * 2 - 1) * mHoldVariation;
		holdDuration += holdDuration * variation;
		return holdDuration;
	}

	/**
	 * Get the hold out duration for a certain repetition.
	 *
	 * @param repetition The repetition
	 * @return The hold out duration
	 */
	private long getHoldOutDuration(final int repetition) {
		long holdDuration = getRepetitions() < 2 ? mHoldOutEndDuration
				: mHoldOutStartDuration + (mHoldOutEndDuration - mHoldOutStartDuration) * (repetition - 1) / (getRepetitions() - 1);
		double variation = (mDoubleIterator.next() * 2 - 1) * mHoldVariation;
		holdDuration += holdDuration * variation;
		return holdDuration;
	}

	@Override
	protected final ExerciseStep[] getStepsForRepetition(final int repetition) {
		long currentBreathDuration = getRepetitions() < 2 ? mBreathEndDuration
				: getBreathStartDuration() + (mBreathEndDuration - getBreathStartDuration()) * (repetition - 1) / (getRepetitions() - 1);

		if (mHoldBreath) {
			return new ExerciseStep[]{
					new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition),
					new ExerciseStep(StepType.HOLD, getHoldInDuration(repetition) / 2, repetition),
					new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition),
					new ExerciseStep(StepType.HOLD, getHoldOutDuration(repetition) / 2, repetition),
			};
		}
		else {
			return new ExerciseStep[]{
					new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition),
					new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition),
			};
		}


	}
}
