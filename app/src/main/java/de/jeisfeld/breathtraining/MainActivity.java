package de.jeisfeld.breathtraining;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.jeisfeld.breathtraining.databinding.ActivityMainBinding;
import de.jeisfeld.breathtraining.exercise.combined.CombinedExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService.ServiceQueryReceiver;
import de.jeisfeld.breathtraining.exercise.service.ServiceReceiver;
import de.jeisfeld.breathtraining.exercise.single.SingleExerciseViewModel;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.sound.SoundPlayer;
import de.jeisfeld.breathtraining.util.PreferenceUtil;

/**
 * Main activity of the app.
 */
public class MainActivity extends AppCompatActivity {
	/**
	 * The navigation bar configuration.
	 */
	private AppBarConfiguration mAppBarConfiguration;
	/**
	 * The service receiver.
	 */
	private ServiceReceiver mServiceReceiver;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());
		setSupportActionBar(mBinding.appBarMain.toolbar);
		AppCompatDelegate.setDefaultNightMode(
				PreferenceUtil.getSharedPreferenceIntString(R.string.key_pref_night_mode, R.string.pref_default_night_mode));
		DrawerLayout drawer = mBinding.drawerLayout;
		NavigationView navigationView = mBinding.navView;

		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_single_exercise, R.id.nav_combined_exercise)
				.setOpenableLayout(drawer).build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);

		SingleExerciseViewModel singleExerciseViewModel = new ViewModelProvider(this).get(SingleExerciseViewModel.class);
		CombinedExerciseViewModel combinedExerciseViewModel = new ViewModelProvider(this).get(CombinedExerciseViewModel.class);

		mServiceReceiver = new ServiceReceiver(new Handler(), singleExerciseViewModel, combinedExerciseViewModel);
		registerReceiver(mServiceReceiver, new IntentFilter(ServiceReceiver.RECEIVER_ACTION));

		ExerciseData exerciseData = (ExerciseData) getIntent().getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_DATA);
		if (exerciseData != null) {
			ExerciseStep exerciseStep = (ExerciseStep) getIntent().getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_STEP);
			singleExerciseViewModel.updateFromExerciseData(exerciseData);
			singleExerciseViewModel.updateExerciseStep(exerciseStep);
			combinedExerciseViewModel.updateFromExerciseData(exerciseData, false);
			combinedExerciseViewModel.updateExerciseStep(exerciseStep);
		}

		singleExerciseViewModel.deleteNameIfNotMatching();
		combinedExerciseViewModel.deleteNameIfNotMatching();

		sendBroadcast(new Intent(ServiceQueryReceiver.RECEIVER_ACTION));
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

		final MenuItem itemSingleExercise = menu.findItem(R.id.action_single_exercise);
		final MenuItem itemCombinedExercise = menu.findItem(R.id.action_combined_exercise);
		final MenuItem itemStoredExercises = menu.findItem(R.id.action_stored_exercises);

		itemSingleExercise.setOnMenuItemClickListener(item -> {
			navController.navigate(R.id.nav_single_exercise);
			return true;
		});

		itemCombinedExercise.setOnMenuItemClickListener(item -> {
			navController.navigate(R.id.nav_combined_exercise);
			return true;
		});

		itemStoredExercises.setOnMenuItemClickListener(item -> {
			navController.navigate(R.id.nav_stored_exercises);
			return true;
		});

		navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
			int destinationId = destination.getId();
			if (destinationId == R.id.nav_single_exercise) {
				itemSingleExercise.setVisible(false);
				itemCombinedExercise.setVisible(true);
				itemStoredExercises.setVisible(true);
			}
			else if (destinationId == R.id.nav_combined_exercise) {
				itemSingleExercise.setVisible(true);
				itemCombinedExercise.setVisible(false);
				itemStoredExercises.setVisible(true);
			}
			else {
				itemSingleExercise.setVisible(false);
				itemCombinedExercise.setVisible(false);
				itemStoredExercises.setVisible(false);
			}
		});

		return true;
	}

	@Override
	public final boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mServiceReceiver);
		SoundPlayer.releaseInstance(MediaTrigger.ACTIVITY);
	}

	@Override
	protected final void attachBaseContext(final Context newBase) {
		super.attachBaseContext(Application.createContextWrapperForLocale(newBase));
	}
}
