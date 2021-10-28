package de.jeisfeld.breathtraining.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentExerciseBinding;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.ExerciseType;
import de.jeisfeld.breathtraining.exercise.data.HoldPosition;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.DialogUtil;
import de.jeisfeld.breathtraining.util.DialogUtil.RequestInputDialogFragment.RequestInputDialogListener;

/**
 * The fragment for managing basic breath control page.
 */
public class ExerciseFragment extends Fragment {
	/**
	 * The number of milliseconds per second.
	 */
	private static final double MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
	/**
	 * The view model.
	 */
	private ExerciseViewModel mExerciseViewModel;
	/**
	 * The fragment binding.
	 */
	private FragmentExerciseBinding mBinding;

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mExerciseViewModel = getViewModel();
		mBinding = FragmentExerciseBinding.inflate(inflater, container, false);

		prepareSpinnerExerciseType();
		prepareTextViewExerciseName();
		prepareSeekbarRepetitions();
		prepareSeekbarBreathStartDuration();
		prepareSeekbarBreathEndDuration();
		prepareSeekbarInOutRelation();
		prepareCheckBoxHoldBreathIn();
		prepareSeekbarHoldInStartDuration();
		prepareSeekbarHoldInEndDuration();
		prepareSpinnerHoldInPosition();
		prepareCheckBoxHoldBreathOut();
		prepareSeekbarHoldOutStartDuration();
		prepareSeekbarHoldOutEndDuration();
		prepareSpinnerHoldOutPosition();
		prepareSeekbarHoldVariation();
		prepareSpinnerSoundType();
		prepareSeekbarCurrentRepetition();
		prepareButtons();
		prepareButtonSave();

