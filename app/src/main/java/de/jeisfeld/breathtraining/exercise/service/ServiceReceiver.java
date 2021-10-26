package de.jeisfeld.breathtraining.exercise.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import de.jeisfeld.breathtraining.exercise.ExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;

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
	 * Handler used to execute code on the UI thread.
	 */
	private final Handler mHandler;
	/**
	 * The view model.
	 */
	private final ExerciseViewModel mExerciseViewModel;

	/**
	 * Create a broadcast intent to send the playStatus and exerciseStep to this receiver.
	 *
	 * @param playStatus The play status.
	 * @param exerciseStep The exercise step.
	 * @return The intent.
	 */
	public static Intent createIntent(final PlayStatus playStatus, final ExerciseStep exerciseStep) {
		Intent intent = new Intent(RECEIVER_ACTION);
		intent.putExtra(EXTRA_PLAY_STATUS, playStatus);
		intent.putExtra(EXTRA_EXERCISE_STEP, exerciseStep);
		return intent;
	}

	/**
	 * Default Constructor.
	 */
	public ServiceReceiver() {
		mHandler = null;
		mExerciseViewModel = null;
	}

	/**
	 * Constructor.
	 *
	 * @param handler           The handler.
	 * @param exerciseViewModel The UI model.
	 */
	public ServiceReceiver(final Handler handler, final ExerciseViewModel exerciseViewModel) {
		mHandler = handler;
		mExerciseViewModel = exerciseViewModel;
	}

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		if (mHandler == null || mExerciseViewModel == null) {
			return;
		}

		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(EXTRA_PLAY_STATUS);
		if (playStatus != null) {
			mHandler.post(() -> mExerciseViewModel.updatePlayStatus(playStatus));
		}

		ExerciseData exerciseData = ExerciseData.fromIntent(intent);
		ExerciseStep exerciseStep = (ExerciseStep) intent.getSerializableExtra(EXTRA_EXERCISE_STEP);
		if (exerciseData != null) {
			mHandler.post(() -> mExerciseViewModel.updateFromExerciseData(exerciseData, exerciseStep));
		}
		else if (exerciseStep != null) {
			mHandler.post(() -> mExerciseViewModel.updateExerciseStep(exerciseStep));
		}
	}
}
