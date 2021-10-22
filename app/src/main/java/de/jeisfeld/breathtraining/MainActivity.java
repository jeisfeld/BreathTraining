package de.jeisfeld.breathtraining;

import com.google.android.material.navigation.NavigationView;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.jeisfeld.breathtraining.databinding.ActivityMainBinding;
import de.jeisfeld.breathtraining.exercise.ExerciseData;
import de.jeisfeld.breathtraining.exercise.ExerciseStep;
import de.jeisfeld.breathtraining.sound.MediaPlayer;
import de.jeisfeld.breathtraining.sound.MediaTrigger;
import de.jeisfeld.breathtraining.ui.home.HomeViewModel;
import de.jeisfeld.breathtraining.ui.home.ServiceReceiver;

/**
 * Main activity of the app.
 */
public class MainActivity extends AppCompatActivity {
	/**
	 * The navigation bar configuration.
	 */
	private AppBarConfiguration mAppBarConfiguration;
	/**
	 * The activity binding.
	 */
	private ActivityMainBinding mBinding;
	/**
	 * The service receiver.
	 */
	private ServiceReceiver mServiceReceiver;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBinding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		setSupportActionBar(mBinding.appBarMain.toolbar);
		DrawerLayout drawer = mBinding.drawerLayout;
		NavigationView navigationView = mBinding.navView;
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_home, R.id.nav_measure)
						.setOpenableLayout(drawer)
						.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);

		HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

		mServiceReceiver = new ServiceReceiver(new Handler(), homeViewModel);
		registerReceiver(mServiceReceiver, new IntentFilter(ServiceReceiver.RECEIVER_ACTION));

		ExerciseData exerciseData = ExerciseData.fromIntent(getIntent());
		if (exerciseData != null) {
			ExerciseStep exerciseStep = (ExerciseStep) getIntent().getSerializableExtra(ServiceReceiver.EXTRA_EXERCISE_STEP);
			homeViewModel.updateFromExerciseData(exerciseData, exerciseStep);
			navController.navigate(R.id.nav_home);
		}
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		MediaPlayer.releaseInstance(MediaTrigger.ACTIVITY);
	}

}
