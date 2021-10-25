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
import de.jeisfeld.breathtraining.exercise.HoldPosition;
import de.jeisfeld.breathtraining.exercise.PlayStatus;
import de.jeisfeld.breathtraining.exercise.StandardExerciseData;
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
	 * The in out relation.
	 */
	private final MutableLiveData<Double> mInOutRelation = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceDouble(R.string.key_in_out_relation, 0.5));

	/**
	 * The hold breath in flag.
	 */
	private final MutableLiveData<Boolean> mHoldBreathIn = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceBoolean(R.string.key_hold_breath_in, false));

	/**
	 * The hold in start duration.
	 */
	private final MutableLiveData<Long> mHoldInStartDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_in_start_duration, 3000L));

	/**
	 * The hold in end duration.
	 */
	private final MutableLiveData<Long> mHoldInEndDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_in_end_duration, 6000L));

	/**
	 * The hold in position.
	 */
	private final MutableLiveData<HoldPosition> mHoldInPosition = new MutableLiveData<>(
			HoldPosition.values()[PreferenceUtil.getSharedPreferenceInt(R.string.key_hold_in_position, HoldPosition.ONLY_END.ordinal())]
	);

	/**
	 * The hold breath out flag.
	 */
	private final MutableLiveData<Boolean> mHoldBreathOut = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceBoolean(R.string.key_hold_breath_out, false));

	/**
	 * The hold out start duration.
	 */
	private final MutableLiveData<Long> mHoldOutStartDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_out_start_duration, 3000L));

	/**
	 * The hold out end duration.
	 */
	private final MutableLiveData<Long> mHoldOutEndDuration = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceLong(R.string.key_hold_out_end_duration, 6000L));

	/**
	 * The hold out position.
	 */
	private final MutableLiveData<HoldPosition> mHoldOutPosition = new MutableLiveData<>(
			HoldPosition.values()[PreferenceUtil.getSharedPreferenceInt(R.string.key_hold_out_position, HoldPosition.ONLY_END.ordinal())]
	);

	/**
	 * The hold variation.
	 */
	private final MutableLiveData<Double> mHoldVariation = new MutableLiveData<>(
			PreferenceUtil.getSharedPreferenceDouble(R.string.key_hold_variation, 0));

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
	 * Get the hold breath in flag.
	 *
	 * @return The hold breath in flag.
	 */
	protected MutableLiveData<Boolean> getHoldBreathIn() {
		return mHoldBreathIn;
	}

	/**
	 * Update the hold breath in flag.
	 *
	 * @param holdBreathIn The new hold breath in flag
	 */
	protected void updateHoldBreathIn(final boolean holdBreathIn) {
		mHoldBreathIn.setValue(holdBreathIn);
		PreferenceUtil.setSharedPreferenceBoolean(R.string.key_hold_breath_in, holdBreathIn);
	}

	/**
	 * Get the hold in start duration.
	 *
	 * @return The hold in start duration.
	 */
	protected MutableLiveData<Long> getHoldInStartDuration() {
		return mHoldInStartDuration;
	}

	/**
	 * Update the hold in start duration.
	 *
	 * @param holdInStartDuration The new hold in start duration
	 */
	protected void updateHoldInStartDuration(final long holdInStartDuration) {
		final Long oldStartDuration = mHoldInStartDuration.getValue();
		mHoldInStartDuration.setValue(holdInStartDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_in_start_duration, holdInStartDuration);
		if (Objects.equals(mHoldInStartDuration.getValue(), mHoldInEndDuration.getValue())
				|| Objects.equals(oldStartDuration, mHoldInEndDuration.getValue())) {
			updateHoldInEndDuration(holdInStartDuration);
		}
	}

	/**
	 * Get the hold in end duration.
	 *
	 * @return The hold in end duration.
	 */
	protected MutableLiveData<Long> getHoldInEndDuration() {
		return mHoldInEndDuration;
	}

	/**
	 * Update the hold in end duration.
	 *
	 * @param holdInEndDuration The new hold in end duration
	 */
	protected void updateHoldInEndDuration(final long holdInEndDuration) {
		mHoldInEndDuration.setValue(holdInEndDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_in_end_duration, holdInEndDuration);
	}

	/**
	 * Get the hold in position.
	 *
	 * @return The hold in position.
	 */
	protected LiveData<HoldPosition> getHoldInPosition() {
		return mHoldInPosition;
	}

	/**
	 * Set the hold in position.
	 *
	 * @param holdInPosition The new hold in position.
	 */
	protected void updateHoldInPosition(final HoldPosition holdInPosition) {
		mHoldInPosition.setValue(holdInPosition);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_hold_in_position, holdInPosition.ordinal());
	}

	/**
	 * Get the hold breath out flag.
	 *
	 * @return The hold breath out flag.
	 */
	protected MutableLiveData<Boolean> getHoldBreathOut() {
		return mHoldBreathOut;
	}

	/**
	 * Update the hold breath out flag.
	 *
	 * @param holdBreathOut The new hold breath out flag
	 */
	protected void updateHoldBreathOut(final boolean holdBreathOut) {
		mHoldBreathOut.setValue(holdBreathOut);
		PreferenceUtil.setSharedPreferenceBoolean(R.string.key_hold_breath_out, holdBreathOut);
	}

	/**
	 * Get the hold out start duration.
	 *
	 * @return The hold out start duration.
	 */
	protected MutableLiveData<Long> getHoldOutStartDuration() {
		return mHoldOutStartDuration;
	}

	/**
	 * Update the hold out start duration.
	 *
	 * @param holdOutStartDuration The new hold out start duration
	 */
	protected void updateHoldOutStartDuration(final long holdOutStartDuration) {
		final Long oldStartDuration = mHoldOutStartDuration.getValue();
		mHoldOutStartDuration.setValue(holdOutStartDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_out_start_duration, holdOutStartDuration);
		if (Objects.equals(mHoldOutStartDuration.getValue(), mHoldOutEndDuration.getValue())
				|| Objects.equals(oldStartDuration, mHoldOutEndDuration.getValue())) {
			updateHoldOutEndDuration(holdOutStartDuration);
		}
	}

	/**
	 * Get the hold out end duration.
	 *
	 * @return The hold out end duration.
	 */
	protected MutableLiveData<Long> getHoldOutEndDuration() {
		return mHoldOutEndDuration;
	}

	/**
	 * Update the hold out end duration.
	 *
	 * @param holdOutEndDuration The new hold out end duration
	 */
	protected void updateHoldOutEndDuration(final long holdOutEndDuration) {
		mHoldOutEndDuration.setValue(holdOutEndDuration);
		PreferenceUtil.setSharedPreferenceLong(R.string.key_hold_out_end_duration, holdOutEndDuration);
	}

	/**
	 * Get the hold out position.
	 *
	 * @return The hold out position.
	 */
	protected LiveData<HoldPosition> getHoldOutPosition() {
		return mHoldOutPosition;
	}

	/**
	 * Set the hold out position.
	 *
	 * @param holdOutPosition The new hold out position.
	 */
	protected void updateHoldOutPosition(final HoldPosition holdOutPosition) {
		mHoldOutPosition.setValue(holdOutPosition);
		PreferenceUtil.setSharedPreferenceInt(R.string.key_hold_out_position, holdOutPosition.ordinal());
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
	 * @param allowZero flag indicating if value 0 is allowed
	 * @return The value
	 */
	protected static long durationSeekbarToValue(final int seekbarValue, final boolean allowZero) {
		if (allowZero && seekbarValue == 0) {
			return 0;
		}

		long value = Math.round(511 * Math.exp(0.025 * seekbarValue)); // MAGIC_NUMBER
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
		return new StandardExerciseData(mRepetitions.getValue(), mBreathStartDuration.getValue(), mBreathEndDuration.getValue(),
				mInOutRelation.getValue(), mHoldBreathIn.getValue(), mHoldInStartDuration.getValue(), mHoldInEndDuration.getValue(),
				mHoldInPosition.getValue(), mHoldBreathOut.getValue(), mHoldOutStartDuration.getValue(), mHoldOutEndDuration.getValue(),
				mHoldOutPosition.getValue(), mHoldVariation.getValue(), mSoundType.getValue(), mPlayStatus.getValue(), repetition);
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
		updateExerciseType(exerciseType);

		updateRepetitions(exerciseData.getRepetitions());
		updateBreathStartDuration(exerciseData.getBreathStartDuration());
		updateSoundType(exerciseData.getSoundType());
		updatePlayStatus(exerciseData.getPlayStatus());

		StandardExerciseData holdData = (StandardExerciseData) exerciseData;
		updateBreathEndDuration(holdData.getBreathEndDuration());
		updateInOutRelation(holdData.getInOutRelation());
		updateHoldBreathIn(holdData.isHoldBreathIn());
		updateHoldInStartDuration(holdData.getHoldInStartDuration());
		updateHoldInEndDuration(holdData.getHoldInEndDuration());
		updateHoldInPosition(holdData.getHoldInPosition());
		updateHoldBreathOut(holdData.isHoldBreathOut());
		updateHoldOutStartDuration(holdData.getHoldOutStartDuration());
		updateHoldOutEndDuration(holdData.getHoldOutEndDuration());
		updateHoldOutPosition(holdData.getHoldOutPosition());
		updateHoldVariation(holdData.getHoldVariation());

		if (exerciseStep != null) {
			updateExerciseStep(exerciseStep);
		}
	}

}
