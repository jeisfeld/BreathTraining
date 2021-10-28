package de.jeisfeld.breathtraining.measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentMeasureBinding;
import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * The fragment for measuring breath duration.
 */
public class MeasureFragment extends Fragment {
	/**
	 * The view model.
	 */
	private MeasureViewModel mMeasureViewModel;
	/**
	 * The fragment binding.
	 */
	private FragmentMeasureBinding mBinding;

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mMeasureViewModel = new ViewModelProvider(requireActivity()).get(MeasureViewModel.class);

		mBinding = FragmentMeasureBinding.inflate(inflater, container, false);

		mMeasureViewModel.getText1().observe(getViewLifecycleOwner(), mBinding.textMeasurement1::setText);
		mMeasureViewModel.getText2().observe(getViewLifecycleOwner(), text -> {
			mBinding.textMeasurement2.setText(text);
			mBinding.textMeasurement2.setVisibility(text == null ? View.GONE : View.VISIBLE);
		});

		mMeasureViewModel.getIsButtonUseValuesVisible().observe(getViewLifecycleOwner(), isVisible ->
				mBinding.buttonUseValues.setVisibility(isVisible ? View.VISIBLE : View.GONE));
		mBinding.buttonUseValues.setOnClickListener(v -> mMeasureViewModel.useValues(requireActivity()));

		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_largetext,
				getResources().getStringArray(R.array.values_sound_type)));
		mMeasureViewModel.getSoundType().observe(getViewLifecycleOwner(), soundType -> spinnerSoundType.setSelection(soundType.ordinal()));
		spinnerSoundType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				mMeasureViewModel.updateSoundType(SoundType.values()[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// do nothing
			}
		});

		final Button buttonStart = mBinding.buttonStart;
		final Button buttonStop = mBinding.buttonStop;
		final Button buttonBreathe = mBinding.buttonBreathe;

		mMeasureViewModel.isBreathingOut().observe(getViewLifecycleOwner(),
				isBreathingOut -> buttonBreathe.setText(isBreathingOut ? R.string.text_exhale : R.string.text_inhale));

		buttonStart.setOnClickListener(v -> {
			buttonStart.setVisibility(View.INVISIBLE);
			buttonStop.setVisibility(View.VISIBLE);
			buttonBreathe.setVisibility(View.VISIBLE);
			mMeasureViewModel.startMeasurement(getContext());
		});

		buttonStop.setOnClickListener(v -> {
			mMeasureViewModel.stopMeasurement(getContext());
			buttonStart.setVisibility(View.VISIBLE);
			buttonStop.setVisibility(View.INVISIBLE);
			buttonBreathe.setVisibility(View.INVISIBLE);
		});

		buttonBreathe.setOnClickListener(v -> mMeasureViewModel.changeBreath(getContext()));

		return mBinding.getRoot();
	}

	@Override
	public final void onDestroyView() {
		super.onDestroyView();
		mBinding = null;
	}
}
