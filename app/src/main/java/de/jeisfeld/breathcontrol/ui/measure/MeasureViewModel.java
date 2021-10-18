package de.jeisfeld.breathcontrol.ui.measure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathcontrol.Application;
import de.jeisfeld.breathcontrol.R;
import de.jeisfeld.breathcontrol.ui.home.HomeViewModel;

/**
 * The view model for the fragment.
 */
public class MeasureViewModel extends ViewModel {
	/**
	 * Factor by which the end duration is bigger than the start duration.
	 */
	private static final double BREATH_DURATION_PROLONGATION = 1.2;
	/**
	 * The display text.
	 */
	private final MutableLiveData<String> mText = new MutableLiveData<>();
	/**
	 * Flag indicating if the current phase is breathing out.
	 */
	private final MutableLiveData<Boolean> mIsBreathingOut = new MutableLiveData<>(true);
	/**
	 * The breathe in durations.
	 */
	private final List<Long> mBreatheInDurations = new ArrayList<>();
	/**
	 * The breathe out durations.
	 */
	private final List<Long> mBreatheOutDurations = new ArrayList<>();
	/**
	 * The list of breath measurement timestamps.
	 */
	private final List<Long> mMeasurementTimes = new ArrayList<>();

	/**
	 * Get the measurement text.
	 *
	 * @return The measurement text.
	 */
	protected LiveData<String> getText() {
		return mText;
	}

	/**
	 * Get the breathing out flag.
	 *
	 * @return The breathing out flag.
	 */
	protected LiveData<Boolean> isBreathingOut() {
		return mIsBreathingOut;
	}

	/**
	 * Start the measurement.
	 */
	protected void startMeasurement() {
		mIsBreathingOut.setValue(false);
		mBreatheInDurations.clear();
		mBreatheOutDurations.clear();
		mMeasurementTimes.clear();
		mMeasurementTimes.add(System.currentTimeMillis());
		mText.setValue(Application.getAppContext().getString(R.string.message_measure));
	}

	/**
	 * Stop the measurement.
	 *
	 * @param homeViewModel The view model of the home view.
	 * @return true if measurement was successful.
	 */
	protected boolean stopMeasurement(final HomeViewModel homeViewModel) {
		changeBreath();

		if (mBreatheInDurations.size() < 1 || mBreatheOutDurations.size() < 1) {
			mText.setValue(Application.getResourceString(R.string.message_measurement_too_short));
			return false;
		}

		// Ensure only full breathe in/out cycles are counted.
		if (mBreatheOutDurations.size() > mBreatheInDurations.size()) {
			mBreatheOutDurations.remove(mBreatheOutDurations.size() - 1);
		}
		if (mBreatheInDurations.size() > mBreatheOutDurations.size()) {
			mBreatheInDurations.remove(mBreatheInDurations.size() - 1);
		}

		int size = mBreatheInDurations.size();

		// Ignore first cycle.
		if (size >= 2) {
			mBreatheInDurations.remove(0);
			mBreatheOutDurations.remove(0);
		}
		if (size >= 4) { // MAGIC_NUMBER
			// Ignore last cycle.
			mBreatheInDurations.remove(mBreatheInDurations.size() - 1);
			mBreatheOutDurations.remove(mBreatheOutDurations.size() - 1);
		}
		if (size >= 6) { // MAGIC_NUMBER
			// Ignore second cycle.
			mBreatheInDurations.remove(0);
			mBreatheOutDurations.remove(0);
		}

		size = mBreatheInDurations.size();

		long averageInDuration = mBreatheInDurations.stream().reduce(0L, Long::sum) / size;
		long averageOutDuration = mBreatheOutDurations.stream().reduce(0L, Long::sum) / size;

		long averageDuration = averageInDuration + averageOutDuration;
		double averageDurationSeconds = averageDuration / (double) TimeUnit.SECONDS.toMillis(1);
		double inOutRatio = (double) averageInDuration / averageDuration;

		mText.setValue(Application.getResourceString(R.string.message_measurement_result, averageDurationSeconds, inOutRatio * 100)); // MAGIC_NUMBER

		if (homeViewModel != null) {
			homeViewModel.updateBreathDuration(averageDuration);
			homeViewModel.updateBreathEndDuration((long) (BREATH_DURATION_PROLONGATION * averageDuration));
			homeViewModel.updateInOutRelation(inOutRatio);
			return true;
		}
		return false;
	}

	/**
	 * Change the breath direction.
	 */
	protected void changeBreath() {
		mIsBreathingOut.setValue(Boolean.FALSE.equals(mIsBreathingOut.getValue()));
		mMeasurementTimes.add(System.currentTimeMillis());
		if (mMeasurementTimes.size() >= 2) {
			long lastDuration = mMeasurementTimes.get(mMeasurementTimes.size() - 1) - mMeasurementTimes.get(mMeasurementTimes.size() - 2);
			if (Boolean.FALSE.equals(mIsBreathingOut.getValue())) {
				mBreatheOutDurations.add(lastDuration);
			}
			else {
				mBreatheInDurations.add(lastDuration);
			}
		}
	}

}
