package de.jeisfeld.breathtraining.exercise;

import java.io.Serializable;

import android.content.Intent;

import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.ui.training.ServiceReceiver;

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
	private static final String EXTRA_EXERCISE_TYPE = "de.jeisfeld.breathtraining.EXERCISE_TYPE";
	/**
	 * Key for the breath start duration within the intent.
	 */
	protected static final String EXTRA_BREATH_START_DURATION = "de.jeisfeld.breathtraining.BREATH_START_DURATION";
	/**
	 * Key for the repetitions within the intent.
	 */
	protected static final String EXTRA_REPETITIONS = "de.jeisfeld.breathtraining.REPETITIONS";
	/**
	 * Key for the repetitions within the intent.
	 */
	protected static final String EXTRA_CURRENT_REPETITION = "de.jeisfeld.breathtraining.CURRENT_REPETITION";
	/**
	 * Key for the sound type within the intent.
	 */
	protected static final String EXTRA_SOUND_TYPE = "de.jeisfeld.breathtraining.SOUND_TYPE";
	/**
	 * Key for the breath duration within the intent.
	 */
	protected static final String EXTRA_BREATH_END_DURATION = "de.jeisfeld.breathtraining.BREATH_END_DURATION";
	/**
	 * Key for the in/out relation within the intent.
	 */
	protected static final String EXTRA_IN_OUT_RELATION = "de.jeisfeld.breathtraining.IN_OUT_RELATION";
	/**
	 * Key for the hold breath flag within the intent..
	 */
	protected static final String EXTRA_HOLD_BREATH = "de.jeisfeld.breathtraining.HOLD_BREATH";
	/**
	 * Key for the hold in start duration within the intent.
	 */
	protected static final String EXTRA_HOLD_IN_START_DURATION = "de.jeisfeld.breathtraining.HOLD_IN_START_DURATION";
	/**
	 * Key for the hold in end duration within the intent.
	 */
	protected static final String EXTRA_HOLD_IN_END_DURATION = "de.jeisfeld.breathtraining.HOLD_IN_END_DURATION";
	/**
	 * Key for the hold out start duration within the intent.
	 */
	protected static final String EXTRA_HOLD_OUT_START_DURATION = "de.jeisfeld.breathtraining.HOLD_OUT_START_DURATION";
	/**
	 * Key for the hold out end duration within the intent.
	 */
	protected static final String EXTRA_HOLD_OUT_END_DURATION = "de.jeisfeld.breathtraining.HOLD_OUT_END_DURATION";
	/**
	 * Key for the hold variation within the intent.
	 */
	protected static final String EXTRA_HOLD_VARIATION = "de.jeisfeld.breathtraining.HOLD_VARIATION";
	/**
	 * The number of repetitions.
	 */
	private final int mRepetitions;
	/**
	 * The breath start duration.
	 */
	private final long mBreathStartDuration;
	/**
	 * The sound type.
	 */
	private final SoundType mSoundType;
	/**
	 * The current repetition number.
	 */
	private int mCurrentRepetitionNumber;
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
	private PlayStatus mPlayStatus;

	/**
	 * Constructor.
	 *
	 * @param repetitions             The number of repetitions.
	 * @param breathStartDuration     The breath start duration.
	 * @param soundType               The sound type.
	 * @param playStatus              The playing status.
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public ExerciseData(final Integer repetitions, final Long breathStartDuration, final SoundType soundType,
						final PlayStatus playStatus, final int currentRepetitionNumber) {
		mRepetitions = repetitions;
		mBreathStartDuration = breathStartDuration;
		mSoundType = soundType;
		mPlayStatus = playStatus;
		mCurrentRepetitionNumber = currentRepetitionNumber;
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
		if (mCurrentSteps == null || mCurrentStepNumber >= mCurrentSteps.length - 1) {
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
		else if (mCurrentRepetitionNumber > getRepetitions()) {
			// may happen if current repetition or number of repetitions has been changed
			return null;
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
		serviceIntent.putExtra(EXTRA_BREATH_START_DURATION, mBreathStartDuration);
		serviceIntent.putExtra(EXTRA_SOUND_TYPE, mSoundType);
		serviceIntent.putExtra(ServiceReceiver.EXTRA_PLAY_STATUS, mPlayStatus);
		serviceIntent.putExtra(EXTRA_CURRENT_REPETITION, mCurrentRepetitionNumber);
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
	 * Get the breath start duration.
	 *
	 * @return the breath start duration.
	 */
	public long getBreathStartDuration() {
		return mBreathStartDuration;
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
		long breathStartDuration = intent.getLongExtra(EXTRA_BREATH_START_DURATION, 0);
		long breathEndDuration = intent.getLongExtra(EXTRA_BREATH_END_DURATION, 0);
		double inOutRelation = intent.getDoubleExtra(EXTRA_IN_OUT_RELATION, 0.5); // MAGIC_NUMBER
		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(ServiceReceiver.EXTRA_PLAY_STATUS);
		boolean holdBreath = intent.getBooleanExtra(EXTRA_HOLD_BREATH, false);
		long holdInStartDuration = intent.getLongExtra(EXTRA_HOLD_IN_START_DURATION, 0);
		long holdInEndDuration = intent.getLongExtra(EXTRA_HOLD_IN_END_DURATION, 0);
		long holdOutStartDuration = intent.getLongExtra(EXTRA_HOLD_OUT_START_DURATION, 0);
		long holdOutEndDuration = intent.getLongExtra(EXTRA_HOLD_OUT_END_DURATION, 0);
		double holdVariation = intent.getDoubleExtra(EXTRA_HOLD_VARIATION, 0);
		SoundType soundType = (SoundType) intent.getSerializableExtra(EXTRA_SOUND_TYPE);
		int currentRepetitionNumber = intent.getIntExtra(EXTRA_CURRENT_REPETITION, 0);
		return new StandardExerciseData(repetitions, breathStartDuration, breathEndDuration, inOutRelation, holdBreath, holdInStartDuration,
				holdInEndDuration, holdOutStartDuration, holdOutEndDuration, holdVariation, soundType, playStatus, currentRepetitionNumber);
	}

	/**
	 * Retrieve the status from other ExerciseData and upate the playStatus.
	 *
	 * @param origin     The other ExerciseData.
	 * @param playStatus The new playStatus.
	 */
	public void retrieveStatus(final ExerciseData origin, final PlayStatus playStatus) {
		mCurrentStepNumber = origin.mCurrentStepNumber;
		mCurrentSteps = getStepsForRepetition(mCurrentRepetitionNumber);
		mPlayStatus = playStatus;
	}
}
