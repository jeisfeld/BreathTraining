package de.jeisfeld.breathtraining.ui.training;

import android.content.Context;

import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.ExerciseData;
import de.jeisfeld.breathtraining.exercise.ExerciseService;
import de.jeisfeld.breathtraining.exercise.ExerciseService.ServiceCommand;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.ExerciseType;
import de.jeisfeld.breathtraining.exercise.HoldExerciseData;
import de.jeisfeld.breathtraining.exercise.HoldPosition;
import de.jeisfeld.breathtraining.exercise.PlayStatus;
import de.jeisfeld.breathtraining.exercise.StepType;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * The view model for the fragment.
 */
public class TrainingViewModel extends ViewModel {
	/**
	 * The exercise type.
	 */
	private final MutableLiveData<ExerciseType> mExerciseType = new MutableLiveData<>(
			ExerciseType.values()[PreferenceUtil.getSharedPreferenceInt(R.string.key_exercise_type, ExerciseType.STANDARD.ordinal())]);

	/**
	 * The number of repetitions.
	 */
	private final MutableLiveData<Integer> mRepetitions = new MutableLiveData<>(PreferenceUtil.getSharedPreferenceInt(R.string.key_repetitions, 10));

	/**
	 * The breath duration.
	 */
	private final MutableLiveData<Long> mBreathStartDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_breath_start_duration, 10000L));

	/**
	 * The breath end duration.
	 */
	private final MutableLiveData<Long> mBreathEndDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_breath_end_duration, 10000L));

	/**
	 * The hold breath flag.
	 */
	private final MutableLiveData<Boolean> mHoldBreath = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceBoolean(R.string.key_hold_breath, false));

	/**
	 * The hold start duration.
	 */
	private final MutableLiveData<Long> mHoldStartDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_start_duration, 3000L));

	/**
	 * The hold end duration.
	 */
	private final MutableLiveData<Long> mHoldEndDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_end_duration, 6000L));

	/**
	 * The in out relation.
	 */
	private final MutableLiveData<Double> mInOutRelation = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceDouble(R.string.key_in_out_relation, 0.5));

	/**
	 * The hold variation.
	 */
	private final MutableLiveData<Double> mHoldVariation = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceDouble(R.string.key_hold_variation, 0));

	/**
	 * The hold position.
	 */
	private final MutableLiveData<HoldPosition> mHoldPosition = new MutableLiveData<>(
			HoldPosition.values()[PreferenceUtil.getSharedPreferenceInt(R.string.key_hold_position, HoldPosition.OUT.ordinal())]);

	/**
	 * The flag indicating what sound should be played.
	 */
	private final MutableLiveData<SoundType> mSoundType = new MutableLiveData<>(
			SoundType.values()[PreferenceUtil.getSharedPreferenceInt(R.string.key_sound_type, SoundType.WORDS.ordinal())]);

	/**
	 * The play status.
	 */
	private final MutableLiveData<PlayStatus> mPlayStatus = new MutableLiveData<>(PlayStatus.STOPPED);

	/**
	 * The current exercise step.
	 */
	private final MutableLiveData<ExerciseStep> mExerciseStep = new MutableLiveData<>(new ExerciseStep(null, 0, 0));

	/**
	 * Get the exercise type.
	 *
	 * @return The exercise type.
	 */
	protected LiveData<ExerciseType> getExerciseType() {
		return mExerciseType;
	}

	/**
	 * Set the exercise type.
	 *
	 * @param exerciseType The new exercise type.
	 */
	protected void updateExerciseType(final ExerciseType exerciseType) {
		mExerciseType.setValue(exerciseType);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_exercise_type, exerciseType.ordinal());
	}

	/**
	 * Get the number of repetitions.
	 *
	 * @return The number of repetitions.
	 */
	protected MutableLiveData<Integer> getRepetitions() {
		return mRepetitions;
	}

	/**
	 * Update the number of repetitions.
	 *
	 * @param repetitions The new number of repetitions
	 */
	protected void updateRepetitions(final int repetitions) {
		mRepetitions.setValue(repetitions);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_repetitions, repetitions);
	}

	/**
	 * Get the breath start duration.
	 *
	 * @return The breath start duration.
	 */
	protected MutableLiveData<Long> getBreathStartDuration() {
		return mBreathStartDuration;
	}

	/**
	 * Update the breath start duration.
	 *
	 * @param breathStartDuration The new breath start duration
	 */
	public void updateBreathStartDuration(final long breathStartDuration) {
		final Long oldStartDuration = mBreathStartDuration.getValue();
		mBreathStartDuration.setValue(breathStartDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_breath_start_duration, breathStartDuration);
		if (Objects.equals(mBreathStartDuration.getValue(), mBreathEndDuration.getValue())
				|| Objects.equals(oldStartDuration, mBreathEndDuration.getValue())) {
			updateBreathEndDuration(breathStartDuration);
		}
	}

	/**
	 * Get the breath end duration.
	 *
	 * @return The breath end duration.
	 */
	protected MutableLiveData<Long> getBreathEndDuration() {
		return mBreathEndDuration;
	}

	/**
	 * Update the breath end duration.
	 *
	 * @param breathEndDuration The new breath end duration
	 */
	public void updateBreathEndDuration(final long breathEndDuration) {
		mBreathEndDuration.setValue(breathEndDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_breath_end_duration, breathEndDuration);
	}

	/**
	 * Get the hold breath flag.
	 *
	 * @return The hold breath flag.
	 */
	protected MutableLiveData<Boolean> getHoldBreath() {
		return mHoldBreath;
	}

	/**
	 * Update the hold breath flag.
	 *
	 * @param holdBreath The new hold breath flag
	 */
	protected void updateHoldBreath(final boolean holdBreath) {
		mHoldBreath.setValue(holdBreath);
		PreferenceUtil.setSharedPreferenceBoolean(R.string.key_hold_breath, holdBreath);
	}

	/**
	 * Get the hold start duration.
	 *
	 * @return The hold start duration.
	 */
	protected MutableLiveData<Long> getHoldStartDuration() {
		return mHoldStartDuration;
	}

	/**
	 * Update the hold start duration.
	 *
	 * @param holdStartDuration The new hold start duration
	 */
	protected void updateHoldStartDuration(final long holdStartDuration) {
		final Long oldStartDuration = mHoldStartDuration.getValue();
		mHoldStartDuration.setValue(holdStartDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_start_duration, holdStartDuration);
		if (Objects.equals(mHoldStartDuration.getValue(), mHoldEndDuration.getValue())
				|| Objects.equals(oldStartDuration, mHoldEndDuration.getValue())) {
			updateHoldEndDuration(holdStartDuration);
		}
	}

	/**
	 * Get the hold end duration.
	 *
	 * @return The hold end duration.
	 */
	protected MutableLiveData<Long> getHoldEndDuration() {
		return mHoldEndDuration;
	}

	/**
	 * Update the hold end duration.
	 *
	 * @param holdEndDuration The new hold end duration
	 */
	protected void updateHoldEndDuration(final long holdEndDuration) {
		mHoldEndDuration.setValue(holdEndDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_end_duration, holdEndDuration);
	}

	/**
	 * Get the in/out relation.
	 *
	 * @return The in/out relation.
	 */
	protected MutableLiveData<Double> getInOutRelation() {
		return mInOutRelation;
	}

	/**
	 * Update the in/out relation.
	 *
	 * @param inOutRelation The new in/out relation
	 */
	public void updateInOutRelation(final double inOutRelation) {
		mInOutRelation.setValue(inOutRelation);
		PreferenceUtil.setSharedPreferenceDouble(R.string.key_in_out_relation, inOutRelation);
	}

	/**
	 * Get the hold variation.
	 *
	 * @return The hold variation.
	 */
	protected MutableLiveData<Double> getHoldVariation() {
		return mHoldVariation;
	}

	/**
	 * Update the hold variation.
	 *
	 * @param holdVariation The new hold variation
	 */
	protected void updateHoldVariation(final double holdVariation) {
		mHoldVariation.setValue(holdVariation);
		PreferenceUtil.setSharedPreferenceDouble(R.string.key_hold_variation, holdVariation);
	}

	/**
	 * Get the hold position.
	 *
	 * @return The hold position.
	 */
	public LiveData<HoldPosition> getHoldPosition() {
		return mHoldPosition;
	}

	/**
	 * Update the hold position.
	 *
	 * @param holdPosition The new hold position
	 */
	protected void updateHoldPosition(final HoldPosition holdPosition) {
		mHoldPosition.setValue(holdPosition);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_hold_position, holdPosition.ordinal());
	}

	/**
	 * Get the sound type.
	 *
	 * @return The sound type.
	 */
	protected MutableLiveData<SoundType> getSoundType() {
		return mSoundType;
	}

	/**
	 * Update the sound type.
	 *
	 * @param soundType The new sound type.
	 */
	protected void updateSoundType(final SoundType soundType) {
		mSoundType.setValue(soundType);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_sound_type, soundType.ordinal());
	}

	/**
	 * Get the play status.
	 *
	 * @return The play status.
	 */
	public MutableLiveData<PlayStatus> getPlayStatus() {
		return mPlayStatus;
	}

	/**
	 * Update the play status.
	 *
	 * @param playStatus The play status.
	 */
	public void updatePlayStatus(final PlayStatus playStatus) {
		mPlayStatus.setValue(playStatus);
		if (playStatus == PlayStatus.STOPPED) {
			mExerciseStep.setValue(new ExerciseStep(null, 0, 0));
		}
	}

	/**
	 * Get the current exercise step.
	 *
	 * @return the current exercise step.
	 */
	protected MutableLiveData<ExerciseStep> getExerciseStep() {
		return mExerciseStep;
	}

	/**
	 * Get the display string for the number of repetitions.
	 *
	 * @return The display string for the number of repetitions.
	 */
	protected String getRepetitionString() {
		ExerciseStep exerciseStep = mExerciseStep.getValue();
		if (exerciseStep == null || exerciseStep.getStepType() == StepType.RELAX) {
			return "";
		}
		else {
			return "(" + exerciseStep.getRepetition() + "/" + mRepetitions.getValue() + ")";
		}
	}

	/**
	 * Update the exercise step.
	 *
	 * @param exerciseStep The current exercise step.
	 */
	public void updateExerciseStep(final ExerciseStep exerciseStep) {
		mExerciseStep.setValue(exerciseStep);
	}

	/**
	 * Start playing.
	 *
	 * @param context The context.
	 */
	public void play(final Context context) {
		ServiceCommand serviceCommand = mPlayStatus.getValue() == PlayStatus.PAUSED ? ServiceCommand.RESUME : ServiceCommand.START;
		mPlayStatus.setValue(PlayStatus.PLAYING);
		ExerciseService.triggerExerciseService(context, serviceCommand, getExerciseData());
	}

	/**
	 * Stop playing.
	 *
	 * @param context The context.
	 */
	protected void stop(final Context context) {
		mPlayStatus.setValue(PlayStatus.STOPPED);
		ExerciseService.triggerExerciseService(context, ServiceCommand.STOP, getExerciseData());
	}

	/**
	 * Pause playing.
	 *
	 * @param context The context.
	 */
	public void pause(final Context context) {
		mPlayStatus.setValue(PlayStatus.PAUSED);
		ExerciseService.triggerExerciseService(context, ServiceCommand.PAUSE, getExerciseData());
	}

	/**
	 * Go to next breath.
	 *
	 * @param context The context.
	 */
	protected void next(final Context context) {
		ExerciseService.triggerExerciseService(context, ServiceCommand.SKIP, getExerciseData());
	}

	/**
	 * Convert seekbar value to value in ms for duration.
	 *
	 * @param seekbarValue the seekbar value
	 * @param allowZero    flag indicating if value 0 is allowed
	 * @return The value
	 */
	protected static long durationSeekbarToValue(final int seekbarValue, final boolean allowZero) {
		if (allowZero && seekbarValue == 0) {
			return 0;
		}

		long value = Math.round(511 * Math.exp(0.025 * seekbarValue));
		// Rounding for better round value selection via seekbar
		if (value > 152500) { // MAGIC_NUMBER
			value = ((value + 5000) / 10000) * 10000; // MAGIC_NUMBER
		}
		else if (value > 45500) { // MAGIC_NUMBER
			value = ((value + 2500) / 5000) * 5000; // MAGIC_NUMBER
		}
		else if (value > 15250) { // MAGIC_NUMBER
			value = ((value + 500) / 1000) * 1000; // MAGIC_NUMBER
		}
		else if (value > 5050) { // MAGIC_NUMBER
			value = ((value + 250) / 500) * 500; // MAGIC_NUMBER
		}
		else {
			value = ((value + 50) / 100) * 100; // MAGIC_NUMBER
		}
		return value;
	}

	/**
	 * Convert value in ms to seekbar value for duration.
	 *
	 * @param value the value in ms
	 * @return The seekbar value
	 */
	protected static int durationValueToSeekbar(final long value) {
		if (value == 0) {
			return 0;
		}
		return (int) Math.round(Math.log(value / 511.0) / 0.025); // MAGIC_NUMBER
	}

	/**
	 * Convert seekbar value to value for repetitions.
	 *
	 * @param seekbarValue the seekbar value
	 * @return The value
	 */
	protected static int repetitionsSeekbarToValue(final int seekbarValue) {
		int value = (int) Math.round(Math.exp(seekbarValue / 144.77)); // MAGIC_NUMBER
		if (value > 200) { // MAGIC_NUMBER
			value = (int) Math.round(value / 25.0) * 25; // MAGIC_NUMBER
		}
		else if (value > 40) { // MAGIC_NUMBER
			value = (int) Math.round(value / 5.0) * 5; // MAGIC_NUMBER
		}
		return value;
	}

	/**
	 * Convert value in ms to seekbar value for repetitions.
	 *
	 * @param value the value in ms
	 * @return The seekbar value
	 */
	protected static int repetitionsValueToSeekbar(final long value) {
		if (value == 0) {
			return 0;
		}
		return (int) Math.round(Math.log(value) * 144.77); // MAGIC_NUMBER
	}

	/**
	 * Get the exercise data.
	 *
	 * @return the exercise data.
	 */
	public ExerciseData getExerciseData() {
		ExerciseType exerciseType = mExerciseType.getValue();
		if (exerciseType == null) {
			return null;
		}
		int repetition = mExerciseStep.getValue() == null ? 0 : mExerciseStep.getValue().getRepetition();
		return new HoldExerciseData(mRepetitions.getValue(), mBreathStartDuration.getValue(), mBreathEndDuration.getValue(),
				mInOutRelation.getValue(), mHoldBreath.getValue(), mHoldStartDuration.getValue(), mHoldEndDuration.getValue(),
				mHoldPosition.getValue(), mHoldVariation.getValue(), mSoundType.getValue(), mPlayStatus.getValue(), repetition);
	}

	/**
	 * Update the model from exercise data.
	 *
	 * @param exerciseData The exercise data.
	 * @param exerciseStep The exercise step.
	 */
	public void updateFromExerciseData(final ExerciseData exerciseData, final ExerciseStep exerciseStep) {
		if (exerciseData == null) {
			return;
		}
		ExerciseType exerciseType = exerciseData.getType();
		if (exerciseType == null) {
			return;
		}
		mExerciseType.setValue(exerciseType);

		mRepetitions.setValue(exerciseData.getRepetitions());
		mBreathStartDuration.setValue(exerciseData.getBreathStartDuration());
		mSoundType.setValue(exerciseData.getSoundType());
		mPlayStatus.setValue(exerciseData.getPlayStatus());

		HoldExerciseData holdData = (HoldExerciseData) exerciseData;
		mBreathEndDuration.setValue(holdData.getBreathEndDuration());
		mInOutRelation.setValue(holdData.getInOutRelation());
		mHoldStartDuration.setValue(holdData.getHoldStartDuration());
		mHoldEndDuration.setValue(holdData.getHoldEndDuration());
		mHoldPosition.setValue(holdData.getHoldPosition());
		mHoldVariation.setValue(holdData.getHoldVariation());

		if (exerciseStep != null) {
			updateExerciseStep(exerciseStep);
		}
	}

}
