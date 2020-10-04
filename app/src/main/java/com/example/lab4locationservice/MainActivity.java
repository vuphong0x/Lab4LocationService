package com.example.lab4locationservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Initialize variable
    TextView tvLongitude, tvLatitude, tvCountry, tvLocality, tvAddress;
    Button btnLocation, btCheckNetwork;
    FusedLocationProviderClient fusedLocationProviderClient;
    BroadcastReceiver MyReceiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign variable
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvCountry = findViewById(R.id.tvCountry);
        tvLocality = findViewById(R.id.tvLocality);
        tvAddress = findViewById(R.id.tvAddress);
        btnLocation = findViewById(R.id.btn_location);
        btCheckNetwork = findViewById(R.id.btCheckNetwork);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get Location
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else {
                    // Nếu chưa cấp quyền
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        // Check network state
        MyReceiver = new MyReceiver();
        btCheckNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastIntent();
            }
        });
    }

    private void getLocation() {
        Log.e("getLocation", "getLocation");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.e("onComplete", "onComplete");
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        tvLatitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Latitude :</b><br></font>" + addresses.get(0).getLatitude()));
                        tvLongitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Longitude :</b><br></font>" + addresses.get(0).getLongitude()));
                        tvAddress.setText(Html.fromHtml("<font color = '#6200EE'><b>Address :</b><br></font>" + addresses.get(0).getAddressLine(0)));
                        tvCountry.setText(Html.fromHtml("<font color = '#6200EE'><b>Country :</b><br></font>" + addresses.get(0).getCountryName()));
                        tvLocality.setText(Html.fromHtml("<font color = '#6200EE'><b>Locality :</b><br></font>" + addresses.get(0).getLocality()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    // Check network
    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }

}