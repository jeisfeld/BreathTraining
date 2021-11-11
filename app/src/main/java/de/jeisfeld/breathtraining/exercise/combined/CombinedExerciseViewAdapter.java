package de.jeisfeld.breathtraining.exercise.combined;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.jeisfeld.breathtraining.MainActivity;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.data.CombinedExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.data.SingleExerciseData;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService.ServiceCommand;
import de.jeisfeld.breathtraining.exercise.single.EditSingleExerciseFragment;
import de.jeisfeld.breathtraining.exercise.single.EditSingleExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.single.SingleExerciseViewModel;
import de.jeisfeld.breathtraining.repository.StoredExercisesRegistry;
import de.jeisfeld.breathtraining.util.DialogUtil;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Adapter for the RecyclerView that allows to sort stored exercises.
 */
public class CombinedExerciseViewAdapter extends RecyclerView.Adapter<CombinedExerciseViewAdapter.MyViewHolder>
		implements CombinedExerciseItemMoveCallback.ItemTouchHelperContract {
	/**
	 * The exercise id.
	 */
	private final int mExerciseId;
	/**
	 * The list of child exercises as view data.
	 */
	private final List<SingleExerciseData> mSingleExercises;
	/**
	 * The list of exercise ids.
	 */
	private final List<Integer> mExerciseIds;
	/**
	 * The listener identifying start of drag.
	 */
	private StartDragListener mStartDragListener;
	/**
	 * A reference to the fragment.
	 */
	private final WeakReference<Fragment> mFragment;

	/**
	 * Constructor.
	 *
	 * @param fragment   the calling fragment.
	 * @param exerciseId the exercise id.
	 */
	public CombinedExerciseViewAdapter(final Fragment fragment, final int exerciseId) {
		mFragment = new WeakReference<>(fragment);
		mExerciseId = exerciseId;
		ExerciseData exerciseData = exerciseId == 0 ? null : ExerciseData.fromId(exerciseId);
		if (exerciseData instanceof CombinedExerciseData) {
			mSingleExercises = ((CombinedExerciseData) exerciseData).getSingleExerciseData();
			mExerciseIds = ((CombinedExerciseData) exerciseData).getSingleExerciseIds();
		}
		else {
			mSingleExercises = new ArrayList<>();
			mExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_single_exercise_ids);
			for (int singleExerciseId : mExerciseIds) {
				mSingleExercises.add(StoredExercisesRegistry.getInstance().getSingleExercise(singleExerciseId));
			}
		}
	}

	/**
	 * Set the listener identifying start of drag.
	 *
	 * @param startDragListener The listener.
	 */
	public void setStartDragListener(final StartDragListener startDragListener) {
		mStartDragListener = startDragListener;
	}

	@Override
	public final MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_combined_exercise_step, parent, false);
		return new MyViewHolder(itemView);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public final void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
		final ExerciseData exerciseData = mSingleExercises.get(position);
		final Fragment fragment = mFragment.get();
		if (fragment == null) {
			return;
		}

		String name = exerciseData.getName();
		holder.mTitle.setText(name == null || name.isEmpty() ? fragment.getString(R.string.text_unnamed_step) : name);

		holder.mDragHandle.setOnTouchListener((view, event) -> {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mStartDragListener.requestDrag(holder);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				view.performClick();
			}
			return false;
		});

		holder.mDeleteButton.setVisibility(View.VISIBLE);
		holder.mDeleteButton.setOnClickListener(v -> {
			if (fragment.getActivity() != null) {
				DialogUtil.displayConfirmationMessage(fragment.getActivity(), dialog -> {
					StoredExercisesRegistry.getInstance().removeExerciseOfId(mSingleExercises.get(position).getId(), true, mExerciseId);
					mSingleExercises.remove(position);
					mExerciseIds.remove(position);
					notifyItemRemoved(position);
					notifyItemRangeChanged(position, mSingleExercises.size() - position);
				}, null, R.string.button_cancel, R.string.button_delete, R.string.message_confirm_delete_child_exercise);
			}
		});

		holder.mEditExercise.setOnClickListener(v -> {
			Activity activity = fragment.getActivity();
			if (activity instanceof MainActivity) {
				// need to stop service as the stored exercise is opened in stopped state
				if (ExerciseService.isServiceRunning(activity)) {
					ExerciseService.triggerExerciseService(activity, ServiceCommand.STOP, exerciseData);
				}
				// PlayStatus might get updated in repository, but should not be used here
				EditSingleExerciseFragment.navigate(v, true, mExerciseIds.get(position), mExerciseId);
				exerciseData.updatePlayStatus(PlayStatus.STOPPED);
				SingleExerciseViewModel singleExerciseViewModel =
						new ViewModelProvider((MainActivity) activity).get(EditSingleExerciseViewModel.class);
				singleExerciseViewModel.updateFromExerciseData(exerciseData);
			}
		});
	}

	@Override
	public final int getItemCount() {
		return mSingleExercises.size();
	}

	@Override
	public final void onRowMoved(final int fromPosition, final int toPosition) {
		if (fromPosition < toPosition) {
			for (int i = fromPosition; i < toPosition; i++) {
				Collections.swap(mSingleExercises, i, i + 1);
				Collections.swap(mExerciseIds, i, i + 1);
			}
		}
		else {
			for (int i = fromPosition; i > toPosition; i--) {
				Collections.swap(mSingleExercises, i, i - 1);
				Collections.swap(mExerciseIds, i, i - 1);
			}
		}
		if (mExerciseId > 0) {
			PreferenceUtil.setIndexedSharedPreferenceIntList(R.string.key_stored_single_exercise_ids, mExerciseId, mExerciseIds);
		}
		else {
			PreferenceUtil.setSharedPreferenceIntList(R.string.key_single_exercise_ids, mExerciseIds);
		}
		notifyItemMoved(fromPosition, toPosition);
	}

	@Override
	public final void onRowSelected(final MyViewHolder myViewHolder) {
		myViewHolder.mRowView.setBackgroundColor(android.graphics.Color.LTGRAY);

	}

	@Override
	public final void onRowClear(final MyViewHolder myViewHolder) {
		myViewHolder.mRowView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
	}

	/**
	 * The view holder of the items.
	 */
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		/**
		 * The whole item.
		 */
		private final View mRowView;
		/**
		 * The button to edit the exercise.
		 */
		private final ImageView mEditExercise;
		/**
		 * The title.
		 */
		private final TextView mTitle;
		/**
		 * The image view.
		 */
		private final ImageView mDragHandle;
		/**
		 * The delete button.
		 */
		private final ImageView mDeleteButton;

		/**
		 * Constructor.
		 *
		 * @param itemView The item view.
		 */
		public MyViewHolder(final View itemView) {
			super(itemView);
			mRowView = itemView;
			mTitle = itemView.findViewById(R.id.textViewColorName);
			mDragHandle = itemView.findViewById(R.id.imageViewDragHandle);
			mDeleteButton = itemView.findViewById(R.id.imageViewDelete);
			mEditExercise = itemView.findViewById(R.id.imageViewEdit);
		}
	}

	/**
	 * A listener for starting the drag.
	 */
	public interface StartDragListener {
		/**
		 * Method for starting the drag.
		 *
		 * @param viewHolder The view Holder.
		 */
		void requestDrag(RecyclerView.ViewHolder viewHolder);
	}
}
