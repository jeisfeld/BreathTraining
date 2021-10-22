package de.jeisfeld.breathtraining.ui.measure;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.StepType;
import de.jeisfeld.breathtraining.sound.MediaPlayer;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.ui.home.HomeViewModel;

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
	 * The flag indicating what sound should be played.
	 */
	private final MutableLiveData<SoundType> mSoundType = new MutableLiveData<>(SoundType.WORDS);
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
	 * Get the sound type.
	 *
	 * @return The sound type.
	 */
	protected MutableLiveData<SoundType> getSoundType() {
		return mSoundType;
	}

	/**
	 * Update the sound type.
	 *
	 * @param soundType The new sound type.
	 */
	protected void updateSoundType(final SoundType soundType) {
		mSoundType.setValue(soundType);
	}

	/**
	 * Start the measurement.
	 *
	 * @param context the context.
	 */
	protected void startMeasurement(final Context context) {
		if (context == null) {
			return;
		}
		mIsBreathingOut.setValue(false);
		mBreatheInDurations.clear();
		mBreatheOutDurations.clear();
		mMeasurementTimes.clear();
		mMeasurementTimes.add(System.currentTimeMillis());
		mText.setValue(context.getString(R.string.message_measure));

		SoundType soundType = mSoundType.getValue();
		if (soundType != null) {
			MediaPlayer.getInstance().play(context, MediaTrigger.ACTIVITY, soundType, StepType.INHALE, 0);
		}
	}

	/**
	 * Stop the measurement.
	 *
	 * @param homeViewModel The view model of the home view.
	 * @param context       the context.
	 * @return true if measurement was successful.
	 */
	protected boolean stopMeasurement(final Context context, final HomeViewModel homeViewModel) {
		if (context == null) {
			return false;
		}
		doChangeBreathCalculations();
		MediaPlayer.getInstance().stop();

		if (mBreatheInDurations.size() < 1 || mBreatheOutDurations.size() < 1) {
			mText.setValue(context.getString(R.string.message_measurement_too_short));
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

		mText.setValue(context.getString(R.string.message_measurement_result, averageDurationSeconds, inOutRatio * 100)); // MAGIC_NUMBER

		if (homeViewModel != null) {
			homeViewModel.updateBreathDuration(averageDuration);
			homeViewModel.updateBreathEndDuration((long) (BREATH_DURATION_PROLONGATION * averageDuration));
			homeViewModel.updateInOutRelation(inOutRatio);
			return true;
		}
		return false;
	}

	/**
	 * Do the calculations after changing Breath.
	 */
	private void doChangeBreathCalculations() {
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

	/**
	 * Change the breath direction.
	 *
	 * @param context the context.
	 */
	protected void changeBreath(final Context context) {
		if (context == null) {
			return;
		}

		doChangeBreathCalculations();

		SoundType soundType = mSoundType.getValue();
		if (soundType != null) {
			MediaPlayer.getInstance().play(context, MediaTrigger.ACTIVITY, soundType,
					Boolean.TRUE.equals(mIsBreathingOut.getValue()) ? StepType.EXHALE : StepType.INHALE, 0);
		}
	}

}
