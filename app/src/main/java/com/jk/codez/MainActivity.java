package com.jk.codez;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jk.codez.databinding.ActivityMainBinding;
import com.mapbox.mapboxsdk.Mapbox;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private CodezViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.load(this);
        mViewModel = new ViewModelProvider(this).get(CodezViewModel.class);
        Mapbox.getInstance(this, getResources().getString(R.string.access_token));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViewById(R.id.btn_set_connection).setOnClickListener(v -> {
            hideConnectionInput();
            mViewModel.setConnection(((EditText)findViewById(R.id.et_connection_input)).getText().toString());
        });

        setSupportActionBar(binding.toolbar);

        NavHostFragment nhf = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = Objects.requireNonNull(nhf).getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        doIHavePermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        String dest = Objects.requireNonNull(navController.getCurrentDestination()).getDisplayName();
        if (dest.contains("Local")) {
            menu.findItem(R.id.action_remote).setVisible(true);
            menu.findItem(R.id.action_local).setVisible(false);
            menu.findItem(R.id.action_connection).setVisible(false);
        }
        if (dest.contains("Remote")) {
            menu.findItem(R.id.action_remote).setVisible(false);
            menu.findItem(R.id.action_local).setVisible(true);
            menu.findItem(R.id.action_connection).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remote) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.RemoteFragment);
            return true;
        }

        if (id == R.id.action_local) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.LocalFragment);
            return true;
        }

        if (id == R.id.action_connection) {
            showConnectionInput();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConnectionInput() {
        findViewById(R.id.nav_host_fragment_content_main).setVisibility(View.GONE);
        findViewById(R.id.fl_connection_input).setVisibility(View.VISIBLE);
    }

    private void hideConnectionInput() {
        findViewById(R.id.nav_host_fragment_content_main).setVisibility(View.VISIBLE);
        findViewById(R.id.fl_connection_input).setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mViewModel.setCurrentLocation(location);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void updateTitle(String title) {
        setTitle(title);
    }

    private void doIHavePermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mViewModel.setPermissionStatus(true);
        } else if (shouldShowRequestPermissionRationale("ACCESS_FINE_LOCATION")) {
            Toast.makeText(this, "This app needs access to your location.", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
        mViewModel.setPermissionStatus(false);
    }

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    System.out.println("is granted");
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    System.out.println("is not granted");
                }
            });
}