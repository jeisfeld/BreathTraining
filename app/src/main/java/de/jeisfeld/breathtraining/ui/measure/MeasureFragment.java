package de.jeisfeld.breathtraining.ui.measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.databinding.FragmentMeasureBinding;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.ui.home.HomeViewModel;

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

		final TextView textView = mBinding.textMeasurement;
		mMeasureViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

		final Spinner spinnerSoundType = mBinding.spinnerSoundType;
		spinnerSoundType.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_item_bigtext,
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
			HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
			boolean success = mMeasureViewModel.stopMeasurement(getContext(), homeViewModel);
			if (success) {
				Navigation.findNavController(v).navigate(R.id.nav_home);
			}
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
