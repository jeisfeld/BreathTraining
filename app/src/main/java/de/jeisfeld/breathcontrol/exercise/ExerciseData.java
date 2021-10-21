package de.jeisfeld.breathcontrol.exercise;

import java.io.Serializable;

import android.content.Intent;
import de.jeisfeld.breathcontrol.sound.SoundType;
import de.jeisfeld.breathcontrol.ui.home.ServiceReceiver;

/**
 * Class holding data representing an animation.
 */
public abstract class ExerciseData implements Serializable {
	/**
	 * The default serial version id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Key for the exercise type within the intent.
	 */
	private static final String EXTRA_EXERCISE_TYPE = "de.jeisfeld.breathcontrol.EXERCISE_TYPE";
	/**
	 * Key for the breath duration within the intent.
	 */
	protected static final String EXTRA_BREATH_DURATION = "de.jeisfeld.breathcontrol.BREATH_DURATION";
	/**
	 * Key for the breath duration within the intent.
	 */
	protected static final String EXTRA_BREATH_END_DURATION = "de.jeisfeld.breathcontrol.BREATH_END_DURATION";
	/**
	 * Key for the in/out relation within the intent.
	 */
	protected static final String EXTRA_IN_OUT_RELATION = "de.jeisfeld.breathcontrol.IN_OUT_RELATION";
	/**
	 * Key for the repetitions within the intent.
	 */
	protected static final String EXTRA_REPETITIONS = "de.jeisfeld.breathcontrol.REPETITIONS";
	/**
	 * Key for the sound type within the intent.
	 */
	protected static final String EXTRA_SOUND_TYPE = "de.jeisfeld.breathcontrol.SOUND_TYPE";
	/**
	 * Key for the hold start duration within the intent.
	 */
	protected static final String EXTRA_HOLD_START_DURATION = "de.jeisfeld.breathcontrol.HOLD_START_DURATION";
	/**
	 * Key for the hold end duration within the intent.
	 */
	protected static final String EXTRA_HOLD_END_DURATION = "de.jeisfeld.breathcontrol.HOLD_END_DURATION";
	/**
	 * Key for the hold position within the intent.
	 */
	protected static final String EXTRA_HOLD_POSITION = "de.jeisfeld.breathcontrol.HOLD_POSITION";
	/**
	 * Key for the hold variation within the intent.
	 */
	protected static final String EXTRA_HOLD_VARIATION = "de.jeisfeld.breathcontrol.HOLD_VARIATION";
	/**
	 * The number of repetitions.
	 */
	private final int mRepetitions;
	/**
	 * The breath duration.
	 */
	private final long mBreathDuration;
	/**
	 * The in/out relation.
	 */
	private final double mInOutRelation;
	/**
	 * The sound type.
	 */
	private final SoundType mSoundType;
	/**
	 * The current repetition number.
	 */
	private int mCurrentRepetitionNumber = 0;
	/**
	 * The current step number within the repetition.
	 */
	private int mCurrentStepNumber = 0;
	/**
	 * The steps of the current repetition.
	 */
	private ExerciseStep[] mCurrentSteps = null;
	/**
	 * The playing status.
	 */
	private final PlayStatus mPlayStatus;

	/**
	 * Constructor.
	 *
	 * @param repetitions The number of repetitions.
	 * @param breathDuration The breath start duration.
	 * @param inOutRelation The in/out relation.
	 * @param soundType The sound type.
	 * @param playStatus The playing status.
	 */
	public ExerciseData(final Integer repetitions, final Long breathDuration, final Double inOutRelation, final SoundType soundType,
			final PlayStatus playStatus) {
		mRepetitions = repetitions;
		mBreathDuration = breathDuration;
		mInOutRelation = inOutRelation;
		mSoundType = soundType;
		mPlayStatus = playStatus;
	}

	/**
	 * Get the animation type.
	 *
	 * @return The animation type.
	 */
	public abstract ExerciseType getType();

	/**
	 * Get the exercise steps for a certain repetition.
	 *
	 * @param repetition The repetition number (starting with 1).
	 * @return The steps for this repetition.
	 */
	protected abstract ExerciseStep[] getStepsForRepetition(int repetition);

