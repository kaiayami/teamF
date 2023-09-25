package com.example.exgps2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean requestingLocationUpdates = false;

    private TextView textView1, textView2;

    private final ActivityResultLauncher<String>
            requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    requestingLocationUpdates = true;
                } else {
                    Toast toast = Toast.makeText(this,
                            "これ以上なにもできません", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);

        } else {
            requestingLocationUpdates = true;
        }

        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);

        locationRequest = LocationRequest.create();



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // 緯度の表示
                    String str1 = " Latitude:" + location.getLatitude();
                    textView1.setText(str1);

                    // 経度の表示
                    String str2 = " Longitude:" + location.getLongitude();
                    textView2.setText(str2);
                }
            }
        };

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
