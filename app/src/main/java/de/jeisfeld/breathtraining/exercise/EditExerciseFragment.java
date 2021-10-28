package de.jeisfeld.breathtraining.exercise;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;

/**
 * Subclass of ExerciseFragment, only for editing.
 */
public class EditExerciseFragment extends ExerciseFragment {
	@Override
	protected final ExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(EditExerciseViewModel.class);
	}

	/**
	 * Prepare the buttons.
	 */
	@Override
	protected void prepareButtons() {
		getBinding().buttonStart.setVisibility(View.GONE);
		getBinding().buttonStop.setVisibility(View.GONE);
		getBinding().buttonPause.setVisibility(View.GONE);
		getBinding().buttonResume.setVisibility(View.GONE);
		getBinding().buttonBreathe.setVisibility(View.GONE);
		getBinding().tableRowCurrentRepetition.setVisibility(View.GONE);
	}
}
