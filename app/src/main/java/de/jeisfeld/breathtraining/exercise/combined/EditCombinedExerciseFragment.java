package de.jeisfeld.breathtraining.exercise.combined;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.single.EditSingleExerciseFragment;

/**
 * Subclass of CombinedExerciseFragment, only for editing.
 */
public class EditCombinedExerciseFragment extends CombinedExerciseFragment {
	/**
	 * Navigate to this fragment.
	 *
	 * @param view       The view triggering the navigation.
	 * @param exerciseId The exercise id.
	 */
	public static void navigate(final View view, final int exerciseId) {
		NavController navController = Navigation.findNavController(view);
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_EXERCISE_ID, exerciseId);
		navController.navigate(R.id.nav_edit_combined_exercise, bundle);
	}

	@Override
	protected final EditCombinedExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(EditCombinedExerciseViewModel.class);
	}

	/**
	 * Prepare the buttons.
	 */
	@Override
	protected void prepareButtons(final int exerciseId) {
		getBinding().buttonStart.setVisibility(View.GONE);
		getBinding().buttonStop.setVisibility(View.GONE);
		getBinding().buttonPause.setVisibility(View.GONE);
		getBinding().buttonResume.setVisibility(View.GONE);
		getBinding().buttonBreathe.setVisibility(View.GONE);
		getBinding().buttonAdd.setVisibility(View.VISIBLE);
		getBinding().buttonAdd.setOnClickListener(v -> EditSingleExerciseFragment.navigate(v, true, 0, exerciseId));
	}
}
