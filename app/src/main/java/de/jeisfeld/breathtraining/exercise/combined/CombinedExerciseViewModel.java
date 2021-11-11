package de.jeisfeld.breathtraining.exercise.combined;

import android.content.Context;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.CombinedExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.ExerciseType;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.data.RepetitionData;
import de.jeisfeld.breathtraining.exercise.data.SingleExerciseData;
import de.jeisfeld.breathtraining.exercise.data.StepType;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService.ServiceCommand;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * The view model for the fragment.
 */
public class CombinedExerciseViewModel extends ViewModel {
	/**
	 * The name of the exercise.
	 */
	private final MutableLiveData<String> mExerciseName = new MutableLiveData<>(PreferenceUtil.getSharedPreferenceString(R.string.key_exercise_name));

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
	private final MutableLiveData<ExerciseStep> mExerciseStep = new MutableLiveData<>(new ExerciseStep(null, 0, new RepetitionData()));

	/**
	 * Get the exercise type.
	 *
	 * @return The exercise type.
	 */
	protected LiveData<String> getExerciseName() {
		return mExerciseName;
	}

	/**
	 * Set the exercise name.
	 *
	 * @param exerciseName The new exercise name.
	 */
	protected void updateExerciseName(final String exerciseName) {
		mExerciseName.setValue(exerciseName);
		if (cacheValues()) {
			PreferenceUtil.setSharedPreferenceString(R.string.key_exercise_name, exerciseName);
		}
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
		if (cacheValues()) {
			PreferenceUtil.setSharedPreferenceInt(R.string.key_sound_type, soundType.ordinal());
		}
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
			mExerciseStep.setValue(new ExerciseStep(null, 0, new RepetitionData()));
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
	 * Update the exercise step.
	 *
	 * @param exerciseStep The current exercise step.
	 */
	public void updateExerciseStep(final ExerciseStep exerciseStep) {
		mExerciseStep.setValue(exerciseStep);
	}

	/**
	 * Method giving information if selected values should be cached.
	 *
	 * @return True if cached.
	 */
	protected boolean cacheValues() {
		return true;
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
	 * Delete the exercise name if not matching the stored exercise.
	 */
	public void deleteNameIfNotMatching() {
		String name = mExerciseName.getValue();
		if (name != null && name.length() > 0) {
			ExerciseData storedExerciseData = StoredExercisesRegistry.getInstance().getStoredExercise(name);
			ExerciseData exerciseData = getExerciseData();
			if (storedExerciseData == null || !storedExerciseData.equals(exerciseData)) {
				mExerciseName.setValue(null);
			}
		}
	}

	/**
	 * Update the model from exercise data.
	 *
	 * @param exerciseData   The exercise data.
	 * @param updateChildren Flag indicating if children should be updated.
	 */
	public void updateFromExerciseData(final ExerciseData exerciseData, final boolean updateChildren) {
		if (exerciseData == null) {
			return;
		}
		if (exerciseData.getType() != ExerciseType.COMBINED) {
			updatePlayStatus(exerciseData.getPlayStatus() == PlayStatus.STOPPED ? PlayStatus.STOPPED : PlayStatus.OTHER);
			return;
		}
		updateExerciseName(exerciseData.getName());
		updatePlayStatus(exerciseData.getPlayStatus());

		if (updateChildren) {
			// After pressing "play", need to update single exercise data with clones of parent, so that started exercise is now the current one
			StoredExercisesRegistry.getInstance().cleanCurrentCombinedExercise();
			for (SingleExerciseData singleExerciseData : ((CombinedExerciseData) exerciseData).getSingleExerciseData()) {
				StoredExercisesRegistry.getInstance().storeAsChild(singleExerciseData, 0, false, 0);
			}
		}
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
			return exerciseStep.getRepetition().toString();
		}
	}

	/**
	 * Get the exercise data.
	 *
	 * @return the exercise data.
	 */
	public ExerciseData getExerciseData() {
		int repetition = mExerciseStep.getValue() == null ? 0 : mExerciseStep.getValue().getRepetition().getCurrentRepetition();
		List<Integer> singleExercises = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
		return new CombinedExerciseData(mExerciseName.getValue(), singleExercises, mSoundType.getValue(), mPlayStatus.getValue(), repetition);
	}

}
