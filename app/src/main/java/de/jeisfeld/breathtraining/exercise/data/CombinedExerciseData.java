package de.jeisfeld.breathtraining.exercise.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.sound.SoundType;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Exercise data for combined exercise.
 */
public class CombinedExerciseData extends ExerciseData {
	/**
	 * The default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The exercise data of the steps of this exercise.
	 */
	private final List<SingleExerciseData> mSingleExerciseData;

	/**
	 * Constructor.
	 *
	 * @param name                    The name of the exercise.
	 * @param singleExerciseIds       The single exercise ids.
	 * @param soundType               The sound type.
	 * @param playStatus              The playing status.
	 * @param currentRepetitionNumber The current repetition number.
	 */
	public CombinedExerciseData(final String name, final List<Integer> singleExerciseIds,
								final SoundType soundType, final PlayStatus playStatus, final int currentRepetitionNumber) {
		super(name, soundType, playStatus, currentRepetitionNumber);
		mSingleExerciseData = getSingleExerciseDataFromIds(singleExerciseIds);
	}

	/**
	 * Get single exercise data from their ids.
	 *
	 * @param singleExerciseIds The single exercise ids.
	 * @return The data of these exercises.
	 */
	private static List<SingleExerciseData> getSingleExerciseDataFromIds(final List<Integer> singleExerciseIds) {
		List<SingleExerciseData> singleExerciseData = new ArrayList<>();
		for (int singleExerciseId : singleExerciseIds) {
			singleExerciseData.add((SingleExerciseData) ExerciseData.fromId(singleExerciseId));
		}
		return singleExerciseData;
	}

	@Override
	public final ExerciseType getType() {
		return ExerciseType.COMBINED;
	}

	@Override
	protected final ExerciseStep[] getStepsForRepetition(final int repetition) {
		int partialRepetition = repetition;
		int partCount = 0;
		for (SingleExerciseData singleExerciseData : mSingleExerciseData) {
			partCount++;
			if (partialRepetition <= singleExerciseData.getRepetitions()) {
				ExerciseStep[] exerciseSteps = singleExerciseData.getStepsForRepetition(partialRepetition);
				for (int i = 0; i < exerciseSteps.length; i++) {
					ExerciseStep exerciseStep = exerciseSteps[i];
					exerciseSteps[i] = new ExerciseStep(exerciseStep.getStepType(), exerciseStep.getDuration(), exerciseStep.getSoundDuration(),
							new RepetitionData(repetition, getRepetitions(), exerciseStep.getRepetition().getCurrentRepetition(),
									exerciseStep.getRepetition().getTotalRepetitions(), partCount));
				}
				return exerciseSteps;
			}
			else {
				partialRepetition -= singleExerciseData.getRepetitions();
			}
		}
		return null;
	}

	@Override
	public final int getRepetitions() {
		int repetitions = 0;
		for (SingleExerciseData singleExerciseData : mSingleExerciseData) {
			repetitions += singleExerciseData.getRepetitions();
		}
		return repetitions;
	}

	/**
	 * Get the single exercise ids.
	 *
	 * @return The single exercise ids.
	 */
	public ArrayList<Integer> getSingleExerciseIds() {
		ArrayList<Integer> singleExerciseIds = new ArrayList<>();
		for (SingleExerciseData singleExerciseData : mSingleExerciseData) {
			singleExerciseIds.add(singleExerciseData.getId());
		}
		return singleExerciseIds;
	}

	/**
	 * Get the single exercise data.
	 *
	 * @return The single exercise data.
	 */
	public List<SingleExerciseData> getSingleExerciseData() {
		return mSingleExerciseData;
	}

	/**
	 * Remove a single child exercise.
	 *
	 * @param singleExerciseId The id of the child to be removed.
	 */
	public void removeSingleExerciseOfId(final int singleExerciseId) {
		SingleExerciseData foundSingleExerciseData = null;
		for (SingleExerciseData singleExerciseData : mSingleExerciseData) {
			if (singleExerciseData.getId() == singleExerciseId) {
				foundSingleExerciseData = singleExerciseData;
			}
		}
		if (foundSingleExerciseData != null) {
			mSingleExerciseData.remove(foundSingleExerciseData);
		}
	}

	@Override
	public final boolean store(final String name) {
		boolean isNew = super.store(name);
		if (isNew) {
			for (SingleExerciseData singleExerciseData : mSingleExerciseData) {
				// Store fresh copies
				StoredExercisesRegistry.getInstance().storeAsChild(singleExerciseData, 0, false, getId());
			}
		}
		PreferenceUtil.setIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, getId(), getSingleExerciseIds());
		return isNew;
	}

	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CombinedExerciseData)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		CombinedExerciseData that = (CombinedExerciseData) o;
		return Objects.equals(mSingleExerciseData, that.mSingleExerciseData);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), mSingleExerciseData);
	}
}
