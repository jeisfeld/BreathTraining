package de.jeisfeld.breathcontrol.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.jeisfeld.breathcontrol.sound.SoundType;

/**
 * The view model for the fragment.
 */
public class HomeViewModel extends ViewModel {
	/**
	 * The mode.
	 */
	private final MutableLiveData<Mode> mMode = new MutableLiveData<>(Mode.SIMPLE);

	/**
	 * The number of repetitions.
	 */
	private final MutableLiveData<Integer> mRepetitions = new MutableLiveData<>(10);

	/**
	 * The breath duration.
	 */
	private final MutableLiveData<Long> mBreathDuration = new MutableLiveData<>(10000L);

	/**
	 * The breath end duration.
	 */
	private final MutableLiveData<Long> mBreathEndDuration = new MutableLiveData<>(10000L);

	/**
	 * The hold start duration.
	 */
	private final MutableLiveData<Long> mHoldStartDuration = new MutableLiveData<>(0L);

	/**
	 * The hold end duration.
	 */
	private final MutableLiveData<Long> mHoldEndDuration = new MutableLiveData<>(0L);

	/**
	 * The in out relation.
	 */
	private final MutableLiveData<Double> mInOutRelation = new MutableLiveData<>(0.5); // MAGIC_NUMBER

	/**
	 * The hold variation.
	 */
	private final MutableLiveData<Double> mHoldVariation = new MutableLiveData<>(0.0); // MAGIC_NUMBER

	/**
	 * The hold position.
	 */
	private final MutableLiveData<HoldPosition> mHoldPosition = new MutableLiveData<>(HoldPosition.OUT);

	/**
	 * The flag indicating what sound should be played.
	 */
	private final MutableLiveData<SoundType> mSoundType = new MutableLiveData<>(SoundType.WORDS);

	/**
	 * Get the mode.
	 *
	 * @return The mode.
	 */
	protected LiveData<Mode> getMode() {
		return mMode;
	}

	/**
	 * Set the mode.
	 *
	 * @param mode The new mode.
	 */
	protected void updateMode(final Mode mode) {
		mMode.setValue(mode);
	}

	/**
	 * Get the number of repetitions.
	 *
	 * @return The number of repetitions.
	 */
	protected MutableLiveData<Integer> getRepetitions() {
		return mRepetitions;
	}

	/**
	 * Update the number of repetitions.
	 *
	 * @param repetitions The new number of repetitions
	 */
	protected void updateRepetitions(final int repetitions) {
		mRepetitions.setValue(repetitions);
	}

	/**
	 * Get the breath duration.
	 *
	 * @return The breath duration.
	 */
	protected MutableLiveData<Long> getBreathDuration() {
		return mBreathDuration;
	}

	/**
	 * Update the breath duration.
	 *
	 * @param breathDuration The new breath duration
	 */
	public void updateBreathDuration(final long breathDuration) {
		mBreathDuration.setValue(breathDuration);
	}

	/**
	 * Get the breath end duration.
	 *
	 * @return The breath end duration.
	 */
	protected MutableLiveData<Long> getBreathEndDuration() {
		return mBreathEndDuration;
	}

	/**
	 * Update the breath end duration.
	 *
	 * @param breathEndDuration The new breath end duration
	 */
	public void updateBreathEndDuration(final long breathEndDuration) {
		mBreathEndDuration.setValue(breathEndDuration);
	}

	/**
	 * Get the hold start duration.
	 *
	 * @return The hold start duration.
	 */
	protected MutableLiveData<Long> getHoldStartDuration() {
		return mHoldStartDuration;
	}

	/**
	 * Update the hold start duration.
	 *
	 * @param holdStartDuration The new hold start duration
	 */
	protected void updateHoldStartDuration(final long holdStartDuration) {
		mHoldStartDuration.setValue(holdStartDuration);
	}

