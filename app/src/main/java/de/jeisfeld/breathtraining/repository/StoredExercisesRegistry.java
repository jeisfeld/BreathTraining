package de.jeisfeld.breathtraining.repository;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.CombinedExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.SingleExerciseData;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * A registry holding information about stored exercises.
 */
public final class StoredExercisesRegistry {
	/**
	 * The singleton instance of StoredExercisesRegistry.
	 */
	private static StoredExercisesRegistry mInstance = null;
	/**
	 * The stored exercises.
	 */
	private final SparseArray<ExerciseData> mStoredExercises = new SparseArray<>();
	/**
	 * The current single exercises as part of combined exercise.
	 */
	private final SparseArray<SingleExerciseData> mSingleExercises = new SparseArray<>();
	/**
	 * Map from exercise name to exercise.
	 */
	private final Map<String, ExerciseData> mExerciseNameMap = new HashMap<>();

	/**
	 * Create the color registry and retrieve stored entries.
	 */
	private StoredExercisesRegistry() {
		List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		for (int exerciseId : exerciseIds) {
			ExerciseData exerciseData = ExerciseData.fromId(exerciseId);
			if (exerciseData != null) {
				mStoredExercises.put(exerciseId, exerciseData);
				mExerciseNameMap.put(exerciseData.getName(), exerciseData);
			}
		}
		List<Integer> singleExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
		for (int exerciseId : singleExerciseIds) {
			SingleExerciseData exerciseData = (SingleExerciseData) ExerciseData.fromId(exerciseId);
			mSingleExercises.put(exerciseId, exerciseData);
		}
	}

	/**
	 * Get the list of stored exercises.
	 *
	 * @return The list of stored exercises.
	 */
	public List<ExerciseData> getStoredExercises() {
		List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		List<ExerciseData> allStoredExercises = new ArrayList<>();
		for (int exerciseId : exerciseIds) {
			allStoredExercises.add(mStoredExercises.get(exerciseId));
		}
		return allStoredExercises;
	}

	/**
	 * Get a stored exercise by its id.
	 *
	 * @param storedExerciseId The stored exercise id.
	 * @return The stored exercise.
	 */
	public ExerciseData getStoredExercise(final int storedExerciseId) {
		return mStoredExercises.get(storedExerciseId);
	}

	/**
	 * Get a single exercise by its id.
	 *
	 * @param singleExerciseId The single exercise id.
	 * @return The single exercise.
	 */
	public SingleExerciseData getSingleExercise(final int singleExerciseId) {
		return mSingleExercises.get(singleExerciseId);
	}

	/**
	 * Get a stored exercise by its name.
	 *
	 * @param exerciseName The stored exercise name.
	 * @return The stored exercise.
	 */
	public ExerciseData getStoredExercise(final String exerciseName) {
		return mExerciseNameMap.get(exerciseName);
	}

	/**
	 * Add or update a stored exercise in local store.
	 *
	 * @param exerciseData the stored exercise.
	 * @param name         the name for storage.
	 */
	public void addOrUpdate(final ExerciseData exerciseData, final String name) {
		exerciseData.fillIdFromStoredExercise(name);
		exerciseData.store(name);
		mStoredExercises.put(exerciseData.getId(), exerciseData);
		mExerciseNameMap.put(exerciseData.getName(), exerciseData);
	}

