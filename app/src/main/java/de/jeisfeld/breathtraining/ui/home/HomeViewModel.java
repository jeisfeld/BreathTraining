package de.jeisfeld.breathtraining.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathtraining.exercise.ExerciseData;
import de.jeisfeld.breathtraining.exercise.ExerciseService;
import de.jeisfeld.breathtraining.exercise.ExerciseService.ServiceCommand;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.ExerciseType;
import de.jeisfeld.breathtraining.exercise.HoldExerciseData;
import de.jeisfeld.breathtraining.exercise.HoldPosition;
import de.jeisfeld.breathtraining.exercise.PlayStatus;
import de.jeisfeld.breathtraining.exercise.SimpleExerciseData;
import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * The view model for the fragment.
 */
public class HomeViewModel extends ViewModel {
	/**
	 * The exercise type.
	 */
	private final MutableLiveData<ExerciseType> mExerciseType = new MutableLiveData<>(ExerciseType.SIMPLE);

	/**
	 * The number of repetitions.
	 */
	private final MutableLiveData<Integer> mRepetitions = new MutableLiveData<>(10);

	/**
	 * The breath duration.
	 */
	private final MutableLiveData<Long> mBreathDuration = new MutableLiveData<>(10000L);

	/**
	 * The breath end duration.
	 */
	private final MutableLiveData<Long> mBreathEndDuration = new MutableLiveData<>(10000L);

	/**
	 * The hold start duration.
	 */
	private final MutableLiveData<Long> mHoldStartDuration = new MutableLiveData<>(3000L);

	/**
	 * The hold end duration.
	 */
	private final MutableLiveData<Long> mHoldEndDuration = new MutableLiveData<>(6000L);

	/**
	 * The in out relation.
	 */
	private final MutableLiveData<Double> mInOutRelation = new MutableLiveData<>(0.5); // MAGIC_NUMBER

	/**
	 * The hold variation.
	 */
	private final MutableLiveData<Double> mHoldVariation = new MutableLiveData<>(0.0); // MAGIC_NUMBER

	/**
	 * The hold position.
	 */
	private final MutableLiveData<HoldPosition> mHoldPosition = new MutableLiveData<>(HoldPosition.OUT);

	/**
	 * The flag indicating what sound should be played.
	 */
	private final MutableLiveData<SoundType> mSoundType = new MutableLiveData<>(SoundType.WORDS);

	/**
	 * The play status.
	 */
	private final MutableLiveData<PlayStatus> mPlayStatus = new MutableLiveData<>(PlayStatus.STOPPED);

	/**
	 * The current exercise step.
	 */
	private final MutableLiveData<ExerciseStep> mExerciseStep = new MutableLiveData<>(null);

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
	}

	/**
	 * Get the breath duration.
	 *
	 * @return The breath duration.
	 */
	protected MutableLiveData<Long> getBreathDuration() {
		return mBreathDuration;
	}

	/**
	 * Update the breath duration.
	 *
	 * @param breathDuration The new breath duration
	 */
	public void updateBreathDuration(final long breathDuration) {
		mBreathDuration.setValue(breathDuration);
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
		mHoldStartDuration.setValue(holdStartDuration);
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
	}

	/**
	 * Get the play status.
	 *
	 * @return The play status.
	 */
	protected MutableLiveData<PlayStatus> getPlayStatus() {
		return mPlayStatus;
	}

	/**
	 * Update the play status.
	 *
	 * @param playStatus The play status.
	 */
	public void updatePlayStatus(final PlayStatus playStatus) {
		mPlayStatus.postValue(playStatus);
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
	 * Update the exercise step.
	 *
	 * @param exerciseStep The current exercise step.
	 */
	public void updateExerciseStep(final ExerciseStep exerciseStep) {
		mExerciseStep.postValue(exerciseStep);
	}

	/**
	 * Start playing.
	 *
	 * @param context The context.
	 */
	protected void play(final Context context) {
		mPlayStatus.setValue(PlayStatus.PLAYING);
		ExerciseService.triggerExerciseService(context, ServiceCommand.START, getExerciseData());
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
	protected void pause(final Context context) {
		mPlayStatus.setValue(PlayStatus.PAUSED);
		ExerciseService.triggerExerciseService(context, ServiceCommand.PAUSE, getExerciseData());
	}

	/**
	 * Go to next breath.
	 *
	 * @param context The context.
	 */
	protected void next(final Context context) {
		ExerciseService.triggerExerciseService(context, ServiceCommand.NEXT, getExerciseData());
	}

	/**
	 * Resume playing.
	 *
	 * @param context The context.
	 */
	protected void resume(final Context context) {
		mPlayStatus.setValue(PlayStatus.PLAYING);
		ExerciseService.triggerExerciseService(context, ServiceCommand.RESUME, getExerciseData());
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
		return Math.round(250 * Math.exp(0.025 * seekbarValue)); // MAGIC_NUMBER
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
		return (int) Math.round(Math.log(value / 250.0) / 0.025); // MAGIC_NUMBER
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
		switch (exerciseType) {
		case SIMPLE:
			return new SimpleExerciseData(mRepetitions.getValue(), mBreathDuration.getValue(), mBreathEndDuration.getValue(),
					mInOutRelation.getValue(), mSoundType.getValue(), mPlayStatus.getValue());
		case HOLD:
			return new HoldExerciseData(mRepetitions.getValue(), mBreathDuration.getValue(), mInOutRelation.getValue(), mHoldStartDuration.getValue(),
					mHoldEndDuration.getValue(), mHoldPosition.getValue(), mHoldVariation.getValue(), mSoundType.getValue(), mPlayStatus.getValue());
		default:
			return null;
		}
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
		mBreathDuration.setValue(exerciseData.getBreathDuration());
		mInOutRelation.setValue(exerciseData.getInOutRelation());
		mSoundType.setValue(exerciseData.getSoundType());
		mPlayStatus.setValue(exerciseData.getPlayStatus());

		switch (exerciseType) {
		case SIMPLE:
			mBreathEndDuration.setValue(((SimpleExerciseData) exerciseData).getBreathEndDuration());
			break;
		case HOLD:
			HoldExerciseData holdData = (HoldExerciseData) exerciseData;
			mHoldStartDuration.setValue(holdData.getHoldStartDuration());
			mHoldEndDuration.setValue(holdData.getHoldEndDuration());
			mHoldPosition.setValue(holdData.getHoldPosition());
			mHoldVariation.setValue(holdData.getHoldVariation());
			break;
		default:
			// do nothing
		}

		if (exerciseStep != null) {
			updateExerciseStep(exerciseStep);
		}
	}

}