	/**
	 * Get the hold end duration.
	 *
	 * @return The hold end duration.
	 */
	protected MutableLiveData<Long> getHoldEndDuration() {
		return mHoldEndDuration;
	}

	/**
	 * Update the hold end duration.
	 *
	 * @param holdEndDuration The new hold end duration
	 */
	protected void updateHoldEndDuration(final long holdEndDuration) {
		mHoldEndDuration.setValue(holdEndDuration);
	}

	/**
	 * Get the in/out relation.
	 *
	 * @return The in/out relation.
	 */
	protected MutableLiveData<Double> getInOutRelation() {
		return mInOutRelation;
	}

	/**
	 * Update the in/out relation.
	 *
	 * @param inOutRelation The new in/out relation
	 */
	public void updateInOutRelation(final double inOutRelation) {
		mInOutRelation.setValue(inOutRelation);
	}

	/**
	 * Get the hold variation.
	 *
	 * @return The hold variation.
	 */
	protected MutableLiveData<Double> getHoldVariation() {
		return mHoldVariation;
	}

	/**
	 * Update the hold variation.
	 *
	 * @param holdVariation The new hold variation
	 */
	protected void updateHoldVariation(final double holdVariation) {
		mHoldVariation.setValue(holdVariation);
	}

	/**
	 * Get the hold position.
	 *
	 * @return The hold position.
	 */
	public LiveData<HoldPosition> getHoldPosition() {
		return mHoldPosition;
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
	protected void updateSoundType (final SoundType soundType) {
		mSoundType.setValue(soundType);
	}

	/**
	 * Convert seekbar value to value in ms for duration.
	 *
	 * @param seekbarValue the seekbar value
	 * @param allowZero    flag indicating if value 0 is allowed
	 * @return The value
	 */
	protected static long durationSeekbarToValue(final int seekbarValue, final boolean allowZero) {
		if (allowZero && seekbarValue == 0) {
			return 0;
		}
		return Math.round(250 * Math.exp(0.025 * seekbarValue)); // MAGIC_NUMBER
	}

	/**
	 * Convert value in ms to seekbar value for duration.
	 *
	 * @param value the value in ms
	 * @return The seekbar value
	 */
	protected static int durationValueToSeekbar(final long value) {
		if (value == 0) {
			return 0;
		}
		return (int) Math.round(Math.log(value / 250.0) / 0.025); // MAGIC_NUMBER
	}

	/**
	 * Convert seekbar value to value for repetitions.
	 *
	 * @param seekbarValue the seekbar value
	 * @return The value
	 */
	protected static int repetitionsSeekbarToValue(final int seekbarValue) {
		int value = (int) Math.round(Math.exp(seekbarValue / 144.77)); // MAGIC_NUMBER
		if (value > 200) { // MAGIC_NUMBER
			value = (int) Math.round(value / 25.0) * 25; // MAGIC_NUMBER
		}
		else if (value > 40) { // MAGIC_NUMBER
			value = (int) Math.round(value / 5.0) * 5; // MAGIC_NUMBER
		}
		return value;
	}

	/**
	 * Convert value in ms to seekbar value for repetitions.
	 *
	 * @param value the value in ms
	 * @return The seekbar value
	 */
	protected static int repetitionsValueToSeekbar(final long value) {
		if (value == 0) {
			return 0;
		}
		return (int) Math.round(Math.log(value) * 144.77); // MAGIC_NUMBER
	}

	/**
	 * Enumeration for mode values.
	 */
	public enum Mode {
		/**
		 * Simple breathing mode.
		 */
		SIMPLE,
		/**
		 * Mode with holding breath.
		 */
		HOLD
	}

	/**
	 * Enumeration for hold position values.
	 */
	public enum HoldPosition {
		/**
		 * After breathing in.
		 */
		IN,
		/**
		 * After breating out.
		 */
		OUT,
		/**
		 * Both.
		 */
		BOTH
	}
}
