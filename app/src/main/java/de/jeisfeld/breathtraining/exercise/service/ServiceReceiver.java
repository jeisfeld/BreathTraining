package de.jeisfeld.breathtraining.exercise.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import de.jeisfeld.breathtraining.exercise.combined.CombinedExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.single.SingleExerciseViewModel;

/**
 * A broadcast receiver for receiving messages from service to update UI.
 */
public class ServiceReceiver extends BroadcastReceiver {
	/**
	 * The action triggering this receiver.
	 */
	public static final String RECEIVER_ACTION = "de.jeisfeld.breathtraining.SERVICE_RECEIVER";
	/**
	 * Key for the play status.
	 */
	public static final String EXTRA_PLAY_STATUS = "de.jeisfeld.breathtraining.PLAY_STATUS";
	/**
	 * Key for the exercise step.
	 */
	public static final String EXTRA_EXERCISE_STEP = "de.jeisfeld.breathtraining.EXERCISE_STEP";
	/**
	 * Key for the exercise data within the intent.
	 */
	public static final String EXTRA_EXERCISE_DATA = "de.jeisfeld.breathtraining.EXERCISE_DATA";
	/**
	 * Handler used to execute code on the UI thread.
	 */
	private final Handler mHandler;
	/**
	 * The view model for single exercise.
	 */
	private final SingleExerciseViewModel mSingleExerciseViewModel;
	/**
	 * The view model for combined exercise.
	 */
	private final CombinedExerciseViewModel mCombinedExerciseViewModel;

	/**
	 * Create a broadcast intent to send the playStatus and exerciseStep to this receiver.
	 *
	 * @param playStatus   The play status.
	 * @param exerciseStep The exercise step.
	 * @param exerciseData The exercise data.
	 * @return The intent.
	 */
	public static Intent createIntent(final PlayStatus playStatus, final ExerciseStep exerciseStep, final ExerciseData exerciseData) {
		Intent intent = new Intent(RECEIVER_ACTION);
		intent.putExtra(EXTRA_PLAY_STATUS, playStatus);
		intent.putExtra(EXTRA_EXERCISE_STEP, exerciseStep);
		intent.putExtra(EXTRA_EXERCISE_DATA, exerciseData);
		return intent;
	}

	/**
	 * Default Constructor.
	 */
	public ServiceReceiver() {
		mHandler = null;
		mSingleExerciseViewModel = null;
		mCombinedExerciseViewModel = null;
	}

	/**
	 * Constructor.
	 *
	 * @param handler                   The handler.
	 * @param singleExerciseViewModel   The UI model for the single exercise.
	 * @param combinedExerciseViewModel The UI model for the combined exercise.
	 */
	public ServiceReceiver(final Handler handler, final SingleExerciseViewModel singleExerciseViewModel,
						   final CombinedExerciseViewModel combinedExerciseViewModel) {
		mHandler = handler;
		mSingleExerciseViewModel = singleExerciseViewModel;
		mCombinedExerciseViewModel = combinedExerciseViewModel;
	}

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		if (mHandler == null || mSingleExerciseViewModel == null || mCombinedExerciseViewModel == null) {
			return;
		}

		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(EXTRA_PLAY_STATUS);
		if (playStatus != null) {
			mHandler.post(() -> {
				mSingleExerciseViewModel.updatePlayStatus(playStatus);
				mCombinedExerciseViewModel.updatePlayStatus(playStatus);
			});
		}

		ExerciseData exerciseData = (ExerciseData) intent.getSerializableExtra(EXTRA_EXERCISE_DATA);
		if (exerciseData != null) {
			exerciseData.updatePlayStatus(playStatus);
			mHandler.post(() -> {
				mSingleExerciseViewModel.updateFromExerciseData(exerciseData);
				mCombinedExerciseViewModel.updateFromExerciseData(exerciseData);
			});
		}

		ExerciseStep exerciseStep = (ExerciseStep) intent.getSerializableExtra(EXTRA_EXERCISE_STEP);
		if (exerciseStep != null) {
			mHandler.post(() -> {
				mSingleExerciseViewModel.updateExerciseStep(exerciseStep);
				mCombinedExerciseViewModel.updateExerciseStep(exerciseStep);
			});
		}
	}
}
