package de.jeisfeld.breathtraining.repository;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

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
	 * Create the color registry and retrieve stored entries.
	 */
	private StoredExercisesRegistry() {
		List<Integer> exercuseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		for (int exerciseId : exercuseIds) {
			mStoredExercises.put(exerciseId, ExerciseData.fromId(exerciseId));
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
	 * @param storedExerciseId The stored color id.
	 * @return The stored exercise.
	 */
	public ExerciseData getStoredExercise(final int storedExerciseId) {
		return mStoredExercises.get(storedExerciseId);
	}

	/**
	 * Add or update a stored exercise in local store.
	 *
	 * @param exerciseData the stored exercise.
	 * @param name         the name for storage.
	 */
	public void addOrUpdate(final ExerciseData exerciseData, final String name) {
		exerciseData.store(name);
		mStoredExercises.put(exerciseData.getId(), exerciseData);
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