		return mBinding.getRoot();
	}

	/**
	 * Get the view model.
	 *
	 * @return The view model.
	 */
	protected ExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
	}

	/**
	 * get the binding.
	 *
	 * @return The binding.
	 */
	protected FragmentExerciseBinding getBinding() {
		return mBinding;
	}

	@Override
	public final void onDestroyView() {
		super.onDestroyView();
		mBinding = null;
	}

	/**
	 * Prepare the buttons.
	 */
	protected void prepareButtons() {
		mExerciseViewModel.getPlayStatus().observe(getViewLifecycleOwner(), playStatus -> {
			switch (playStatus) {
			case STOPPED:
				mBinding.buttonStart.setVisibility(View.VISIBLE);
				mBinding.buttonStop.setVisibility(View.INVISIBLE);
				mBinding.buttonPause.setVisibility(View.INVISIBLE);
				mBinding.buttonResume.setVisibility(View.INVISIBLE);
				mBinding.buttonBreathe.setVisibility(View.INVISIBLE);
				mBinding.tableRowCurrentRepetition.setVisibility(View.GONE);
				break;
			case PLAYING:
				mBinding.buttonStart.setVisibility(View.INVISIBLE);
				mBinding.buttonStop.setVisibility(View.VISIBLE);
				mBinding.buttonPause.setVisibility(View.VISIBLE);
				mBinding.buttonResume.setVisibility(View.INVISIBLE);
				mBinding.buttonBreathe.setVisibility(View.VISIBLE);
				mBinding.tableRowCurrentRepetition.setVisibility(View.VISIBLE);
				break;
			case PAUSED:
				mBinding.buttonStart.setVisibility(View.INVISIBLE);
				mBinding.buttonStop.setVisibility(View.VISIBLE);
				mBinding.buttonPause.setVisibility(View.INVISIBLE);
				mBinding.buttonResume.setVisibility(View.VISIBLE);
				mBinding.buttonBreathe.setVisibility(View.INVISIBLE);
				mBinding.tableRowCurrentRepetition.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		});

		mExerciseViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			if (exerciseStep != null && exerciseStep.getStepType() != null) {
				mBinding.buttonBreathe.setText(getString(R.string.formatting_current_repetition,
						getString(exerciseStep.getStepType().getDisplayResource()), mExerciseViewModel.getRepetitionString()));
			}
		});

		mBinding.buttonStart.setOnClickListener(v -> mExerciseViewModel.play(getContext()));
		mBinding.buttonStop.setOnClickListener(v -> mExerciseViewModel.stop(getContext()));
		mBinding.buttonPause.setOnClickListener(v -> mExerciseViewModel.pause(getContext()));
		mBinding.buttonResume.setOnClickListener(v -> mExerciseViewModel.play(getContext()));
		mBinding.buttonBreathe.setOnClickListener(v -> mExerciseViewModel.next(getContext()));
	}

	/**
	 * Prepare the spinner for exercise type.
	 */
	private void prepareSpinnerExerciseType() {
		final Spinner spinnerExerciseType = mBinding.spinnerExerciseType;
		spinnerExerciseType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_exercise_type,
				getResources().getStringArray(R.array.values_exercise_type)));
		mExerciseViewModel.getExerciseType().observe(getViewLifecycleOwner(),
				exerciseType -> spinnerExerciseType.setSelection(exerciseType.ordinal()));
		spinnerExerciseType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				ExerciseType exerciseType = ExerciseType.values()[position];
				mExerciseViewModel.updateExerciseType(exerciseType);
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
		mExerciseViewModel.getExerciseName().observe(getViewLifecycleOwner(), name -> {
			if (name == null || name.trim().length() == 0) {
				mBinding.tableRowExerciseName.setVisibility(View.GONE);
			}
			else {
				mBinding.tableRowExerciseName.setVisibility(View.VISIBLE);
				mBinding.textViewExerciseName.setText(name.trim());
			}
		});
	}

	/**
	 * Prepare the repetitions seekbar.
	 */
	private void prepareSeekbarRepetitions() {
		mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d",
				ExerciseViewModel.repetitionsSeekbarToValue(mBinding.seekBarRepetitions.getProgress())));
		mExerciseViewModel.getRepetitions().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = ExerciseViewModel.repetitionsValueToSeekbar(value);
			mBinding.seekBarRepetitions.setProgress(seekBarValue);
			mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d", value));
		});
		mBinding.seekBarRepetitions.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
						.updateRepetitions(ExerciseViewModel.repetitionsSeekbarToValue(progress)));
	}

	/**
	 * Set the duration text field nest to a seekbar.
	 *
	 * @param textView The text field.
	 * @param duration The duration to be set.
	 */
	private static void setDurationText(final TextView textView, final long duration) {
		String format = duration > 14750 ? "%.0fs" : "%.1fs"; // MAGIC_NUMBER
		textView.setText(String.format(Locale.getDefault(), format, duration / MILLIS_PER_SECOND));
	}

	/**
	 * Prepare the breath start duration seekbar.
	 */
	private void prepareSeekbarBreathStartDuration() {
		long breathStartDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarBreathStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewBreathStartDuration, breathStartDuration);
		mExerciseViewModel.getBreathStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewBreathStartDuration, duration);
		});
		mBinding.seekBarBreathStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateBreathStartDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the breath end duration seekbar.
	 */
	private void prepareSeekbarBreathEndDuration() {
		long breathEndDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarBreathEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewBreathEndDuration, breathEndDuration);
		mExerciseViewModel.getBreathEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewBreathEndDuration, duration);
		});
		mBinding.seekBarBreathEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateBreathEndDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the in out relation seekbar.
	 */
	private void prepareSeekbarInOutRelation() {
		mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", mBinding.seekBarInOutRelation.getProgress()));
		mExerciseViewModel.getInOutRelation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarInOutRelation.setProgress(seekBarValue);
			mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarInOutRelation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mExerciseViewModel.updateInOutRelation(progress / 100.0); // MAGIC_NUMBER
				});
	}

	/**
	 * Prepare the hold breath in checkbox.
	 */
	private void prepareCheckBoxHoldBreathIn() {
		mExerciseViewModel.getHoldBreathIn().observe(getViewLifecycleOwner(), holdBreathIn -> {
			mBinding.checkBoxHoldBreathIn.setChecked(holdBreathIn);
			boolean isHoldBreathOut = Boolean.TRUE.equals(mExerciseViewModel.getHoldBreathOut().getValue());
			final int holdViewStatus1 = holdBreathIn ? View.VISIBLE : View.GONE;
			final int holdViewStatus2 = isHoldBreathOut || holdBreathIn ? View.VISIBLE : View.GONE;

			mBinding.tableRowHoldInStartDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldInEndDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldInPosition.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldVariation.setVisibility(holdViewStatus2);
		});
		mBinding.checkBoxHoldBreathIn.setOnCheckedChangeListener((buttonView, isChecked) -> mExerciseViewModel.updateHoldBreathIn(isChecked));
	}

	/**
	 * Prepare the hold in start duration seekbar.
	 */
	private void prepareSeekbarHoldInStartDuration() {
		long holdInStartDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldInStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldInStartDuration, holdInStartDuration);
		mExerciseViewModel.getHoldInStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldInStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldInStartDuration, duration);
		});
		mBinding.seekBarHoldInStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateHoldInStartDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the hold in end duration seekbar.
	 */
	private void prepareSeekbarHoldInEndDuration() {
		long holdInEndDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldInEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldInStartDuration, holdInEndDuration);
		mExerciseViewModel.getHoldInEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldInEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldInEndDuration, duration);
		});
		mBinding.seekBarHoldInEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateHoldInEndDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the spinner for hold in position.
	 */
	private void prepareSpinnerHoldInPosition() {
		final Spinner spinnerHoldInPosition = mBinding.spinnerHoldInPosition;
		spinnerHoldInPosition.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_hold_position)));
		mExerciseViewModel.getHoldInPosition().observe(getViewLifecycleOwner(),
				holdInPosition -> spinnerHoldInPosition.setSelection(holdInPosition.ordinal()));
		spinnerHoldInPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				HoldPosition holdPosition = HoldPosition.values()[position];
				mExerciseViewModel.updateHoldInPosition(holdPosition);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});
	}

	/**
	 * Prepare the hold breath in checkbox.
	 */
	private void prepareCheckBoxHoldBreathOut() {
		mExerciseViewModel.getHoldBreathOut().observe(getViewLifecycleOwner(), holdBreathOut -> {
			mBinding.checkBoxHoldBreathOut.setChecked(holdBreathOut);
			boolean isHoldBreathIn = Boolean.TRUE.equals(mExerciseViewModel.getHoldBreathIn().getValue());
			final int holdViewStatus1 = holdBreathOut ? View.VISIBLE : View.GONE;
			final int holdViewStatus2 = isHoldBreathIn || holdBreathOut ? View.VISIBLE : View.GONE;

			mBinding.tableRowHoldOutStartDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldOutEndDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldOutPosition.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldVariation.setVisibility(holdViewStatus2);
		});
		mBinding.checkBoxHoldBreathOut.setOnCheckedChangeListener((buttonView, isChecked) -> mExerciseViewModel.updateHoldBreathOut(isChecked));
	}

	/**
	 * Prepare the hold out start duration seekbar.
	 */
	private void prepareSeekbarHoldOutStartDuration() {
		long holdOutStartDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldOutStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldOutStartDuration, holdOutStartDuration);
		mExerciseViewModel.getHoldOutStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldOutStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldOutStartDuration, duration);
		});
		mBinding.seekBarHoldOutStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateHoldOutStartDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the hold out end duration seekbar.
	 */
	private void prepareSeekbarHoldOutEndDuration() {
		long holdOutEndDuration = ExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldOutEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldOutStartDuration, holdOutEndDuration);
		mExerciseViewModel.getHoldOutEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = ExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldOutEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldOutEndDuration, duration);
		});
		mBinding.seekBarHoldOutEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mExerciseViewModel
				.updateHoldOutEndDuration(ExerciseViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the spinner for hold out position.
	 */
	private void prepareSpinnerHoldOutPosition() {
		final Spinner spinnerHoldOutPosition = mBinding.spinnerHoldOutPosition;
		spinnerHoldOutPosition.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_hold_position)));
		mExerciseViewModel.getHoldOutPosition().observe(getViewLifecycleOwner(),
				holdOutPosition -> spinnerHoldOutPosition.setSelection(holdOutPosition.ordinal()));
		spinnerHoldOutPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				HoldPosition holdPosition = HoldPosition.values()[position];
				mExerciseViewModel.updateHoldOutPosition(holdPosition);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});
	}

	/**
	 * Prepare the hold variation seekbar.
	 */
	private void prepareSeekbarHoldVariation() {
		mBinding.textViewHoldVariation.setText(String.format(Locale.getDefault(), "%d%%", mBinding.seekBarHoldVariation.getProgress()));
		mExerciseViewModel.getHoldVariation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarHoldVariation.setProgress(seekBarValue);
			mBinding.textViewHoldVariation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarHoldVariation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mExerciseViewModel.updateHoldVariation(progress / 100.0); // MAGIC_NUMBER
				});
	}

	/**
	 * Prepare the spinner for sound type.
	 */
	private void prepareSpinnerSoundType() {
		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_largetext,
				getResources().getStringArray(R.array.values_sound_type)));
		mExerciseViewModel.getSoundType().observe(getViewLifecycleOwner(), soundType -> spinnerSoundType.setSelection(soundType.ordinal()));
		spinnerSoundType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mExerciseViewModel.updateSoundType(SoundType.values()[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});
	}

	/**
	 * Prepare the current repetition seekbar.
	 */
	private void prepareSeekbarCurrentRepetition() {
		mExerciseViewModel.getRepetitions().observe(getViewLifecycleOwner(), repetitions -> {
			mBinding.seekBarCurrentRepetition.setMax(repetitions - 1);
			ExerciseStep exerciseStep = mExerciseViewModel.getExerciseStep().getValue();
			int currentRepetition = exerciseStep == null ? 0 : exerciseStep.getRepetition();
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, Math.min(currentRepetition - 1, repetitions - 1)));
		});
		mExerciseViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, exerciseStep.getRepetition() - 1));
			mBinding.textViewCurrentRepetition.setText(String.format(Locale.getDefault(), "%d", exerciseStep.getRepetition()));
		});
		mBinding.seekBarCurrentRepetition.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					ExerciseStep exerciseStep = mExerciseViewModel.getExerciseStep().getValue();
					if (exerciseStep != null) {
						mExerciseViewModel.updateExerciseStep(new ExerciseStep(exerciseStep.getStepType(), exerciseStep.getDuration(), progress + 1));
					}
				});
	}

	/**
	 * Prepare the button to save the exercise.
	 */
	private void prepareButtonSave() {
		mBinding.imageViewStore.setOnClickListener(v -> DialogUtil.displayInputDialog(requireActivity(), new RequestInputDialogListener() {
					@Override
					public void onDialogPositiveClick(final DialogFragment dialog, final String text) {
						if (text == null || text.trim().isEmpty()) {
							DialogUtil.displayConfirmationMessage(getActivity(),
									R.string.title_did_not_save_empty_name, R.string.message_did_not_save_empty_name);
						}
						else if (StoredExercisesRegistry.getInstance().getStoredExercise(text) != null) {
							DialogUtil.displayConfirmationMessage(requireActivity(), dialog1 -> {
										StoredExercisesRegistry.getInstance().addOrUpdate(mExerciseViewModel.getExerciseData(), text);
										mExerciseViewModel.updateExerciseName(text);
									},
									null, R.string.button_cancel, R.string.button_overwrite,
									R.string.message_confirm_overwrite_exercise, text);
						}
						else {
							StoredExercisesRegistry.getInstance().addOrUpdate(mExerciseViewModel.getExerciseData(), text);
							mExerciseViewModel.updateExerciseName(text);
						}
					}

					@Override
					public void onDialogNegativeClick(final DialogFragment dialog) {
						// do nothing
					}
				}, R.string.title_dialog_save_exercise, R.string.button_save,
				mExerciseViewModel.getExerciseName().getValue(), R.string.message_dialog_save_exercise));
	}

	/**
	 * Variant of OnSeekBarChangeListener that reacts only on progress change from user.
	 */
	private interface OnSeekBarProgressChangedListener extends OnSeekBarChangeListener {
		/**
		 * Callback on progress change from user.
		 *
		 * @param progress The progress.
		 */
		void onProgessChanged(int progress);

		@Override
		default void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
			if (fromUser) {
				onProgessChanged(progress);
			}
		}

		@Override
		default void onStartTrackingTouch(final SeekBar seekBar) {
			// do nothing
		}

		@Override
		default void onStopTrackingTouch(final SeekBar seekBar) {
			// do nothing
		}
	}

}
