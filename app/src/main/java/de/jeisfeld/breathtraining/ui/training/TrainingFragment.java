package de.jeisfeld.breathtraining.ui.training;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentTrainingBinding;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.ExerciseType;
import de.jeisfeld.breathtraining.exercise.HoldPosition;
import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * The fragment for managing basic breath control page.
 */
public class TrainingFragment extends Fragment {
	/**
	 * The number of milliseconds per second.
	 */
	private static final double MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
	/**
	 * The max number of milliseconds displayed as two-digit seconds with decimal point.
	 */
	private static final int MAX_TWODIGIT_SECONDS = 99900;

	/**
	 * The view model.
	 */
	private TrainingViewModel mTrainingViewModel;
	/**
	 * The fragment binding.
	 */
	private FragmentTrainingBinding mBinding;

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mTrainingViewModel = new ViewModelProvider(requireActivity()).get(TrainingViewModel.class);
		mBinding = FragmentTrainingBinding.inflate(inflater, container, false);

		final Spinner spinnerExerciseType = mBinding.spinnerExerciseType;
		spinnerExerciseType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_exercise_type,
				getResources().getStringArray(R.array.values_exercise_type)));
		mTrainingViewModel.getExerciseType().observe(getViewLifecycleOwner(),
				exerciseType -> spinnerExerciseType.setSelection(exerciseType.ordinal()));
		spinnerExerciseType.setOnItemSelectedListener(getOnExerciseTypeSelectedListener(mTrainingViewModel));

		final Spinner spinnerHoldPosition = mBinding.spinnerHoldPosition;
		spinnerHoldPosition.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_hold_position)));
		mTrainingViewModel.getHoldPosition().observe(getViewLifecycleOwner(),
				holdPosition -> spinnerHoldPosition.setSelection(holdPosition.ordinal()));
		spinnerHoldPosition.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mTrainingViewModel.updateHoldPosition(HoldPosition.values()[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});

		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_standard,
				getResources().getStringArray(R.array.values_sound_type)));
		mTrainingViewModel.getSoundType().observe(getViewLifecycleOwner(), soundType -> spinnerSoundType.setSelection(soundType.ordinal()));
		spinnerSoundType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mTrainingViewModel.updateSoundType(SoundType.values()[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});

		prepareSeekbarRepetitions();
		prepareSeekbarBreathStartDuration();
		prepareSeekbarBreathEndDuration();
		prepareSeekbarHoldStartDuration();
		prepareSeekbarHoldEndDuration();
		prepareSeekbarInOutRelation();
		prepareSeekbarHoldVariation();
		prepareSeekbarCurrentRepetition();
		prepareButtons();

		return mBinding.getRoot();
	}

	@Override
	public final void onDestroyView() {
		super.onDestroyView();
		mBinding = null;
	}

	/**
	 * Prepare the buttons.
	 */
	private void prepareButtons() {
		mTrainingViewModel.getPlayStatus().observe(getViewLifecycleOwner(), playStatus -> {
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

		mTrainingViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			if (exerciseStep != null && exerciseStep.getStepType() != null) {
				mBinding.buttonBreathe.setText(getString(R.string.formatting_current_repetition,
						getString(exerciseStep.getStepType().getDisplayResource()), mTrainingViewModel.getRepetitionString()));
			}
		});

		mBinding.buttonStart.setOnClickListener(v -> mTrainingViewModel.play(getContext()));
		mBinding.buttonStop.setOnClickListener(v -> mTrainingViewModel.stop(getContext()));
		mBinding.buttonPause.setOnClickListener(v -> mTrainingViewModel.pause(getContext()));
		mBinding.buttonResume.setOnClickListener(v -> mTrainingViewModel.play(getContext()));
		mBinding.buttonBreathe.setOnClickListener(v -> mTrainingViewModel.next(getContext()));
	}

	/**
	 * Prepare the repetitions seekbar.
	 */
	private void prepareSeekbarRepetitions() {
		mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d",
				TrainingViewModel.repetitionsSeekbarToValue(mBinding.seekBarRepetitions.getProgress())));
		mTrainingViewModel.getRepetitions().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = TrainingViewModel.repetitionsValueToSeekbar(value);
			mBinding.seekBarRepetitions.setProgress(seekBarValue);
			mBinding.textViewRepetitions.setText(String.format(Locale.getDefault(), "%d", value));
		});
		mBinding.seekBarRepetitions.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> mTrainingViewModel
						.updateRepetitions(TrainingViewModel.repetitionsSeekbarToValue(progress)));
	}

	/**
	 * Prepare the breath start duration seekbar.
	 */
	private void prepareSeekbarBreathStartDuration() {
		long breathStartDuration = TrainingViewModel.durationSeekbarToValue(mBinding.seekBarBreathStartDuration.getProgress(), false);
		if (breathStartDuration >= MAX_TWODIGIT_SECONDS) {
			mBinding.textViewBreathStartDuration.setText(String.format(Locale.getDefault(), "%.0fs", breathStartDuration / MILLIS_PER_SECOND));
		}
		else {
			mBinding.textViewBreathStartDuration.setText(String.format(Locale.getDefault(), "%.1fs", breathStartDuration / MILLIS_PER_SECOND));
		}
		mTrainingViewModel.getBreathStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = TrainingViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathStartDuration.setProgress(seekbarValue);
			if (duration >= MAX_TWODIGIT_SECONDS) {
				mBinding.textViewBreathStartDuration.setText(String.format(Locale.getDefault(), "%.0fs", duration / MILLIS_PER_SECOND));
			}
			else {
				mBinding.textViewBreathStartDuration.setText(String.format(Locale.getDefault(), "%.1fs", duration / MILLIS_PER_SECOND));
			}
		});
		mBinding.seekBarBreathStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mTrainingViewModel
				.updateBreathStartDuration(TrainingViewModel.durationSeekbarToValue(progress, false)));
	}

	/**
	 * Prepare the breath end duration seekbar.
	 */
	private void prepareSeekbarBreathEndDuration() {
		long breathEndDuration = TrainingViewModel.durationSeekbarToValue(mBinding.seekBarBreathEndDuration.getProgress(), false);
		if (breathEndDuration >= MAX_TWODIGIT_SECONDS) {
			mBinding.textViewBreathEndDuration.setText(String.format(Locale.getDefault(), "%.0fs", breathEndDuration / MILLIS_PER_SECOND));
		}
		else {
			mBinding.textViewBreathEndDuration.setText(String.format(Locale.getDefault(), "%.1fs", breathEndDuration / MILLIS_PER_SECOND));
		}
		mTrainingViewModel.getBreathEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = TrainingViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarBreathEndDuration.setProgress(seekbarValue);
			if (duration >= MAX_TWODIGIT_SECONDS) {
				mBinding.textViewBreathEndDuration.setText(String.format(Locale.getDefault(), "%.0fs", duration / MILLIS_PER_SECOND));
			}
			else {
				mBinding.textViewBreathEndDuration.setText(String.format(Locale.getDefault(), "%.1fs", duration / MILLIS_PER_SECOND));
			}
		});
		mBinding.seekBarBreathEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mTrainingViewModel
				.updateBreathEndDuration(TrainingViewModel.durationSeekbarToValue(progress, false)));

		//noinspection ConstantConditions
		mBinding.textViewBreathEndDuration.setOnClickListener(v ->
				mTrainingViewModel.updateBreathEndDuration(mTrainingViewModel.getBreathStartDuration().getValue()));
	}

	/**
	 * Prepare the hold start duration seekbar.
	 */
	private void prepareSeekbarHoldStartDuration() {
		long holdStartDuration = TrainingViewModel.durationSeekbarToValue(mBinding.seekBarHoldStartDuration.getProgress(), true);
		if (holdStartDuration >= MAX_TWODIGIT_SECONDS) {
			mBinding.textViewHoldStartDuration.setText(String.format(Locale.getDefault(), "%.0fs", holdStartDuration / MILLIS_PER_SECOND));
		}
		else {
			mBinding.textViewHoldStartDuration.setText(String.format(Locale.getDefault(), "%.1fs", holdStartDuration / MILLIS_PER_SECOND));
		}
		mTrainingViewModel.getHoldStartDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = TrainingViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldStartDuration.setProgress(seekbarValue);
			if (duration >= MAX_TWODIGIT_SECONDS) {
				mBinding.textViewHoldStartDuration.setText(String.format(Locale.getDefault(), "%.0fs", duration / MILLIS_PER_SECOND));
			}
			else {
				mBinding.textViewHoldStartDuration.setText(String.format(Locale.getDefault(), "%.1fs", duration / MILLIS_PER_SECOND));
			}
		});
		mBinding.seekBarHoldStartDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mTrainingViewModel
				.updateHoldStartDuration(TrainingViewModel.durationSeekbarToValue(progress, true)));
	}

	/**
	 * Prepare the hold end duration seekbar.
	 */
	private void prepareSeekbarHoldEndDuration() {
		long holdEndDuration = TrainingViewModel.durationSeekbarToValue(mBinding.seekBarHoldEndDuration.getProgress(), true);
		if (holdEndDuration >= MAX_TWODIGIT_SECONDS) {
			mBinding.textViewHoldEndDuration.setText(String.format(Locale.getDefault(), "%.0fs", holdEndDuration / MILLIS_PER_SECOND));
		}
		else {
			mBinding.textViewHoldEndDuration.setText(String.format(Locale.getDefault(), "%.1fs", holdEndDuration / MILLIS_PER_SECOND));
		}
		mTrainingViewModel.getHoldEndDuration().observe(getViewLifecycleOwner(), duration -> {
			int seekbarValue = TrainingViewModel.durationValueToSeekbar(duration);
			mBinding.seekBarHoldEndDuration.setProgress(seekbarValue);
			if (duration >= MAX_TWODIGIT_SECONDS) {
				mBinding.textViewHoldEndDuration.setText(String.format(Locale.getDefault(), "%.0fs", duration / MILLIS_PER_SECOND));
			}
			else {
				mBinding.textViewHoldEndDuration.setText(String.format(Locale.getDefault(), "%.1fs", duration / MILLIS_PER_SECOND));
			}
		});
		mBinding.seekBarHoldEndDuration.setOnSeekBarChangeListener((OnSeekBarProgressChangedListener) progress -> mTrainingViewModel
				.updateHoldEndDuration(TrainingViewModel.durationSeekbarToValue(progress, true)));

		//noinspection ConstantConditions
		mBinding.textViewHoldEndDuration.setOnClickListener(v ->
				mTrainingViewModel.updateHoldEndDuration(mTrainingViewModel.getHoldStartDuration().getValue()));
	}

	/**
	 * Prepare the in out relation seekbar.
	 */
	private void prepareSeekbarInOutRelation() {
		mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", mBinding.seekBarInOutRelation.getProgress()));
		mTrainingViewModel.getInOutRelation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarInOutRelation.setProgress(seekBarValue);
			mBinding.textViewInOutRelation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarInOutRelation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mTrainingViewModel.updateInOutRelation(progress / 100.0); // MAGIC_NUMBER
				});
	}

	/**
	 * Prepare the hold variation seekbar.
	 */
	private void prepareSeekbarHoldVariation() {
		mBinding.textViewHoldVariation.setText(String.format(Locale.getDefault(), "%d%%", mBinding.seekBarHoldVariation.getProgress()));
		mTrainingViewModel.getHoldVariation().observe(getViewLifecycleOwner(), value -> {
			int seekBarValue = (int) Math.round(value * 100); // MAGIC_NUMBER
			mBinding.seekBarHoldVariation.setProgress(seekBarValue);
			mBinding.textViewHoldVariation.setText(String.format(Locale.getDefault(), "%d%%", seekBarValue));
		});
		mBinding.seekBarHoldVariation.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					mTrainingViewModel.updateHoldVariation(progress / 100.0); // MAGIC_NUMBER
				});
	}

	/**
	 * Prepare the current repetition seekbar.
	 */
	private void prepareSeekbarCurrentRepetition() {
		mTrainingViewModel.getRepetitions().observe(getViewLifecycleOwner(), repetitions -> {
			mBinding.seekBarCurrentRepetition.setMax(repetitions - 1);
			ExerciseStep exerciseStep = mTrainingViewModel.getExerciseStep().getValue();
			int currentRepetition = exerciseStep == null ? 0 : exerciseStep.getRepetition();
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, Math.min(currentRepetition - 1, repetitions - 1)));
		});
		mTrainingViewModel.getExerciseStep().observe(getViewLifecycleOwner(), exerciseStep -> {
			mBinding.seekBarCurrentRepetition.setProgress(Math.max(0, exerciseStep.getRepetition() - 1));
			mBinding.textViewCurrentRepetition.setText(String.format(Locale.getDefault(), "%d", exerciseStep.getRepetition()));
		});
		mBinding.seekBarCurrentRepetition.setOnSeekBarChangeListener(
				(OnSeekBarProgressChangedListener) progress -> {
					ExerciseStep exerciseStep = mTrainingViewModel.getExerciseStep().getValue();
					if (exerciseStep != null) {
						mTrainingViewModel.updateExerciseStep(new ExerciseStep(exerciseStep.getStepType(), exerciseStep.getDuration(), progress + 1));
					}
				});
	}

	/**
	 * Get the listener on exercise type change.
	 *
	 * @param viewModel The view model.
	 * @return The listener.
	 */
	protected final OnItemSelectedListener getOnExerciseTypeSelectedListener(final TrainingViewModel viewModel) {
		return new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				ExerciseType exerciseType = ExerciseType.values()[position];
				switch (exerciseType) {
				case SIMPLE:
					mBinding.tableRowBreathEndDuration.setVisibility(View.VISIBLE);
					mBinding.tableRowHoldStartDuration.setVisibility(View.GONE);
					mBinding.tableRowHoldEndDuration.setVisibility(View.GONE);
					mBinding.tableRowHoldPosition.setVisibility(View.GONE);
					mBinding.tableRowHoldVariation.setVisibility(View.GONE);
					break;
				case HOLD:
					mBinding.tableRowBreathEndDuration.setVisibility(View.GONE);
					mBinding.tableRowHoldStartDuration.setVisibility(View.VISIBLE);
					mBinding.tableRowHoldEndDuration.setVisibility(View.VISIBLE);
					mBinding.tableRowHoldPosition.setVisibility(View.VISIBLE);
					mBinding.tableRowHoldVariation.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
				viewModel.updateExerciseType(exerciseType);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		};
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
