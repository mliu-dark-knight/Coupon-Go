package edu.illinois.cs465.walkingrewardapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Date;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;
import edu.illinois.cs465.walkingrewardapp.Maps.TouchableWrapper;

public class WalkingActivity extends AppCompatActivity implements
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener,
        SensorEventListener,
        TouchableWrapper.UpdateMapAfterUserInterection
{
    public CheckBox dontShowAgain;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    LocationManager locationManager;
    String locationProvider;
    Challenge goal;

    int progress = 0;
    ProgressBar simpleProgressBar;

    int value = 0;
    int maxSteps;

    protected void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

        try {
            goal = Library.getCurrentGoal();
            TextView current_goal = (TextView) findViewById(R.id.goal);
            TextView description = (TextView) findViewById(R.id.description);
            if(goal != null) {
                current_goal.setText("Current Goal: " + goal.getRestaurant());
                description.setText(goal.getDescription());
                maxSteps = goal.getStepsRequired();
                TextView progress = (TextView) findViewById(R.id.progress);
                progress.setText(Integer.toString(Library.getCurrentSteps()) + "/" + Integer.toString(maxSteps) + " steps");
            }
            else
            {
                current_goal.setText("Current Goal: None" );
                description.setText("");
            }
        }
        catch (Exception e) {
        }

        final TextView textic = (TextView) findViewById(R.id.timeRemaining);

        if(goal != null) {
            CountDownTimer Count = new CountDownTimer(goal.timeRemainingMS(), 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
                    int minutes = (int) (((millisUntilFinished / 1000) % 3600) / 60);
                    int hours = (int) ((millisUntilFinished / 1000) / 3600);
                    DecimalFormat df = new DecimalFormat("#00");
                    textic.setText(df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds));
                }

                public void onFinish() {
                    textic.setText("Challenge Failed");
                }
            };
            Count.start();
        }

        boolean show_dialog = true;
        try {
            show_dialog = getIntent().getExtras().getBoolean("show_dialog");
        }
        catch (Exception e) {
        }

        if(goal == null && Library.ShowTutorial() && show_dialog)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater adbInflater = LayoutInflater.from(this);
            View layout = adbInflater.inflate(R.layout.checkbox, null);

            dontShowAgain = (CheckBox) layout.findViewById(R.id.skip);
            builder.setView(layout);
            builder.setMessage("You have no goal selected. You can choose a goal by selecting \"Change Goal\" in the top right menu.").setTitle("Tutorial");
            builder.setPositiveButton("Take me there", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dontShowAgain.isChecked()) {
                        Library.removeTutorial();
                    }
                    openActivity(ChooseGoalActivity.class);
                }
            });
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dontShowAgain.isChecked()) {
                        Library.removeTutorial();
                    }
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mSensorManager.unregisterListener(this, mStepCounterSensor);
        //mSensorManager.unregisterListener(this, mStepDetectorSensor);
        //TODO: Save current challenge and current steps
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        Library.initializeData(getApplicationContext());

        goal = Library.getCurrentGoal();

        if(goal != null) {
            findViewById(R.id.menu).setVisibility(View.VISIBLE);
            TextView current_goal = (TextView) findViewById(R.id.goal);
            current_goal.setText("Current Goal: " + goal.getRestaurant());
            TextView description = (TextView) findViewById(R.id.description);
            description.setText(goal.getDescription());
            maxSteps = goal.getStepsRequired();
            TextView progress = (TextView) findViewById(R.id.progress);
            progress.setText(Integer.toString(Library.getCurrentSteps()) + "/" + Integer.toString(maxSteps) + " steps");
            this.setTitle("Start Walking!");
        }

        SetupSensor();

        // initiate progress
        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        setProgressValue(0);

        //add the on-click event to the goal information
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(Library.getCurrentGoal() != null) {
                    //show the current goal 
                    Intent intent = new Intent(WalkingActivity.this, ViewGoalActivity.class);
                    intent.putExtra("goal", Library.getCurrentGoal());
                    startActivity(intent);
                }
            }
        });
    }

    private void setProgressValue(final int progress) {
        // set the progress
        simpleProgressBar.setProgress(progress);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        Location location = null;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location  = locationManager.getLastKnownLocation(locationProvider);
        }

        //initialize the location
        if(location != null) {
            onLocationChanged(location);
        }
    }


    boolean isFollowing = true;
    @Override
    public void onLocationChanged(Location location) {
        if(isFollowing) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            this.mMap.moveCamera(center);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            this.mMap.animateCamera(zoom);
        }
    }

    @Override
    public void onCameraMove(){

    }

    @Override
    public boolean onMyLocationButtonClick() {
        isFollowing = true;
        return false;
    }

    public void onUpdateMapAfterUserInterection() {
        isFollowing = false;
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            //get the location manager
            this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            //define the location manager criteria
            Criteria criteria = new Criteria();
            this.locationProvider = locationManager.getBestProvider(criteria, false);

            mMap.setMyLocationEnabled(true);
            this.locationManager.requestLocationUpdates(this.locationProvider, 400, 1, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            mPermissionDenied = false;
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }

    //code from http://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        Library.initializeData(getApplicationContext());

        return true;
    }

    //code from http://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_change_goal:
                openActivity(ChooseGoalActivity.class);
                break;
            case R.id.action_my_rewards:
                //Toast.makeText(getApplicationContext(), "Thanks for clicking the Rewards button!", Toast.LENGTH_SHORT).show();
                openActivity(RewardsActivity.class);
                break;
            case R.id.action_view_statistics:
                openActivity(StatisticsActivity.class);
                break;
            default:
                break;
        }
        return true;
    }

    private SensorManager mSensorManager;

    private Sensor mStepCounterSensor;

    private Sensor mStepDetectorSensor;

    protected void SetupSensor()
    {
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public void onSensorChanged (SensorEvent e)
    {
        Sensor sensor = e.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float[] values = e.values;
            goal = Library.getCurrentGoal();
            if (values.length > 0 && goal != null) {
                value = (int) values[0];
                Library.setTotalSteps(value);
                Library.setCurrentSteps(Library.getCurrentSteps()+1);

                if( Library.getCurrentSteps() >= goal.getStepsRequired())
                {
                    //reset progress
                    Library.setCurrentSteps(0);
                    goal.setTimeCompleted(new Date());
                    Library.addReward(goal);
                    Library.setnRewardsEarned(Library.getnRewardsEarned()+1);

                    //remove goal
                    TextView current_goal = (TextView) findViewById(R.id.goal);
                    current_goal.setText("Current Goal: None");
                    TextView description = (TextView) findViewById(R.id.description);
                    description.setText("");
                    Library.setCurrentGoal(null);
                    goal = null;
                }
            }
            if(goal == null){
                TextView current_goal = (TextView) findViewById(R.id.goal);
                current_goal.setText("Current Goal: None");
                TextView description = (TextView) findViewById(R.id.description);
                description.setText("");
            }

            int steps = Library.getCurrentSteps();
            TextView progress = (TextView) findViewById(R.id.progress);
            progress.setText(Integer.toString(steps) + "/" + Integer.toString(maxSteps) + " steps");

            double progress_bar = (1.0 * steps / maxSteps) * 100.0;
            simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
            setProgressValue((int)progress_bar);
        }
    }

    public void onAccuracyChanged(Sensor s, int i)
    {

    }
}
