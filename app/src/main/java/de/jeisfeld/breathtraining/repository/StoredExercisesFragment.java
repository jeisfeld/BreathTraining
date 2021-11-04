package de.jeisfeld.breathtraining.repository;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.jeisfeld.breathtraining.databinding.FragmentStoredExercisesBinding;

/**
 * Fragment for management of stored exercises.
 */
public class StoredExercisesFragment extends Fragment {
	/**
	 * The fragment binding.
	 */
	private FragmentStoredExercisesBinding mBinding;

	@Override
	public final View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mBinding = FragmentStoredExercisesBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = mBinding.recyclerViewStoredExercises;
		populateRecyclerView(recyclerView);
		return mBinding.getRoot();
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
