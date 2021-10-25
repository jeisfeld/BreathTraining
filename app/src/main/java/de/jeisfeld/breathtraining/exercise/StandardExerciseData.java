package de.jeisfeld.breathtraining.exercise;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * Exercise data for hold exercise.
 */
public class StandardExerciseData extends ExerciseData {
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
	 * The hold breath in flag.
	 */
	private final boolean mHoldBreathIn;
	/**
	 * The hold in start duration.
	 */
	private final long mHoldInStartDuration;
	/**
	 * The hold in end duration.
	 */
	private final long mHoldInEndDuration;
	/**
	 * The hold in position.
	 */
	private final HoldPosition mHoldInPosition;
	/**
	 * The hold breath out flag.
	 */
	private final boolean mHoldBreathOut;
	/**
	 * The hold out start duration.
	 */
	private final long mHoldOutStartDuration;
	/**
	 * The hold out end duration.
	 */
	private final long mHoldOutEndDuration;
	/**
	 * The hold out position.
	 */
	private final HoldPosition mHoldOutPosition;
	/**
	 * The hold variation.
	 */
	private final double mHoldVariation;

	/**
	 * Constructor.
	 *
	 * @param repetitions             The number of repetitions
	 * @param breathStartDuration     The breath duration
	 * @param breathEndDuration       The breath end duration.
	 * @param inOutRelation           The in/out relation
	 * @param holdBreathIn            The hold breath in flag
	 * @param holdInStartDuration     The hold in start duration
	 * @param holdInEndDuration       The hold in end duration
	 * @param holdInPosition          The hold in position
	 * @param holdBreathOut           The hold breath out flag
	 * @param holdOutStartDuration    The hold out start duration
	 * @param holdOutEndDuration      The hold out end duration
	 * @param holdOutPosition         The hold out position
	 * @param holdVariation           The hold variation
	 * @param soundType               The sound type
	 * @param playStatus              The playing status
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public StandardExerciseData(final Integer repetitions, final Long breathStartDuration, final Long breathEndDuration, // SUPPRESS_CHECKSTYLE
								final Double inOutRelation, final Boolean holdBreathIn, final Long holdInStartDuration, final Long holdInEndDuration,
								final HoldPosition holdInPosition, final Boolean holdBreathOut, final Long holdOutStartDuration,
								final Long holdOutEndDuration, final HoldPosition holdOutPosition, final Double holdVariation,
								final SoundType soundType, final PlayStatus playStatus, final int currentRepetitionNumber) {
		super(repetitions, breathStartDuration, soundType, playStatus, currentRepetitionNumber);
		mBreathEndDuration = breathEndDuration;
		mInOutRelation = inOutRelation;
		mHoldBreathIn = holdBreathIn;
		mHoldInStartDuration = holdInStartDuration;
		mHoldInEndDuration = holdInEndDuration;
		mHoldInPosition = holdInPosition;
		mHoldBreathOut = holdBreathOut;
		mHoldOutStartDuration = holdOutStartDuration;
		mHoldOutEndDuration = holdOutEndDuration;
		mHoldOutPosition = holdOutPosition;
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
	 * Get the hold breath in flag.
	 *
	 * @return The hold breath in flag.
	 */
	public boolean isHoldBreathIn() {
		return mHoldBreathIn;
	}

	/**
	 * Get the hold in start duration.
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
	 * Get the hold in position.
	 *
	 * @return The hold in position.
	 */
	public HoldPosition getHoldInPosition() {
		return mHoldInPosition;
	}

	/**
	 * Get the hold breath out flag.
	 *
	 * @return The hold breath out flag.
	 */
	public boolean isHoldBreathOut() {
		return mHoldBreathOut;
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
	 * Get the hold out position.
	 *
	 * @return The hold out position.
	 */
	public HoldPosition getHoldOutPosition() {
		return mHoldOutPosition;
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
		serviceIntent.putExtra(EXTRA_HOLD_BREATH_IN, mHoldBreathIn);
		serviceIntent.putExtra(EXTRA_HOLD_IN_START_DURATION, mHoldInStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_IN_END_DURATION, mHoldInEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_IN_POSITION, mHoldInPosition);
		serviceIntent.putExtra(EXTRA_HOLD_BREATH_OUT, mHoldBreathOut);
		serviceIntent.putExtra(EXTRA_HOLD_OUT_START_DURATION, mHoldOutStartDuration);
		serviceIntent.putExtra(EXTRA_HOLD_OUT_END_DURATION, mHoldOutEndDuration);
		serviceIntent.putExtra(EXTRA_HOLD_OUT_POSITION, mHoldOutPosition);
		serviceIntent.putExtra(EXTRA_HOLD_VARIATION, mHoldVariation);
	}

	/**
	 * Calculate the duration for a certain repetition.
	 *
	 * @param startDuration The start duration
	 * @param endDuration The end duration
	 * @param repetition The repetition number
	 * @return The repetition duration
	 */
	private long calculateDuration(final long startDuration, final long endDuration, final int repetition) {
		return getRepetitions() < 2 ? endDuration : startDuration + (endDuration - startDuration) * (repetition - 1) / (getRepetitions() - 1);
	}

	@Override
	protected final ExerciseStep[] getStepsForRepetition(final int repetition) {
		long currentBreathDuration = calculateDuration(getBreathStartDuration(), mBreathEndDuration, repetition);
		List<ExerciseStep> exerciseSteps = new ArrayList<>();

		ExerciseStep inhaleStep = new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()), repetition);
		if (mHoldBreathIn) {
			long holdInDuration = calculateDuration(mHoldInStartDuration, mHoldInEndDuration, repetition);
			exerciseSteps.addAll(mHoldInPosition.applyHold(inhaleStep, holdInDuration, mHoldVariation));
		}
		else {
			exerciseSteps.add(inhaleStep);
		}

		ExerciseStep exhaleStep = new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())), repetition);
		if (mHoldBreathOut) {
			long holdOutDuration = calculateDuration(mHoldOutStartDuration, mHoldOutEndDuration, repetition);
			exerciseSteps.addAll(mHoldOutPosition.applyHold(exhaleStep, holdOutDuration, mHoldVariation));
		}
		else {
			exerciseSteps.add(exhaleStep);
		}

		return exerciseSteps.toArray(new ExerciseStep[0]);
	}
}
