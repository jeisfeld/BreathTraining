package de.jeisfeld.breathtraining.exercise.data;

import android.content.Intent;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.service.ServiceReceiver;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

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
	 * Key for the exercise name within the intent.
	 */
	private static final String EXTRA_EXERCISE_NAME = "de.jeisfeld.breathtraining.EXERCISE_NAME";
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
	 * Key for the hold breath in flag within the intent..
	 */
	protected static final String EXTRA_HOLD_BREATH_IN = "de.jeisfeld.breathtraining.HOLD_BREATH_IN";
	/**
	 * Key for the hold in start duration within the intent.
	 */
	protected static final String EXTRA_HOLD_IN_START_DURATION = "de.jeisfeld.breathtraining.HOLD_IN_START_DURATION";
	/**
	 * Key for the hold in end duration within the intent.
	 */
	protected static final String EXTRA_HOLD_IN_END_DURATION = "de.jeisfeld.breathtraining.HOLD_IN_END_DURATION";
	/**
	 * Key for the hold in position within the intent.
	 */
	protected static final String EXTRA_HOLD_IN_POSITION = "de.jeisfeld.breathtraining.HOLD_IN_POSITION";
	/**
	 * Key for the hold breath out flag within the intent..
	 */
	protected static final String EXTRA_HOLD_BREATH_OUT = "de.jeisfeld.breathtraining.HOLD_BREATH_OUT";
	/**
	 * Key for the hold out start duration within the intent.
	 */
	protected static final String EXTRA_HOLD_OUT_START_DURATION = "de.jeisfeld.breathtraining.HOLD_OUT_START_DURATION";
	/**
	 * Key for the hold out end duration within the intent.
	 */
	protected static final String EXTRA_HOLD_OUT_END_DURATION = "de.jeisfeld.breathtraining.HOLD_OUT_END_DURATION";
	/**
	 * Key for the hold in position within the intent.
	 */
	protected static final String EXTRA_HOLD_OUT_POSITION = "de.jeisfeld.breathtraining.HOLD_OUT_POSITION";
	/**
	 * Key for the hold variation within the intent.
	 */
	protected static final String EXTRA_HOLD_VARIATION = "de.jeisfeld.breathtraining.HOLD_VARIATION";
	/**
	 * The id (in case of storage).
	 */
	private int mId = 0;
	/**
	 * The name (in case of storage).
	 */
	private String mName;
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
	 * @param name                    The name of the exercise.
	 * @param repetitions             The number of repetitions.
	 * @param breathStartDuration     The breath start duration.
	 * @param soundType               The sound type.
	 * @param playStatus              The playing status.
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public ExerciseData(final String name, final Integer repetitions, final Long breathStartDuration, final SoundType soundType,
						final PlayStatus playStatus, final int currentRepetitionNumber) {
		mName = name;
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
	 * Go back to the start of the repetition.
	 */
	public void goBackToRepetitionStart() {
		mCurrentRepetitionNumber--;
		mCurrentSteps = null;
	}

	/**
	 * Add the data to an intent.
	 *
	 * @param serviceIntent The intent.
	 */
	// OVERRIDABLE
	public void addToIntent(final Intent serviceIntent) {
		serviceIntent.putExtra(EXTRA_EXERCISE_TYPE, getType());
		serviceIntent.putExtra(EXTRA_EXERCISE_NAME, mName);
		serviceIntent.putExtra(EXTRA_REPETITIONS, mRepetitions);
		serviceIntent.putExtra(EXTRA_BREATH_START_DURATION, mBreathStartDuration);
		serviceIntent.putExtra(EXTRA_SOUND_TYPE, mSoundType);
		serviceIntent.putExtra(ServiceReceiver.EXTRA_PLAY_STATUS, mPlayStatus);
		serviceIntent.putExtra(EXTRA_CURRENT_REPETITION, mCurrentRepetitionNumber);
	}

	/**
	 * Get the id for storage.
	 *
	 * @return The id
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Get the name for storage.
	 *
	 * @return The name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Set the name for storage.
	 *
	 * @param name The name
	 */
	public void setName(final String name) {
		mName = name;
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
	 * @return The play status.
	 */
	public PlayStatus getPlayStatus() {
		return mPlayStatus;
	}

	/**
	 * Update the play status.
	 *
	 * @param playStatus The new play status.
	 */
	public void updatePlayStatus(PlayStatus playStatus) {
		mPlayStatus = playStatus;
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
		String exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME);
		long breathStartDuration = intent.getLongExtra(EXTRA_BREATH_START_DURATION, 0);
		long breathEndDuration = intent.getLongExtra(EXTRA_BREATH_END_DURATION, 0);
		double inOutRelation = intent.getDoubleExtra(EXTRA_IN_OUT_RELATION, 0.5); // MAGIC_NUMBER
		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(ServiceReceiver.EXTRA_PLAY_STATUS);
		boolean holdBreathIn = intent.getBooleanExtra(EXTRA_HOLD_BREATH_IN, false);
		long holdInStartDuration = intent.getLongExtra(EXTRA_HOLD_IN_START_DURATION, 0);
		long holdInEndDuration = intent.getLongExtra(EXTRA_HOLD_IN_END_DURATION, 0);
		HoldPosition holdInPosition = (HoldPosition) intent.getSerializableExtra(EXTRA_HOLD_IN_POSITION);
		boolean holdBreathOut = intent.getBooleanExtra(EXTRA_HOLD_BREATH_OUT, false);
		long holdOutStartDuration = intent.getLongExtra(EXTRA_HOLD_OUT_START_DURATION, 0);
		long holdOutEndDuration = intent.getLongExtra(EXTRA_HOLD_OUT_END_DURATION, 0);
		HoldPosition holdOutPosition = (HoldPosition) intent.getSerializableExtra(EXTRA_HOLD_OUT_POSITION);
		double holdVariation = intent.getDoubleExtra(EXTRA_HOLD_VARIATION, 0);
		SoundType soundType = (SoundType) intent.getSerializableExtra(EXTRA_SOUND_TYPE);
		int currentRepetitionNumber = intent.getIntExtra(EXTRA_CURRENT_REPETITION, 0);
		return new StandardExerciseData(exerciseName, repetitions, breathStartDuration, breathEndDuration, inOutRelation, holdBreathIn,
				holdInStartDuration, holdInEndDuration, holdInPosition, holdBreathOut, holdOutStartDuration, holdOutEndDuration, holdOutPosition,
				holdVariation, soundType, playStatus, currentRepetitionNumber);
	}

	/**
	 * Restore exercise data from storage.
	 *
	 * @param storedExerciseId The stored exercise id.
	 * @return The exercise data.
	 */
	public static ExerciseData fromId(final int storedExerciseId) {
		String name = PreferenceUtil.getIndexedSharedPreferenceString(R.string.key_stored_exercise_name, storedExerciseId);
		int repetitions = PreferenceUtil.getIndexedSharedPreferenceInt(R.string.key_stored_repetitions, storedExerciseId, 0);
		long breathStartDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_breath_start_duration, storedExerciseId, 0);
		long breathEndDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_breath_end_duration, storedExerciseId, 0);
		double inOutRelation = PreferenceUtil.getIndexedSharedPreferenceDouble(
				R.string.key_stored_in_out_relation, storedExerciseId, 0.5); // MAGIC_NUMBER
		boolean holdBreathIn = PreferenceUtil.getIndexedSharedPreferenceBoolean(R.string.key_stored_hold_breath_in, storedExerciseId, false);
		long holdInStartDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_hold_in_start_duration, storedExerciseId, 0);
		long holdInEndDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_hold_in_end_duration, storedExerciseId, 0);
		HoldPosition holdInPosition =
				HoldPosition.values()[PreferenceUtil.getIndexedSharedPreferenceInt(R.string.key_stored_hold_in_position, storedExerciseId,
						HoldPosition.ONLY_END.ordinal())];
		boolean holdBreathOut = PreferenceUtil.getIndexedSharedPreferenceBoolean(R.string.key_stored_hold_breath_out, storedExerciseId, false);
		long holdOutStartDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_hold_out_start_duration, storedExerciseId, 0);
		long holdOutEndDuration = PreferenceUtil.getIndexedSharedPreferenceLong(R.string.key_stored_hold_out_end_duration, storedExerciseId, 0);
		HoldPosition holdOutPosition =
				HoldPosition.values()[PreferenceUtil.getIndexedSharedPreferenceInt(R.string.key_stored_hold_out_position, storedExerciseId,
						HoldPosition.ONLY_END.ordinal())];
		double holdVariation = PreferenceUtil.getIndexedSharedPreferenceDouble(R.string.key_stored_hold_variation, storedExerciseId, 0);
		SoundType soundType = SoundType.values()[PreferenceUtil.getIndexedSharedPreferenceInt(R.string.key_stored_sound_type, storedExerciseId,
				SoundType.WORDS.ordinal())];
		ExerciseData result =
				new StandardExerciseData(name, repetitions, breathStartDuration, breathEndDuration, inOutRelation, holdBreathIn, holdInStartDuration,
						holdInEndDuration, holdInPosition, holdBreathOut, holdOutStartDuration, holdOutEndDuration, holdOutPosition, holdVariation,
						soundType, PlayStatus.STOPPED, 0);
		result.mId = storedExerciseId;
		return result;
	}

	/**
	 * Fill the id from a stored exercise, if existing.
	 *
	 * @param name The stored exercise name.
	 */
	public void fillIdFromStoredExercise(final String name) {
		ExerciseData storedExerciseData = StoredExercisesRegistry.getInstance().getStoredExercise(name);
		if (storedExerciseData == null) {
			mId = 0;
		}
		else {
			mId = storedExerciseData.getId();
		}
	}

	/**
	 * Remove the id, so that this becomes unsaved exercise.
	 */
	public void cleanId() {
		mId = 0;
	}

	/**
	 * Store this exercise.
	 *
	 * @param name The name for storage.
	 */
	// OVERRIDABLE
	public void store(final String name) {
		if (mId <= 0) {
			int newId = PreferenceUtil.getSharedPreferenceInt(R.string.key_stored_exercise_max_id, 0) + 1;
			PreferenceUtil.setSharedPreferenceInt(R.string.key_stored_exercise_max_id, newId);

			List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
			exerciseIds.add(newId);
			PreferenceUtil.setSharedPreferenceIntList(R.string.key_stored_exercise_ids, exerciseIds);
			mId = newId;
		}
		PreferenceUtil.setIndexedSharedPreferenceInt(R.string.key_stored_repetitions, mId, mRepetitions);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_breath_start_duration, mId, mBreathStartDuration);
		PreferenceUtil.setIndexedSharedPreferenceInt(R.string.key_stored_sound_type, mId, mSoundType.ordinal());
		mName = name;
		PreferenceUtil.setIndexedSharedPreferenceString(R.string.key_stored_exercise_name, mId, mName);
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

	// OVERRIDABLE
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ExerciseData)) {
			return false;
		}
		ExerciseData that = (ExerciseData) o;
		return mRepetitions == that.mRepetitions && mBreathStartDuration == that.mBreathStartDuration && mSoundType == that.mSoundType;
	}

	// OVERRIDABLE
	@Override
	public int hashCode() {
		return Objects.hash(mRepetitions, mBreathStartDuration, mSoundType);
	}
}
