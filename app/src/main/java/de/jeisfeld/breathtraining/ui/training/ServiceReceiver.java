package de.jeisfeld.breathtraining.ui.training;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.PlayStatus;

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
	private final TrainingViewModel mTrainingViewModel;

	/**
	 * Create a broadcast intent to send the playStatus to this receiver.
	 *
	 * @param playStatus The play status.
	 * @return The intent.
	 */
	public static Intent createIntent(final PlayStatus playStatus) {
		Intent intent = new Intent(RECEIVER_ACTION);
		intent.putExtra(EXTRA_PLAY_STATUS, playStatus);
		return intent;
	}

	/**
	 * Create a broadcast intent to send the exercise step to this receiver.
	 *
	 * @param exerciseStep The exercise step.
	 * @return The intent.
	 */
	public static Intent createIntent(final ExerciseStep exerciseStep) {
		Intent intent = new Intent(RECEIVER_ACTION);
		intent.putExtra(EXTRA_EXERCISE_STEP, exerciseStep);
		return intent;
	}

	/**
	 * Default Constructor.
	 */
	public ServiceReceiver() {
		mHandler = null;
		mTrainingViewModel = null;
	}

	/**
	 * Constructor.
	 *
	 * @param handler The handler.
	 * @param trainingViewModel The UI model.
	 */
	public ServiceReceiver(final Handler handler, final TrainingViewModel trainingViewModel) {
		mHandler = handler;
		mTrainingViewModel = trainingViewModel;
	}

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		if (mHandler == null || mTrainingViewModel == null) {
			return;
		}

		PlayStatus playStatus = (PlayStatus) intent.getSerializableExtra(EXTRA_PLAY_STATUS);
		if (playStatus != null) {
			mHandler.post(() -> mTrainingViewModel.updatePlayStatus(playStatus));
		}

		ExerciseStep exerciseStep = (ExerciseStep) intent.getSerializableExtra(EXTRA_EXERCISE_STEP);
		if (exerciseStep != null) {
			mHandler.post(() -> mTrainingViewModel.updateExerciseStep(exerciseStep));
		}
	}
}