	/**
	 * Store exerciseData as current child exercise.
	 *
	 * @param exerciseData     The exercise data.
	 * @param exerciseId       The exercise id.
	 * @param updateParent     Flag indicating if the child should be added to the parent.
	 * @param parentExerciseId The parent exercise id.
	 */
	public void storeAsChild(final SingleExerciseData exerciseData, final int exerciseId, final boolean updateParent, final int parentExerciseId) {
		exerciseData.setId(exerciseId);
		exerciseData.store(exerciseData.getName());
		int newExerciseId = exerciseData.getId();

		if (parentExerciseId == 0) {
			List<Integer> singleExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
			if (!singleExerciseIds.contains(newExerciseId)) {
				singleExerciseIds.add(newExerciseId);
				PreferenceUtil.setSharedPreferenceIntList(R.string.key_single_exercise_ids, singleExerciseIds);
			}
			mSingleExercises.put(newExerciseId, exerciseData);
		}
		else {
			List<Integer> singleExerciseIds =
					PreferenceUtil.getIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, parentExerciseId);
			if (singleExerciseIds.contains(newExerciseId)) {
				if (updateParent) {
					CombinedExerciseData parentExercise = (CombinedExerciseData) getStoredExercise(parentExerciseId);
					if (parentExercise != null) {
						List<SingleExerciseData> siblingExercises = parentExercise.getSingleExerciseData();
						for (int i = 0; i < siblingExercises.size(); i++) {
							SingleExerciseData sibling = siblingExercises.get(i);
							if (sibling.getId() == exerciseId) {
								siblingExercises.set(i, exerciseData);
							}
						}
					}
				}
			}
			else {
				singleExerciseIds.add(newExerciseId);
				PreferenceUtil.setIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, parentExerciseId, singleExerciseIds);
				if (updateParent) {
					CombinedExerciseData parentExercise = (CombinedExerciseData) getStoredExercise(parentExerciseId);
					if (parentExercise != null) {
						parentExercise.getSingleExerciseData().add(exerciseData);
					}
				}
			}
		}

		List<Integer> storedExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		if (storedExerciseIds.contains(newExerciseId)) {
			storedExerciseIds.remove((Integer) newExerciseId);
			PreferenceUtil.setSharedPreferenceIntList(R.string.key_stored_exercise_ids, storedExerciseIds);
		}
	}


	/**
	 * Rename a stored exercise in local store.
	 *
	 * @param exerciseData the stored exercise.
	 * @param name         the new name for storage.
	 */
	public void rename(final ExerciseData exerciseData, final String name) {
		String oldName = exerciseData.getName();
		exerciseData.store(name);
		mStoredExercises.put(exerciseData.getId(), exerciseData);
		if (oldName != null && !oldName.equals(name)) {
			// name change
			mExerciseNameMap.remove(oldName);
		}
		mExerciseNameMap.put(name, exerciseData);
	}

	/**
	 * Clean the current combined exercise.
	 */
	public void cleanCurrentCombinedExercise() {
		List<Integer> singleExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
		for (int exerciseId : singleExerciseIds) {
			removeExerciseOfId(exerciseId, true, 0);
		}
	}

	/**
	 * Remove the exercise of a certain id.
	 *
	 * @param exerciseId       The id.
	 * @param isChild          Flag indicating if this is child exercise.
	 * @param parentExerciseId The exercise id of the parent.
	 */
	public void removeExerciseOfId(final int exerciseId, final boolean isChild, final int parentExerciseId) {
		if (isChild) {
			if (parentExerciseId > 0) {
				List<Integer> exerciseIds =
						PreferenceUtil.getIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, parentExerciseId);
				exerciseIds.remove((Integer) exerciseId);
				PreferenceUtil.setIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, parentExerciseId, exerciseIds);
				CombinedExerciseData parentExercise = (CombinedExerciseData) getStoredExercise(parentExerciseId);
				if (parentExercise != null) {
					parentExercise.removeSingleExerciseOfId(exerciseId);
				}
			}
			else {
				List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
				exerciseIds.remove((Integer) exerciseId);
				PreferenceUtil.setSharedPreferenceIntList(R.string.key_single_exercise_ids, exerciseIds);
				mSingleExercises.remove(exerciseId);
			}
		}
		else {
			List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
			exerciseIds.remove((Integer) exerciseId);
			PreferenceUtil.setSharedPreferenceIntList(R.string.key_stored_exercise_ids, exerciseIds);
			mStoredExercises.remove(exerciseId);
		}

		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_exercise_name, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_exercise_type, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_repetitions, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_breath_start_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_breath_end_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_in_out_relation, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_breath_in, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_in_start_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_in_end_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_in_position, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_breath_out, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_out_start_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_out_end_duration, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_out_position, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_hold_variation, exerciseId);
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_sound_type, exerciseId);

		List<Integer> childIds = PreferenceUtil.getIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, exerciseId);
		for (Integer childId : childIds) {
			if (childId != null) {
				removeExerciseOfId(childId, true, exerciseId);
			}
		}
		PreferenceUtil.removeIndexedSharedPreference(R.string.key_stored_single_exercise_ids, exerciseId);
	}

	/**
	 * Remove a stored exercise from local store.
	 *
	 * @param exerciseData The stored exercise to be deleted.
	 */
	public void remove(final ExerciseData exerciseData) {
		removeExerciseOfId(exerciseData.getId(), false, 0);
		mExerciseNameMap.remove(exerciseData.getName());
	}

	/**
	 * Get the StoredExercisesRegistry as singleton.
	 *
	 * @return The StoredExercisesRegistry as singleton.
	 */
	public static synchronized StoredExercisesRegistry getInstance() {
		if (StoredExercisesRegistry.mInstance == null) {
			StoredExercisesRegistry.mInstance = new StoredExercisesRegistry();
		}
		return StoredExercisesRegistry.mInstance;
	}

	/**
	 * Cleanup the stored color registry, so that it is recreated next time.
	 */
	public static synchronized void cleanUp() {
		StoredExercisesRegistry.mInstance = null;
	}

}