	/**
	 * Get the next exercise step.
	 *
	 * @return The next exercise step.
	 */
	public final ExerciseStep getNextStep() {
		if (mCurrentSteps == null || mCurrentStepNumber == mCurrentSteps.length - 1) {
			mCurrentRepetitionNumber++;
			if (mCurrentRepetitionNumber > getRepetitions()) {
				// made all repetitions
				return null;
			}
			mCurrentStepNumber = 0;
			mCurrentSteps = getStepsForRepetition(mCurrentRepetitionNumber);
			while ((mCurrentSteps == null || mCurrentSteps.length == 0) && mCurrentRepetitionNumber <= getRepetitions()) {
				// go to next repetitions in case of empty repetitions.
				mCurrentRepetitionNumber++;
				mCurrentSteps = getStepsForRepetition(mCurrentRepetitionNumber);
			}
			if (mCurrentSteps == null || mCurrentSteps.length == 0) {
				return null;
			}
		}
		else {
			mCurrentStepNumber++;
		}
		return mCurrentSteps[mCurrentStepNumber];
	}

	/**
	 * Add the data to an intent.
	 *
	 * @param serviceIntent The intent.
	 */
	// OVERRIDABLE
	public void addToIntent(final Intent serviceIntent) {
		serviceIntent.putExtra(EXTRA_EXERCISE_TYPE, getType());
		serviceIntent.putExtra(EXTRA_REPETITIONS, mRepetitions);
		serviceIntent.putExtra(EXTRA_BREATH_DURATION, mBreathDuration);
		serviceIntent.putExtra(EXTRA_IN_OUT_RELATION, mInOutRelation);
		serviceIntent.putExtra(EXTRA_SOUND_TYPE, mSoundType);
		serviceIntent.putExtra(ServiceReceiver.EXTRA_PLAY_STATUS, mPlayStatus);
	}

	/**
	 * Get the number of repetitions.
	 *
	 * @return The number of repetitions.
	 */
	public int getRepetitions() {
		return mRepetitions;
	}

	/**
	 * Get the breath duration.
	 *
	 * @return the breath duration.
	 */
	public long getBreathDuration() {
		return mBreathDuration;
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
	 * Get the sound type.
	 *
	 * @return The sound type.
	 */
	public SoundType getSoundType() {
		return mSoundType;
	}

	/**
	 * Get the play status.
	 *
	 * @return The play statis.
	 */
	public PlayStatus getPlayStatus() {
		return mPlayStatus;
	}

	/**
	 * Restore exercise data from service intent.
	 *
	 * @param intent The service intent.
	 * @return The exercise data.
	 */
	public static ExerciseData fromIntent(final Intent intent) {
		ExerciseType exerciseType = (ExerciseType) intent.getSerializableExtra(EXTRA_EXERCISE_TYPE);
		if (exerciseType == null) {
			return null;
		}

		int repetitions = intent.getIntExtra(EXTRA_REPETITIONS, 0);
		long breathDuration = intent.getLongExtra(EXTRA_BREATH_DURATION, 0);
		double inOutRelation = intent.getDoubleExtra(EXTRA_IN_OUT_RELATION, 0.5); // MAGIC_NUMBER
		SoundType soundType = (SoundType) intent.getSerializableExtra(EXTRA_SOUND_TYPE);
		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(ServiceReceiver.EXTRA_PLAY_STATUS);

		switch (exerciseType) {
		case SIMPLE:
			long breathEndDuration = intent.getLongExtra(EXTRA_BREATH_END_DURATION, 0);
			return new SimpleExerciseData(repetitions, breathDuration, breathEndDuration, inOutRelation, soundType, playStatus);
		case HOLD:
			long holdStartDuration = intent.getLongExtra(EXTRA_HOLD_START_DURATION, 0);
			long holdEndDuration = intent.getLongExtra(EXTRA_HOLD_END_DURATION, 0);
			HoldPosition holdPosition = (HoldPosition) intent.getSerializableExtra(EXTRA_HOLD_POSITION);
			double holdVariation = intent.getDoubleExtra(EXTRA_HOLD_VARIATION, 0);
			return new HoldExerciseData(repetitions, breathDuration, inOutRelation, holdStartDuration,
					holdEndDuration, holdPosition, holdVariation, soundType, playStatus);
		default:
			return null;
		}
	}

}
