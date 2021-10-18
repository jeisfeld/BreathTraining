package de.jeisfeld.breathcontrol.ui.measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import de.jeisfeld.breathcontrol.R;
import de.jeisfeld.breathcontrol.databinding.FragmentMeasureBinding;
import de.jeisfeld.breathcontrol.ui.home.HomeViewModel;

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
		mMeasureViewModel =
				new ViewModelProvider(requireActivity()).get(MeasureViewModel.class);

		mBinding = FragmentMeasureBinding.inflate(inflater, container, false);

		final TextView textView = mBinding.textMeasurement;
		mMeasureViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

		final Button buttonStart = mBinding.buttonStart;
		final Button buttonStop = mBinding.buttonStop;
		final Button buttonBreathe = mBinding.buttonBreathe;

		mMeasureViewModel.isBreathingOut().observe(getViewLifecycleOwner(),
				isBreathingOut -> buttonBreathe.setText(isBreathingOut ? R.string.button_breathe_out : R.string.button_breathe_in));

		buttonStart.setOnClickListener(v -> {
			buttonStart.setVisibility(View.INVISIBLE);
			buttonStop.setVisibility(View.VISIBLE);
			buttonBreathe.setVisibility(View.VISIBLE);
			mMeasureViewModel.startMeasurement();
		});

		buttonStop.setOnClickListener(v -> {
			HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
			boolean success = mMeasureViewModel.stopMeasurement(homeViewModel);
			if (success) {
				Navigation.findNavController(v).navigate(R.id.nav_home);
			}
			buttonStart.setVisibility(View.VISIBLE);
			buttonStop.setVisibility(View.INVISIBLE);
			buttonBreathe.setVisibility(View.INVISIBLE);
		});

		buttonBreathe.setOnClickListener(v -> mMeasureViewModel.changeBreath());

		return mBinding.getRoot();
	}

	@Override
	public final void onDestroyView() {
		super.onDestroyView();
		mBinding = null;
	}
}
