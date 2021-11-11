package de.jeisfeld.breathtraining.exercise.combined;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentCombinedExerciseBinding;
import de.jeisfeld.breathtraining.exercise.single.EditSingleExerciseFragment;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.DialogUtil;

/**
 * Fragment for management of stored exercises.
 */
public class CombinedExerciseFragment extends Fragment {
	/**
	 * Key for the exercise id within the intent.
	 */
	public static final String EXTRA_EXERCISE_ID = "de.jeisfeld.breathtraining.EXERCISE_ID";
	/**
	 * The view model.
	 */
	private CombinedExerciseViewModel mCombinedExerciseViewModel;

	/**
	 * The fragment binding.
	 */
	private FragmentCombinedExerciseBinding mBinding;

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mCombinedExerciseViewModel = getViewModel();
		mBinding = FragmentCombinedExerciseBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = mBinding.recyclerViewStoredExercises;
		int exerciseId = getArguments() == null ? 0 : getArguments().getInt(EXTRA_EXERCISE_ID, 0);

		prepareTextViewExerciseName();
		prepareSpinnerSoundType();
		prepareButtons(exerciseId);
		prepareButtonSave();
		populateRecyclerView(recyclerView, exerciseId);
		return mBinding.getRoot();
	}

	/**
	 * Get the view model.
	 *
	 * @return The view model.
	 */
	protected CombinedExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(CombinedExerciseViewModel.class);
	}

	/**
	 * Get the binding.
	 *
	 * @return The binding.
	 */
	public FragmentCombinedExerciseBinding getBinding() {
		return mBinding;
	}

	/**
	 * Populate the recycler view for the stored exercises.
	 *
	 * @param recyclerView The recycler view.
	 * @param exerciseId   The exercise id.
	 */
	private void populateRecyclerView(final RecyclerView recyclerView, final int exerciseId) {
		CombinedExerciseViewAdapter adapter = new CombinedExerciseViewAdapter(this, exerciseId);
		ItemTouchHelper.Callback callback = new CombinedExerciseItemMoveCallback(adapter);
		ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
		adapter.setStartDragListener(touchHelper::startDrag);
		touchHelper.attachToRecyclerView(recyclerView);

		recyclerView.setAdapter(adapter);
	}

	/**
	 * Prepare the buttons.
	 *
	 * @param exerciseId The exercise id.
	 */
	protected void prepareButtons(final int exerciseId) {
		mCombinedExerciseViewModel.getPlayStatus().observe(getViewLifecycleOwner(), playStatus -> {
			switch (playStatus) {
			case STOPPED:
				mBinding.buttonStart.setVisibility(View.VISIBLE);
				mBinding.buttonStop.setVisibility(View.INVISIBLE);
				mBinding.buttonPause.setVisibility(View.INVISIBLE);
				mBinding.buttonResume.setVisibility(View.INVISIBLE);
				mBinding.buttonBreathe.setVisibility(View.INVISIBLE);
				mBinding.buttonAdd.setVisibility(View.VISIBLE);
				break;
			case PLAYING:
				mBinding.buttonStart.setVisibility(View.INVISIBLE);
				mBinding.buttonStop.setVisibility(View.VISIBLE);
				mBinding.buttonPause.setVisibility(View.VISIBLE);
				mBinding.buttonResume.setVisibility(View.INVISIBLE);
				mBinding.buttonBreathe.setVisibility(View.VISIBLE);
				mBinding.buttonAdd.setVisibility(View.INVISIBLE);
				break;
			case PAUSED:
				mBinding.buttonStart.setVisibility(View.INVISIBLE);
				mBinding.buttonStop.setVisibility(View.VISIBLE);
				mBinding.buttonPause.setVisibility(View.INVISIBLE);
				mBinding.buttonResume.setVisibility(View.VISIBLE);
				mBinding.buttonBreathe.setVisibility(View.INVISIBLE);
				mBinding.buttonAdd.setVisibility(View.VISIBLE);
				break;
			case OTHER:
				mBinding.buttonStart.setVisibility(View.GONE);
				mBinding.buttonStop.setVisibility(View.GONE);
				mBinding.buttonPause.setVisibility(View.GONE);
				mBinding.buttonResume.setVisibility(View.GONE);
				mBinding.buttonBreathe.setVisibility(View.GONE);
				mBinding.buttonAdd.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		});

		mCombinedExerciseViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			if (exerciseStep != null && exerciseStep.getStepType() != null) {
				mBinding.buttonBreathe.setText(getString(R.string.formatting_current_repetition,
						getString(exerciseStep.getStepType().getDisplayResource()), mCombinedExerciseViewModel.getRepetitionString()));
			}
		});

		mBinding.buttonStart.setOnClickListener(v -> mCombinedExerciseViewModel.play(getContext()));
		mBinding.buttonStop.setOnClickListener(v -> mCombinedExerciseViewModel.stop(getContext()));
		mBinding.buttonPause.setOnClickListener(v -> mCombinedExerciseViewModel.pause(getContext()));
		mBinding.buttonResume.setOnClickListener(v -> mCombinedExerciseViewModel.play(getContext()));
		mBinding.buttonBreathe.setOnClickListener(v -> mCombinedExerciseViewModel.next(getContext()));

		mBinding.buttonAdd.setOnClickListener(v -> EditSingleExerciseFragment.navigate(v, true, 0, exerciseId));
	}

	/**
	 * Prepare the spinner for sound type.
	 */
	private void prepareSpinnerSoundType() {
		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_largetext,
				getResources().getStringArray(R.array.values_sound_type)));
		mCombinedExerciseViewModel.getSoundType().observe(getViewLifecycleOwner(), soundType -> spinnerSoundType.setSelection(soundType.ordinal()));
		spinnerSoundType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mCombinedExerciseViewModel.updateSoundType(SoundType.values()[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});
	}

	/**
	 * Prepare the text view displaying exercise name if existing.
	 */
	private void prepareTextViewExerciseName() {
		mCombinedExerciseViewModel.getExerciseName().observe(getViewLifecycleOwner(), name -> {
			if (name == null || name.trim().length() == 0) {
				mBinding.textViewExerciseName.setVisibility(View.INVISIBLE);
			}
			else {
				mBinding.textViewExerciseName.setVisibility(View.VISIBLE);
				mBinding.textViewExerciseName.setText(name.trim());
			}
		});
	}

	/**
	 * Prepare the button to save the exercise.
	 */
	private void prepareButtonSave() {
		mBinding.imageViewStore.setOnClickListener(v -> DialogUtil.displayInputDialog(requireActivity(), (dialog, text) -> {
					if (text == null || text.trim().isEmpty()) {
						DialogUtil.displayConfirmationMessage(getActivity(),
								R.string.title_did_not_save_empty_name, R.string.message_did_not_save_empty_name);
					}
					else if (StoredExercisesRegistry.getInstance().getStoredExercise(text) != null) {
						DialogUtil.displayConfirmationMessage(requireActivity(), dialog1 -> {
									StoredExercisesRegistry.getInstance().addOrUpdate(mCombinedExerciseViewModel.getExerciseData(), text);
									mCombinedExerciseViewModel.updateExerciseName(text);
								},
								null, R.string.button_cancel, R.string.button_overwrite,
								R.string.message_confirm_overwrite_exercise, text);
					}
					else {
						StoredExercisesRegistry.getInstance().addOrUpdate(mCombinedExerciseViewModel.getExerciseData(), text);
						mCombinedExerciseViewModel.updateExerciseName(text);
					}
				}, R.string.title_dialog_save_exercise, R.string.button_save,
				mCombinedExerciseViewModel.getExerciseName().getValue(), InputType.TYPE_CLASS_TEXT, R.string.message_dialog_save_exercise));
	}
}
