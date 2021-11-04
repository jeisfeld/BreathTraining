package de.jeisfeld.breathtraining.exercise.data;

import java.util.Objects;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Exercise data for a single exercise.
 */
public abstract class SingleExerciseData extends ExerciseData {
	/**
	 * The default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The number of repetitions.
	 */
	private final int mRepetitions;
	/**
	 * The breath start duration.
	 */
	private final long mBreathStartDuration;

	/**
	 * Constructor.
	 *
	 * @param name                    The name of the exercise.
	 * @param repetitions             The number of repetitions.
	 * @param breathStartDuration     The breath start duration.
	 * @param soundType               The sound type.
	 * @param playStatus              The playing status.
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public SingleExerciseData(final String name, final Integer repetitions, final Long breathStartDuration, final SoundType soundType,
							  final PlayStatus playStatus, final int currentRepetitionNumber) {
		super(name, soundType, playStatus, currentRepetitionNumber);
		mRepetitions = repetitions;
		mBreathStartDuration = breathStartDuration;
	}

	/**
	 * Get the exercise steps for a certain repetition.
	 *
	 * @param repetition The repetition number (starting with 1).
	 * @return The steps for this repetition.
	 */
	@Override
	protected abstract ExerciseStep[] getStepsForRepetition(int repetition);

	/**
	 * Get the number of repetitions.
	 *
	 * @return The number of repetitions.
	 */
	@Override
	public int getRepetitions() {
		return mRepetitions;
	}

	/**
	 * Get the breath start duration.
	 *
	 * @return the breath start duration.
	 */
	public long getBreathStartDuration() {
		return mBreathStartDuration;
	}

	// OVERRIDABLE
	@Override
	public boolean store(final String name) {
		boolean isNew = super.store(name);
		PreferenceUtil.setIndexedSharedPreferenceInt(R.string.key_stored_repetitions, getId(), mRepetitions);
		PreferenceUtil.setIndexedSharedPreferenceLong(R.string.key_stored_breath_start_duration, getId(), mBreathStartDuration);
		return isNew;
	}

	// OVERRIDABLE
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SingleExerciseData)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		SingleExerciseData that = (SingleExerciseData) o;
		return mRepetitions == that.mRepetitions && mBreathStartDuration == that.mBreathStartDuration;
	}

	// OVERRIDABLE
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), mRepetitions, mBreathStartDuration);
	}
}
