package de.jeisfeld.breathtraining.repository;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
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
	 * The stored colors.
	 */
	private final SparseArray<ExerciseData> mStoredExercises = new SparseArray<>();
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
			mStoredExercises.put(exerciseId, exerciseData);
			mExerciseNameMap.put(exerciseData.getName(), exerciseData);
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
	 * Remove a stored exercise from local store.
	 *
	 * @param exerciseData The stored exercise to be deleted.
	 */
	public void remove(final ExerciseData exerciseData) {
		int exerciseId = exerciseData.getId();
		mStoredExercises.remove(exerciseId);

		List<Integer> exerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		exerciseIds.remove((Integer) exerciseId);
		PreferenceUtil.setSharedPreferenceIntList(R.string.key_stored_exercise_ids, exerciseIds);

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
