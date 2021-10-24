package de.jeisfeld.breathtraining;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.jeisfeld.breathtraining.databinding.ActivityMainBinding;
import de.jeisfeld.breathtraining.exercise.ExerciseData;
import de.jeisfeld.breathtraining.exercise.ExerciseService.ServiceQueryReceiver;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.PlayStatus;
import de.jeisfeld.breathtraining.sound.SoundPlayer;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.ui.training.ServiceReceiver;
import de.jeisfeld.breathtraining.ui.training.TrainingViewModel;

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
		DrawerLayout drawer = mBinding.drawerLayout;
		NavigationView navigationView = mBinding.navView;
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_training, R.id.nav_measure)
						.setOpenableLayout(drawer)
						.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);

		TrainingViewModel trainingViewModel = new ViewModelProvider(this).get(TrainingViewModel.class);

		mServiceReceiver = new ServiceReceiver(new Handler(), trainingViewModel);
		registerReceiver(mServiceReceiver, new IntentFilter(ServiceReceiver.RECEIVER_ACTION));

		ExerciseData exerciseData = ExerciseData.fromIntent(getIntent());
		if (exerciseData != null) {
			ExerciseStep exerciseStep = (ExerciseStep) getIntent().getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_STEP);
			trainingViewModel.updateFromExerciseData(exerciseData, exerciseStep);
			navController.navigate(R.id.nav_training);
		}

		sendBroadcast(new Intent(ServiceQueryReceiver.RECEIVER_ACTION));
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		TrainingViewModel trainingViewModel = new ViewModelProvider(this).get(TrainingViewModel.class);

		MenuItem menuItemPlay = menu.findItem(R.id.action_play);
		MenuItem menuItemPause = menu.findItem(R.id.action_pause);

		menuItemPlay.setOnMenuItemClickListener(item -> {
			trainingViewModel.play(MainActivity.this);
			return true;
		});

		menuItemPause.setOnMenuItemClickListener(item -> {
			trainingViewModel.pause(MainActivity.this);
			return true;
		});

		trainingViewModel.getPlayStatus().observe(MainActivity.this, playStatus -> {
			menuItemPause.setVisible(playStatus == PlayStatus.PLAYING);
			menuItemPlay.setVisible(playStatus != PlayStatus.PLAYING);
		});

		return true;
	}

	@Override
	public final boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mServiceReceiver);
		SoundPlayer.releaseInstance(MediaTrigger.ACTIVITY);
	}

}
