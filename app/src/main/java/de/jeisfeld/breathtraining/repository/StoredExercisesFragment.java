package de.jeisfeld.breathtraining.repository;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.jeisfeld.breathtraining.R;

/**
 * Fragment for management of stored exercises.
 */
public class StoredExercisesFragment extends Fragment {
	@Override
	public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_stored_exercises, container, false);
		final RecyclerView recyclerView = root.findViewById(R.id.recyclerViewStoredExercises);
		populateRecyclerView(recyclerView);
		return root;
	}

	/**
	 * Populate the recycler view for the stored exercises.
	 *
	 * @param recyclerView The recycler view.
	 */
	private void populateRecyclerView(final RecyclerView recyclerView) {
		StoredExercisesViewAdapter adapter = new StoredExercisesViewAdapter(this);
		ItemTouchHelper.Callback callback = new StoredExercisesItemMoveCallback(adapter);
		ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
		adapter.setStartDragListener(touchHelper::startDrag);
		touchHelper.attachToRecyclerView(recyclerView);

		recyclerView.setAdapter(adapter);
	}
}
