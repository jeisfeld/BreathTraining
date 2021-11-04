package de.jeisfeld.breathtraining.exercise.single;

import android.os.Bundle;
import android.text.InputType;
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
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentSingleExerciseBinding;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.ExerciseType;
import de.jeisfeld.breathtraining.exercise.data.HoldPosition;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.DialogUtil;

/**
 * The fragment for managing basic breath control page.
 */
public class SingleExerciseFragment extends Fragment {
	/**
	 * The number of milliseconds per second.
	 */
	private static final double MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
	/**
	 * The view model.
	 */
	private SingleExerciseViewModel mSingleExerciseViewModel;
	/**
	 * The fragment binding.
	 */
	private FragmentSingleExerciseBinding mBinding;

	// OVERRIDABLE
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mSingleExerciseViewModel = getViewModel();
		mBinding = FragmentSingleExerciseBinding.inflate(inflater, container, false);

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
	protected SingleExerciseViewModel getViewModel() {
		return new ViewModelProvider(requireActivity()).get(SingleExerciseViewModel.class);
	}

	/**
	 * get the binding.
	 *
	 * @return The binding.
	 */
	protected FragmentSingleExerciseBinding getBinding() {
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
		mSingleExerciseViewModel.getPlayStatus().observe(getViewLifecycleOwner(), playStatus -> {
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
			case OTHER:
				mBinding.buttonStart.setVisibility(View.GONE);
				mBinding.buttonStop.setVisibility(View.GONE);
				mBinding.buttonPause.setVisibility(View.GONE);
				mBinding.buttonResume.setVisibility(View.GONE);
				mBinding.buttonBreathe.setVisibility(View.GONE);
				mBinding.tableRowCurrentRepetition.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		});

		mSingleExerciseViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			if (exerciseStep != null && exerciseStep.getStepType() != null) {
				mBinding.buttonBreathe.setText(getString(R.string.formatting_current_repetition,
						getString(exerciseStep.getStepType().getDisplayResource()), mSingleExerciseViewModel.getRepetitionString()));
			}
		});

		mBinding.buttonStart.setOnClickListener(v -> mSingleExerciseViewModel.play(getContext()));
		mBinding.buttonStop.setOnClickListener(v -> mSingleExerciseViewModel.stop(getContext()));
		mBinding.buttonPause.setOnClickListener(v -> mSingleExerciseViewModel.pause(getContext()));
		mBinding.buttonResume.setOnClickListener(v -> mSingleExerciseViewModel.play(getContext()));
		mBinding.buttonBreathe.setOnClickListener(v -> mSingleExerciseViewModel.next(getContext()));
	}

	/**
	 * Prepare the spinner for exercise type.
	 */
	private void prepareSpinnerExerciseType() {
		final Spinner spinnerExerciseType = mBinding.spinnerExerciseType;
		spinnerExerciseType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_exercise_type,
				getResources().getStringArray(R.array.values_exercise_type)));
		mSingleExerciseViewModel.getExerciseType().observe(getViewLifecycleOwner(),
				exerciseType -> spinnerExerciseType.setSelection(exerciseType.ordinal()));
		spinnerExerciseType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				ExerciseType exerciseType = ExerciseType.values()[position];
				mSingleExerciseViewModel.updateExerciseType(exerciseType);
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
	protected void prepareTextViewExerciseName() {
		mSingleExerciseViewModel.getExerciseName().observe(getViewLifecycleOwner(), name -> {
			if (name == null || name.trim().length() == 0) {
				mBinding.tableRowExerciseName.setVisibility(View.GONE);
			}
			else {
				mBinding.tableRowExerciseName.setVisibility(View.VISIBLE);
				mBinding.textViewExerciseName.setText(name.trim());
			}
			mBinding.tableRowExerciseNameEdit.setVisibility(View.GONE);
		});
	}

	/**
	 * Prepare the repetitions seekbar.
	 */
	private void prepareSeekbarRepetitions() {
		mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d",
				SingleExerciseViewModel.repetitionsSeekbarToValue(mBinding.seekBarRepetitions.getProgress())));
		mSingleExerciseViewModel.getRepetitions().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = SingleExerciseViewModel.repetitionsValueToSeekbar(value);
			mBinding.seekBarRepetitions.setProgress(seekBarValue);
			mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d", value));
		});
		mBinding.seekBarRepetitions.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
						.updateRepetitions(SingleExerciseViewModel.repetitionsSeekbarToValue(progress)));
		mBinding.textViewRepetitions.setOnClickListener(v -> {
			Integer repetitions = mSingleExerciseViewModel.getRepetitions().getValue();
			DialogUtil.displayInputDialog(requireActivity(), (dialog, text) -> {
						try {
							mSingleExerciseViewModel.updateRepetitions(Integer.parseInt(text));
						}
						catch (NumberFormatException e) {
							// ignore
						}
					}, R.string.title_edit_value, R.string.button_set_value,
					repetitions == null ? "" : Integer.toString(repetitions), InputType.TYPE_CLASS_NUMBER, R.string.text_edit_repetitions);
		});
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
	 * Prepare a seconds update dialog.
	 *
	 * @param view           The view which triggers the dialog.
	 * @param dataField      The data field to be updated.
	 * @param updateFunction The function used for updating the resource.
	 * @param textResource   The text to be displayed for the update.
	 */
	private void prepareSecondsUpdateDialog(final View view, final MutableLiveData<Long> dataField,
											final Consumer<Long> updateFunction, final int textResource) {
		view.setOnClickListener(v -> {
			Long dataValueLong = dataField.getValue();
			double dataValue = dataValueLong == null ? 0 : dataValueLong / MILLIS_PER_SECOND;
			DialogUtil.displayInputDialog(requireActivity(), (dialog, text) -> {
						try {
							updateFunction.accept(Math.round(MILLIS_PER_SECOND * Double.parseDouble(text)));
						}
						catch (NumberFormatException e) {
							// ignore
						}
					}, R.string.title_edit_value, R.string.button_set_value,
					String.format(Locale.ENGLISH, "%.1f", dataValue),
					InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textResource);
		});
	}

	/**
	 * Prepare a percentage update dialog.
	 *
	 * @param view           The view which triggers the dialog.
	 * @param dataField      The data field to be updated.
	 * @param updateFunction The function used for updating the resource.
	 * @param textResource   The text to be displayed for the update.
	 */
	private void preparePercentageUpdateDialog(final View view, final MutableLiveData<Double> dataField,
											   final Consumer<Double> updateFunction, final int textResource) {
		view.setOnClickListener(v -> {
			Double dataValueDouble = dataField.getValue();
			int dataValue = dataValueDouble == null ? 0 : (int) Math.round(dataValueDouble * 100); // MAGIC_NUMBER

			DialogUtil.displayInputDialog(requireActivity(), (dialog, text) -> {
				try {
					updateFunction.accept(Integer.parseInt(text) / 100.0); // MAGIC_NUMBER
				}
				catch (NumberFormatException e) {
					// ignore
				}
			}, R.string.title_edit_value, R.string.button_set_value, Integer.toString(dataValue), InputType.TYPE_CLASS_NUMBER, textResource);
		});
	}

	/**
	 * Prepare the breath start duration seekbar.
	 */
	private void prepareSeekbarBreathStartDuration() {
		long breathStartDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarBreathStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewBreathStartDuration, breathStartDuration);
		mSingleExerciseViewModel.getBreathStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewBreathStartDuration, duration);
		});
		mBinding.seekBarBreathStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateBreathStartDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewBreathStartDuration, mSingleExerciseViewModel.getBreathStartDuration(),
				mSingleExerciseViewModel::updateBreathStartDuration, R.string.text_edit_breath_start_duration);
	}

	/**
	 * Prepare the breath end duration seekbar.
	 */
	private void prepareSeekbarBreathEndDuration() {
		long breathEndDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarBreathEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewBreathEndDuration, breathEndDuration);
		mSingleExerciseViewModel.getBreathEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewBreathEndDuration, duration);
		});
		mBinding.seekBarBreathEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateBreathEndDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewBreathEndDuration, mSingleExerciseViewModel.getBreathEndDuration(),
				mSingleExerciseViewModel::updateBreathEndDuration, R.string.text_edit_breath_end_duration);
	}

	/**
	 * Prepare the in out relation seekbar.
	 */
	private void prepareSeekbarInOutRelation() {
		mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", mBinding.seekBarInOutRelation.getProgress()));
		mSingleExerciseViewModel.getInOutRelation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarInOutRelation.setProgress(seekBarValue);
			mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarInOutRelation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mSingleExerciseViewModel.updateInOutRelation(progress / 100.0); // MAGIC_NUMBER
				});
		preparePercentageUpdateDialog(mBinding.textViewInOutRelation, mSingleExerciseViewModel.getInOutRelation(),
				mSingleExerciseViewModel::updateInOutRelation, R.string.text_edit_in_out_relation);
	}

	/**
	 * Prepare the hold breath in checkbox.
	 */
	private void prepareCheckBoxHoldBreathIn() {
		mSingleExerciseViewModel.getHoldBreathIn().observe(getViewLifecycleOwner(), holdBreathIn -> {
			mBinding.checkBoxHoldBreathIn.setChecked(holdBreathIn);
			boolean isHoldBreathOut = Boolean.TRUE.equals(mSingleExerciseViewModel.getHoldBreathOut().getValue());
			final int holdViewStatus1 = holdBreathIn ? View.VISIBLE : View.GONE;
			final int holdViewStatus2 = isHoldBreathOut || holdBreathIn ? View.VISIBLE : View.GONE;

			mBinding.tableRowHoldInStartDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldInEndDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldInPosition.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldVariation.setVisibility(holdViewStatus2);
		});
		mBinding.checkBoxHoldBreathIn.setOnCheckedChangeListener((buttonView, isChecked) -> mSingleExerciseViewModel.updateHoldBreathIn(isChecked));
	}

	/**
	 * Prepare the hold in start duration seekbar.
	 */
	private void prepareSeekbarHoldInStartDuration() {
		long holdInStartDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldInStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldInStartDuration, holdInStartDuration);
		mSingleExerciseViewModel.getHoldInStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldInStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldInStartDuration, duration);
		});
		mBinding.seekBarHoldInStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateHoldInStartDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewHoldInStartDuration, mSingleExerciseViewModel.getHoldInStartDuration(),
				mSingleExerciseViewModel::updateHoldInStartDuration, R.string.text_edit_hold_in_start_duration);
	}

	/**
	 * Prepare the hold in end duration seekbar.
	 */
	private void prepareSeekbarHoldInEndDuration() {
		long holdInEndDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldInEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldInStartDuration, holdInEndDuration);
		mSingleExerciseViewModel.getHoldInEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldInEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldInEndDuration, duration);
		});
		mBinding.seekBarHoldInEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateHoldInEndDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewHoldInEndDuration, mSingleExerciseViewModel.getHoldInEndDuration(),
				mSingleExerciseViewModel::updateHoldInEndDuration, R.string.text_edit_hold_in_end_duration);
	}

	/**
	 * Prepare the spinner for hold in position.
	 */
	private void prepareSpinnerHoldInPosition() {
		final Spinner spinnerHoldInPosition = mBinding.spinnerHoldInPosition;
		spinnerHoldInPosition.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_hold_position)));
		mSingleExerciseViewModel.getHoldInPosition().observe(getViewLifecycleOwner(),
				holdInPosition -> spinnerHoldInPosition.setSelection(holdInPosition.ordinal()));
		spinnerHoldInPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				HoldPosition holdPosition = HoldPosition.values()[position];
				mSingleExerciseViewModel.updateHoldInPosition(holdPosition);
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
		mSingleExerciseViewModel.getHoldBreathOut().observe(getViewLifecycleOwner(), holdBreathOut -> {
			mBinding.checkBoxHoldBreathOut.setChecked(holdBreathOut);
			boolean isHoldBreathIn = Boolean.TRUE.equals(mSingleExerciseViewModel.getHoldBreathIn().getValue());
			final int holdViewStatus1 = holdBreathOut ? View.VISIBLE : View.GONE;
			final int holdViewStatus2 = isHoldBreathIn || holdBreathOut ? View.VISIBLE : View.GONE;

			mBinding.tableRowHoldOutStartDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldOutEndDuration.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldOutPosition.setVisibility(holdViewStatus1);
			mBinding.tableRowHoldVariation.setVisibility(holdViewStatus2);
		});
		mBinding.checkBoxHoldBreathOut.setOnCheckedChangeListener((buttonView, isChecked) -> mSingleExerciseViewModel.updateHoldBreathOut(isChecked));
	}

	/**
	 * Prepare the hold out start duration seekbar.
	 */
	private void prepareSeekbarHoldOutStartDuration() {
		long holdOutStartDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldOutStartDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldOutStartDuration, holdOutStartDuration);
		mSingleExerciseViewModel.getHoldOutStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldOutStartDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldOutStartDuration, duration);
		});
		mBinding.seekBarHoldOutStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateHoldOutStartDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewHoldOutStartDuration, mSingleExerciseViewModel.getHoldOutStartDuration(),
				mSingleExerciseViewModel::updateHoldOutStartDuration, R.string.text_edit_hold_out_start_duration);
	}

	/**
	 * Prepare the hold out end duration seekbar.
	 */
	private void prepareSeekbarHoldOutEndDuration() {
		long holdOutEndDuration = SingleExerciseViewModel.durationSeekbarToValue(mBinding.seekBarHoldOutEndDuration.getProgress(), false);
		setDurationText(mBinding.textViewHoldOutStartDuration, holdOutEndDuration);
		mSingleExerciseViewModel.getHoldOutEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = SingleExerciseViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldOutEndDuration.setProgress(seekbarValue);
			setDurationText(mBinding.textViewHoldOutEndDuration, duration);
		});
		mBinding.seekBarHoldOutEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mSingleExerciseViewModel
				.updateHoldOutEndDuration(SingleExerciseViewModel.durationSeekbarToValue(progress, false)));
		prepareSecondsUpdateDialog(mBinding.textViewHoldOutEndDuration, mSingleExerciseViewModel.getHoldOutEndDuration(),
				mSingleExerciseViewModel::updateHoldOutEndDuration, R.string.text_edit_hold_out_end_duration);
	}

	/**
	 * Prepare the spinner for hold out position.
	 */
	private void prepareSpinnerHoldOutPosition() {
		final Spinner spinnerHoldOutPosition = mBinding.spinnerHoldOutPosition;
		spinnerHoldOutPosition.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_hold_position)));
		mSingleExerciseViewModel.getHoldOutPosition().observe(getViewLifecycleOwner(),
				holdOutPosition -> spinnerHoldOutPosition.setSelection(holdOutPosition.ordinal()));
		spinnerHoldOutPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				HoldPosition holdPosition = HoldPosition.values()[position];
				mSingleExerciseViewModel.updateHoldOutPosition(holdPosition);
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
		mSingleExerciseViewModel.getHoldVariation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarHoldVariation.setProgress(seekBarValue);
			mBinding.textViewHoldVariation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarHoldVariation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mSingleExerciseViewModel.updateHoldVariation(progress / 100.0); // MAGIC_NUMBER
				});
		preparePercentageUpdateDialog(mBinding.textViewHoldVariation, mSingleExerciseViewModel.getHoldVariation(),
				mSingleExerciseViewModel::updateHoldVariation, R.string.text_edit_hold_variation);
	}

	/**
	 * Prepare the spinner for sound type.
	 */
	private void prepareSpinnerSoundType() {
		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_largetext,
				getResources().getStringArray(R.array.values_sound_type)));
		mSingleExerciseViewModel.getSoundType().observe(getViewLifecycleOwner(), soundType -> spinnerSoundType.setSelection(soundType.ordinal()));
		spinnerSoundType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mSingleExerciseViewModel.updateSoundType(SoundType.values()[position]);
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
		mSingleExerciseViewModel.getRepetitions().observe(getViewLifecycleOwner(), repetitions -> {
			mBinding.seekBarCurrentRepetition.setMax(repetitions - 1);
			ExerciseStep exerciseStep = mSingleExerciseViewModel.getExerciseStep().getValue();
			int currentRepetition = exerciseStep == null ? 0 : exerciseStep.getRepetition();
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, Math.min(currentRepetition - 1, repetitions - 1)));
		});
		mSingleExerciseViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, exerciseStep.getRepetition() - 1));
			mBinding.textViewCurrentRepetition.setText(String.format(Locale.getDefault(), "%d", exerciseStep.getRepetition()));
		});
		mBinding.seekBarCurrentRepetition.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					ExerciseStep exerciseStep = mSingleExerciseViewModel.getExerciseStep().getValue();
					if (exerciseStep != null) {
						mSingleExerciseViewModel.updateExerciseStep(
								new ExerciseStep(exerciseStep.getStepType(), exerciseStep.getDuration(), progress + 1));
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
									StoredExercisesRegistry.getInstance().addOrUpdate(mSingleExerciseViewModel.getExerciseData(), text);
									mSingleExerciseViewModel.updateExerciseName(text);
								},
								null, R.string.button_cancel, R.string.button_overwrite,
								R.string.message_confirm_overwrite_exercise, text);
					}
					else {
						StoredExercisesRegistry.getInstance().addOrUpdate(mSingleExerciseViewModel.getExerciseData(), text);
						mSingleExerciseViewModel.updateExerciseName(text);
					}
				}, R.string.title_dialog_save_exercise, R.string.button_save,
				mSingleExerciseViewModel.getExerciseName().getValue(), InputType.TYPE_CLASS_TEXT, R.string.message_dialog_save_exercise));
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
		void onProgressChanged(int progress);

		@Override
		default void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
			if (fromUser) {
				onProgressChanged(progress);
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
