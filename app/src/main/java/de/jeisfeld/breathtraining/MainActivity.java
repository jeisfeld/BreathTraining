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
import de.jeisfeld.breathtraining.exercise.ExerciseViewModel;
import de.jeisfeld.breathtraining.exercise.data.ExerciseData;
import de.jeisfeld.breathtraining.exercise.data.ExerciseStep;
import de.jeisfeld.breathtraining.exercise.data.PlayStatus;
import de.jeisfeld.breathtraining.exercise.service.ExerciseService.ServiceQueryReceiver;
import de.jeisfeld.breathtraining.exercise.service.ServiceReceiver;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.sound.SoundPlayer;

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
		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_exercise).setOpenableLayout(drawer).build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);

		ExerciseViewModel exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);

		mServiceReceiver = new ServiceReceiver(new Handler(), exerciseViewModel);
		registerReceiver(mServiceReceiver, new IntentFilter(ServiceReceiver.RECEIVER_ACTION));

		ExerciseData exerciseData = ExerciseData.fromIntent(getIntent());
		if (exerciseData != null) {
			ExerciseStep exerciseStep = (ExerciseStep) getIntent().getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_STEP);
			exerciseViewModel.updateFromExerciseData(exerciseData, exerciseStep);
		}

		exerciseViewModel.deleteNameIfNotMatching();

		sendBroadcast(new Intent(ServiceQueryReceiver.RECEIVER_ACTION));
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		ExerciseViewModel exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);

		MenuItem menuItemPlay = menu.findItem(R.id.action_play);
		MenuItem menuItemPause = menu.findItem(R.id.action_pause);

		menuItemPlay.setOnMenuItemClickListener(item -> {
			exerciseViewModel.play(MainActivity.this);
			return true;
		});

		menuItemPause.setOnMenuItemClickListener(item -> {
			exerciseViewModel.pause(MainActivity.this);
			return true;
		});

		exerciseViewModel.getPlayStatus().observe(MainActivity.this, playStatus -> {
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
