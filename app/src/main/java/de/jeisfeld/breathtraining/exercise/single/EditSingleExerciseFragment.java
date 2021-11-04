package de.jeisfeld.breathtraining.exercise.single;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;

/**
 * Subclass of SingleExerciseFragment, only for editing.
 */
public class EditSingleExerciseFragment extends SingleExerciseFragment {
	/**
	 * Key for the child exercise flag within the intent.
	 */
	private static final String EXTRA_CHILD_EXERCISE = "de.jeisfeld.breathtraining.CHILD_EXERCISE";
	/**
	 * Key for the exercise id within the intent.
	 */
	public static final String EXTRA_EXERCISE_ID = "de.jeisfeld.breathtraining.EXERCISE_ID";
	/**
	 * Key for the exercise id within the intent.
	 */
	public static final String EXTRA_PARENT_EXERCISE_ID = "de.jeisfeld.breathtraining.PARENT_EXERCISE_ID";
	/**
	 * Flag indicating if auto save should apply.
	 */
	private boolean mIsChildExercise;
	/**
	 * The exercise id.
	 */
	private int mExerciseId;
	/**
	 * The parent exercise id.
	 */
	private int mParentExerciseId;

	/**
	 * Navigate to this fragment.
	 *
	 * @param view             The view triggering the navigation.
	 * @param isChildExercise  Flag indicating if this is child of combined exercise.
	 * @param exerciseId       The exercise id.
	 * @param parentExerciseId The parent exercise id.
	 */
	public static void navigate(final View view, final boolean isChildExercise, final int exerciseId, final int parentExerciseId) {
		final NavController navController = Navigation.findNavController(view);
		Bundle bundle = new Bundle();
		bundle.putBoolean(EXTRA_CHILD_EXERCISE, isChildExercise);
		bundle.putInt(EXTRA_EXERCISE_ID, exerciseId);
		bundle.putInt(EXTRA_PARENT_EXERCISE_ID, parentExerciseId);
		navController.navigate(R.id.nav_edit_single_exercise, bundle);
	}

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mIsChildExercise = getArguments() != null && getArguments().getBoolean(EXTRA_CHILD_EXERCISE, false);
		mExerciseId = getArguments() == null ? 0 : getArguments().getInt(EXTRA_EXERCISE_ID, 0);
		mParentExerciseId = getArguments() == null ? 0 : getArguments().getInt(EXTRA_PARENT_EXERCISE_ID, 0);
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (mIsChildExercise) {
			getBinding().imageViewStore.setVisibility(View.GONE);
			getBinding().buttonSave.setVisibility(View.VISIBLE);
			getBinding().buttonSave.setOnClickListener(v -> {
				NavController navController = Navigation.findNavController(v);
				navController.popBackStack();
				StoredExercisesRegistry.getInstance().storeAsChild(getViewModel().getExerciseData(), mExerciseId, mParentExerciseId);
			});
			getBinding().buttonCancel.setVisibility(View.VISIBLE);
			getBinding().buttonCancel.setOnClickListener(v -> {
				NavController navController = Navigation.findNavController(v);
				navController.popBackStack();
			});
		}

		return view;
	}

	@Override
	protected final EditSingleExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(EditSingleExerciseViewModel.class);
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

	@Override
	protected final void prepareTextViewExerciseName() {
		if (mIsChildExercise) {
			getBinding().tableRowExerciseName.setVisibility(View.GONE);
			getBinding().tableRowExerciseNameEdit.setVisibility(View.VISIBLE);
			getViewModel().getExerciseName().observe(getViewLifecycleOwner(), name -> {
				String oldName = getBinding().editTextExerciseName.getText() == null ? "" : getBinding().editTextExerciseName.getText().toString();
				if (!oldName.equals(name)) {
					getBinding().editTextExerciseName.setText(name);
				}
			});
			if (mExerciseId == 0) {
				getViewModel().updateExerciseName("");
			}
			getBinding().editTextExerciseName.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
					// do nothing
				}

				@Override
				public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				}

				@Override
				public void afterTextChanged(final Editable s) {
					getViewModel().updateExerciseName(s.toString());
				}
			});
		}
		else {
			super.prepareTextViewExerciseName();
		}
	}

}
