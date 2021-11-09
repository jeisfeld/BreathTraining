package de.jeisfeld.breathtraining.exercise.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Exercise data for standard breath exercise.
 */
public class StandardExerciseData extends SingleExerciseData {
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
	 * @param name                    The name of the exercise
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
	public StandardExerciseData(final String name, final Integer repetitions, final Long breathStartDuration, // SUPPRESS_CHECKSTYLE
								final Long breathEndDuration, final Double inOutRelation, final Boolean holdBreathIn, final Long holdInStartDuration,
								final Long holdInEndDuration, final HoldPosition holdInPosition, final Boolean holdBreathOut,
								final Long holdOutStartDuration, final Long holdOutEndDuration, final HoldPosition holdOutPosition,
								final Double holdVariation, final SoundType soundType, final PlayStatus playStatus,
								final int currentRepetitionNumber) {
		super(name, repetitions, breathStartDuration, soundType, playStatus, currentRepetitionNumber);
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
	public final boolean store(final String name) {
		final boolean isNew = super.store(name);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_breath_end_duration, getId(), mBreathEndDuration);
		PreferenceUtil.setIndexedSharedPreferenceDouble(R.string.key_stored_in_out_relation, getId(), mInOutRelation);
		PreferenceUtil.setIndexedSharedPreferenceBoolean(R.string.key_stored_hold_breath_in, getId(), mHoldBreathIn);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_hold_in_start_duration, getId(), mHoldInStartDuration);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_hold_in_end_duration, getId(), mHoldInEndDuration);
		PreferenceUtil.setIndexedSharedPreferenceInt(R.string.key_stored_hold_in_position, getId(), mHoldInPosition.ordinal());
		PreferenceUtil.setIndexedSharedPreferenceBoolean(R.string.key_stored_hold_breath_out, getId(), mHoldBreathOut);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_hold_out_start_duration, getId(), mHoldOutStartDuration);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_hold_out_end_duration, getId(), mHoldOutEndDuration);
		PreferenceUtil.setIndexedSharedPreferenceInt(R.string.key_stored_hold_out_position, getId(), mHoldOutPosition.ordinal());
		PreferenceUtil.setIndexedSharedPreferenceDouble(R.string.key_stored_hold_variation, getId(), mHoldVariation);
		return isNew;
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
		if (repetition > getRepetitions()) {
			return null;
		}
		long currentBreathDuration = calculateDuration(getBreathStartDuration(), mBreathEndDuration, repetition);
		List<ExerciseStep> exerciseSteps = new ArrayList<>();

		ExerciseStep inhaleStep = new ExerciseStep(StepType.INHALE, (long) (currentBreathDuration * getInOutRelation()),
				new RepetitionData(repetition, getRepetitions()));
		if (mHoldBreathIn) {
			long holdInDuration = calculateDuration(mHoldInStartDuration, mHoldInEndDuration, repetition);
			exerciseSteps.addAll(mHoldInPosition.applyHold(inhaleStep, holdInDuration, mHoldVariation));
		}
		else {
			exerciseSteps.add(inhaleStep);
		}

		ExerciseStep exhaleStep = new ExerciseStep(StepType.EXHALE, (long) (currentBreathDuration * (1 - getInOutRelation())),
				new RepetitionData(repetition, getRepetitions()));
		if (mHoldBreathOut) {
			long holdOutDuration = calculateDuration(mHoldOutStartDuration, mHoldOutEndDuration, repetition);
			exerciseSteps.addAll(mHoldOutPosition.applyHold(exhaleStep, holdOutDuration, mHoldVariation));
		}
		else {
			exerciseSteps.add(exhaleStep);
		}

		return exerciseSteps.toArray(new ExerciseStep[0]);
	}

	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StandardExerciseData)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		StandardExerciseData that = (StandardExerciseData) o;
		return mBreathEndDuration == that.mBreathEndDuration && Double.compare(that.mInOutRelation, mInOutRelation) == 0
				&& mHoldBreathIn == that.mHoldBreathIn && mHoldInStartDuration == that.mHoldInStartDuration
				&& mHoldInEndDuration == that.mHoldInEndDuration && mHoldBreathOut == that.mHoldBreathOut
				&& mHoldOutStartDuration == that.mHoldOutStartDuration && mHoldOutEndDuration == that.mHoldOutEndDuration
				&& Double.compare(that.mHoldVariation, mHoldVariation) == 0 && mHoldInPosition == that.mHoldInPosition
				&& mHoldOutPosition == that.mHoldOutPosition;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), mBreathEndDuration, mInOutRelation, mHoldBreathIn, mHoldInStartDuration, mHoldInEndDuration,
				mHoldInPosition, mHoldBreathOut, mHoldOutStartDuration, mHoldOutEndDuration, mHoldOutPosition, mHoldVariation);
	}
}
