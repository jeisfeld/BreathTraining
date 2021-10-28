package de.jeisfeld.breathtraining.measure;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.ExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.StepType;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.sound.SoundPlayer;
import de.jeisfeld.breathtraining.sound.SoundType;

/**
 * The view model for the fragment.
 */
public class MeasureViewModel extends ViewModel {
	/**
	 * The display text, upper part
	 */
	private final MutableLiveData<String> mText1 = new MutableLiveData<>();
	/**
	 * The flag indicating if the "use values" button should be visible.
	 */
	private final MutableLiveData<Boolean> mIsButtonUseValuesVisible = new MutableLiveData<>(false);
	/**
	 * The display text, lower part
	 */
	private final MutableLiveData<String> mText2 = new MutableLiveData<>();
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
	 * The measured average duration.
	 */
	private Long mAverageDuration = null;
	/**
	 * The measured average in/out relation
	 */
	private Double mAverageInOutRelation = null;

	/**
	 * Get the measurement text, upper part.
	 *
	 * @return The measurement text, upper part.
	 */
	protected LiveData<String> getText1() {
		return mText1;
	}

	/**
	 * Get the flag if "use values" button is visible.
	 *
	 * @return The flag if "use values" button is visible.
	 */
	protected LiveData<Boolean> getIsButtonUseValuesVisible() {
		return mIsButtonUseValuesVisible;
	}

	/**
	 * Get the measurement text, lower part.
	 *
	 * @return The measurement text, lower part.
	 */
	protected LiveData<String> getText2() {
		return mText2;
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
		mText1.setValue(context.getString(R.string.message_measure));

		SoundType soundType = mSoundType.getValue();
		if (soundType != null) {
			SoundPlayer.getInstance().play(context, MediaTrigger.ACTIVITY, soundType, StepType.INHALE);
		}
	}

	/**
	 * Stop the measurement.
	 *
	 * @param context the context.
	 */
	protected void stopMeasurement(final Context context) {
		if (context == null) {
			return;
		}
		doChangeBreathCalculations();
		SoundPlayer.getInstance().stop();

		if (mBreatheInDurations.size() < 1 || mBreatheOutDurations.size() < 1) {
			mText1.setValue(context.getString(R.string.message_measurement_too_short));
			mText2.setValue(null);
			mIsButtonUseValuesVisible.setValue(false);
			mAverageDuration = null;
			mAverageInOutRelation = null;
			return;
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

		mAverageDuration = averageInDuration + averageOutDuration;
		double averageDurationSeconds = mAverageDuration / (double) TimeUnit.SECONDS.toMillis(1);
		mAverageInOutRelation = (double) averageInDuration / mAverageDuration;

		mText1.setValue(
				context.getString(R.string.message_measurement_result_1, averageDurationSeconds, mAverageInOutRelation * 100)); // MAGIC_NUMBER
		mText2.setValue(context.getString(R.string.message_measurement_result_2));
		mIsButtonUseValuesVisible.setValue(true);
	}

	/**
	 * Use the measured values.
	 *
	 * @param activity The triggering activity.
	 */
	protected void useValues(final FragmentActivity activity) {
		ExerciseViewModel exerciseViewModel = new ViewModelProvider(activity).get(ExerciseViewModel.class);
		if (mAverageDuration != null && mAverageInOutRelation != null) {
			exerciseViewModel.updateBreathStartDuration(mAverageDuration);
			exerciseViewModel.updateBreathEndDuration(mAverageDuration);
			exerciseViewModel.updateInOutRelation(mAverageInOutRelation);

			NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
			navController.popBackStack(R.id.nav_exercise, false);
		}
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
			SoundPlayer.getInstance().play(context, MediaTrigger.ACTIVITY, soundType,
					Boolean.TRUE.equals(mIsBreathingOut.getValue()) ? StepType.EXHALE : StepType.INHALE);
		}
	}

}
