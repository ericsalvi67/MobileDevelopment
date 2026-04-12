package com.salvi.atividadecinco;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private TextView tvLatIni, tvLonIni, tvAltIni;
    private TextView tvLatFin, tvLonFin, tvAltFin;
    private TextView tvAcelIni, tvMagIni;
    private TextView tvAcelFin, tvMagFin;
    private TextView tvDistance, tvAngle;

    private Button btnInitialPos, btnCalculate;

    private Location currentLocation = new Location("current");
    private Location initialLocation = new Location("initial");
    private Location finalLocation = new Location("final");

    private AxisDTO accelValues = new AxisDTO();
    private AxisDTO magValues = new AxisDTO();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    currentLocation = location;
                }
                else if (currentLocation == null) {
                    currentLocation = location;
                }
            }
        };

        initViews();

        btnInitialPos.setOnClickListener(v -> {
            saveCurrentLocation(initialLocation);

            tvLatIni.setText("Latitude: " + initialLocation.getLatitude());
            tvLonIni.setText("Longitude: " + initialLocation.getLongitude());
            tvAltIni.setText("Altitude: " + initialLocation.getAltitude());
            tvAcelIni.setText(
                    "Acelerômetro: X:" + accelValues.x +
                            " Y:" + accelValues.y +
                            " Z:" + accelValues.z
            );
            tvMagIni.setText(
                    "Bússola: X:" + magValues.x +
                            " Y:" + magValues.y +
                            " Z:" + magValues.z
            );
        });

        btnCalculate.setOnClickListener(v -> {
            saveCurrentLocation(finalLocation);

            tvLatFin.setText("Latitude: " + finalLocation.getLatitude());
            tvLonFin.setText("Longitude: " + finalLocation.getLongitude());
            tvAltFin.setText("Altitude: " + finalLocation.getAltitude());
            tvAcelFin.setText(
                    "Acelerômetro: X:" + accelValues.x +
                            " Y:" + accelValues.y +
                            " Z:" + accelValues.z
            );
            tvMagFin.setText(
                    "Bússola: X:" + magValues.x +
                            " Y:" + magValues.y +
                            " Z:" + magValues.z
            );

            double distance = Math.sqrt(
                        (finalLocation.getLatitude() - initialLocation.getLatitude()) +
                        (finalLocation.getLatitude() - initialLocation.getLatitude()) +
                        (finalLocation.getAltitude() - initialLocation.getAltitude())
            );
            if (distance < 5) distance = 0; //trata inconsistencia do GPS

            tvDistance.setText(String.format("Distância: %.2f metros", distance));

            float angle = getAngle();
            tvAngle.setText("Ângulo: " + angle);
        });
    }

    private void initViews() {
        tvLatIni = findViewById(R.id.tvLatitudIni);
        tvLonIni = findViewById(R.id.tvLongitudIni);
        tvAltIni = findViewById(R.id.tvAltitudIni);
        tvAcelIni = findViewById(R.id.tvAcelIni);
        tvMagIni = findViewById(R.id.tvMagIni);

        tvLatFin = findViewById(R.id.tvLatitudFin);
        tvLonFin = findViewById(R.id.tvLongitudFin);
        tvAltFin = findViewById(R.id.tvAltitudFin);
        tvAcelFin = findViewById(R.id.tvAcelFin);
        tvMagFin = findViewById(R.id.tvMagFin);

        tvDistance = findViewById(R.id.tvDistance);
        tvAngle = findViewById(R.id.tvAngle);

        btnInitialPos = findViewById(R.id.btInicialPos);
        btnCalculate = findViewById(R.id.btCalculate);
    }

    private void saveCurrentLocation(Location target) {

        if (currentLocation == null || currentLocation.getLatitude() == 0) {
            tvDistance.setText("Distância: Aguardando GPS...");
            return;
        }

        target.setLatitude(currentLocation.getLatitude());
        target.setLongitude(currentLocation.getLongitude());
        target.setAltitude(currentLocation.getAltitude());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accel != null)
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);

        if (mag != null)
            sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_UI);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0,
                    locationListener
            );
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0,
                    locationListener
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelValues.x = event.values[0];
            accelValues.y = event.values[1];
            accelValues.z = event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues.x = event.values[0];
            magValues.y = event.values[1];
            magValues.z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float getAngle() {

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                new float[] {accelValues.x, accelValues.y, accelValues.z},
                new float[] {magValues.x, magValues.y, magValues.z}
        );

        if (!success) return 0;

        SensorManager.getOrientation(rotationMatrix, orientation);

        float azimuth = (float) Math.toDegrees(orientation[0]);

        return azimuth < 0 ? azimuth + 360 : azimuth;
    }
}