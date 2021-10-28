package de.jeisfeld.breathtraining.repository;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.jeisfeld.breathtraining.MainActivity;
import de.jeisfeld.breathtraining.R;
import de.jeisfeld.breathtraining.exercise.EditExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.ExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService.ServiceCommand;
import de.jeisfeld.breathtraining.util.DialogUtil;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Adapter for the RecyclerView that allows to sort stored exercises.
 */
public class StoredExercisesViewAdapter extends RecyclerView.Adapter<StoredExercisesViewAdapter.MyViewHolder>
		implements de.jeisfeld.breathtraining.repository.StoredExercisesItemMoveCallback.ItemTouchHelperContract {
	/**
	 * The list of stored exercises as view data.
	 */
	private final List<ExerciseData> mStoredExercises;
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
	 * @param fragment the calling fragment.
	 */
	public StoredExercisesViewAdapter(final Fragment fragment) {
		mStoredExercises = StoredExercisesRegistry.getInstance().getStoredExercises();
		mExerciseIds = PreferenceUtil.getSharedPreferenceIntList(R.string.key_stored_exercise_ids);
		mFragment = new WeakReference<>(fragment);
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
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_stored_exercise, parent, false);
		return new MyViewHolder(itemView);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public final void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
		final ExerciseData exerciseData = mStoredExercises.get(position);
		holder.mTitle.setText(exerciseData.getName());

		holder.mTitle.setOnClickListener(v -> {
			FragmentActivity activity = mFragment.get() == null ? null : mFragment.get().getActivity();
			if (activity != null) {
				DialogUtil.displayInputDialog(activity, (dialog, text) -> {
							if (text == null || text.trim().isEmpty()) {
								DialogUtil.displayConfirmationMessage(activity,
										R.string.title_did_not_save_empty_name, R.string.message_did_not_save_empty_name);
							}
							else {
								StoredExercisesRegistry.getInstance().rename(exerciseData, text);
								holder.mTitle.setText(text.trim());
							}
						}, R.string.title_dialog_change_exercise_name, R.string.button_rename,
						holder.mTitle.getText().toString(), InputType.TYPE_CLASS_TEXT, R.string.message_dialog_new_exercise_name);
			}
		});

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
			Fragment fragment = mFragment.get();
			if (fragment != null && fragment.getActivity() != null) {
				DialogUtil.displayConfirmationMessage(fragment.getActivity(), dialog -> {
					StoredExercisesRegistry.getInstance().remove(exerciseData);
					mStoredExercises.remove(position);
					mExerciseIds.remove(position);
					notifyItemRemoved(position);
					notifyItemRangeChanged(position, mStoredExercises.size() - position);
				}, null, R.string.button_cancel, R.string.button_delete, R.string.message_confirm_delete_exercise, exerciseData.getName());
			}
		});

		holder.mEditExercise.setOnClickListener(v -> {
			Fragment fragment = mFragment.get();
			if (fragment != null) {
				Activity activity = fragment.getActivity();
				if (activity instanceof MainActivity) {
					// need to stop service as the stored exercise is opened in stopped state
					if (ExerciseService.isServiceRunning(activity)) {
						ExerciseService.triggerExerciseService(activity, ServiceCommand.STOP, exerciseData);
					}
					// PlayStatus might get updated in repository, but should not be used here
					NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
					navController.navigate(R.id.nav_edit_exercise);
					exerciseData.updatePlayStatus(PlayStatus.STOPPED);
					ExerciseViewModel exerciseViewModel = new ViewModelProvider((MainActivity) activity).get(EditExerciseViewModel.class);
					exerciseViewModel.updateFromExerciseData(exerciseData, null);
				}
			}
		});

		holder.mPlayExercise.setOnClickListener(v -> {
			Fragment fragment = mFragment.get();
			if (fragment != null) {
				Activity activity = fragment.getActivity();
				if (activity instanceof MainActivity) {
					NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
					// delete and re-create fragment, in order to prevent recovery of old screen status
					navController.popBackStack(R.id.nav_exercise, true);
					navController.navigate(R.id.nav_exercise);

					ExerciseViewModel exerciseViewModel = new ViewModelProvider((MainActivity) activity).get(ExerciseViewModel.class);
					exerciseData.updatePlayStatus(PlayStatus.PLAYING);
					exerciseViewModel.updateFromExerciseData(exerciseData, null);

					ExerciseService.triggerExerciseService(v.getContext(), ServiceCommand.START, exerciseData);
				}
			}
		});
	}

	@Override
	public final int getItemCount() {
		return mStoredExercises.size();
	}

	@Override
	public final void onRowMoved(final int fromPosition, final int toPosition) {
		if (fromPosition < toPosition) {
			for (int i = fromPosition; i < toPosition; i++) {
				Collections.swap(mStoredExercises, i, i + 1);
				Collections.swap(mExerciseIds, i, i + 1);
			}
		}
		else {
			for (int i = fromPosition; i > toPosition; i--) {
				Collections.swap(mStoredExercises, i, i - 1);
				Collections.swap(mExerciseIds, i, i - 1);
			}
		}
		PreferenceUtil.setSharedPreferenceIntList(R.string.key_stored_exercise_ids, mExerciseIds);
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
	public class MyViewHolder extends RecyclerView.ViewHolder {
		/**
		 * The whole item.
		 */
		private final View mRowView;
		/**
		 * The button to play the exercise.
		 */
		private final ImageButton mPlayExercise;
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
			mPlayExercise = itemView.findViewById(R.id.imageViewPlayExercise);
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
