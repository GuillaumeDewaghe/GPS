package fr.insa.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class TrackerActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor,tv_updates,tv_address;

    Switch sw_locationupdates, sw_gps;

    private ImageView imageView;


    //variable qui sert a verifier si on active le tracker ou non
    boolean update= false;

    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    //GOOGLE API LOCATION SERVICE
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude= findViewById(R.id.tv_altitude);
        tv_accuracy= findViewById(R.id.tv_accuracy);
        tv_speed= findViewById(R.id.tv_speed);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_updates=findViewById(R.id.tv_updates);
        tv_address=findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);

        imageView = findViewById(R.id.imageView2);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000* DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000* FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updatedUIV(locationResult.getLastLocation());
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()){
                    //precisiongps accrue
                    locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("GPS utilisé");
                }
                else{
                    locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Tour+wifi");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw_locationupdates.isChecked()){
                    startLocationUpdates();
                }
                else {
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();
    }
    //Update de la localisation avec le bouton associé
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack,null);

        imageView.setVisibility(View.VISIBLE);

        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

        imageView.setVisibility(View.INVISIBLE);
    }

    //gere les permissions afin de pouvoir acceder au gps
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app require permission to be granted in order to work properly ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    //update du gps
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TrackerActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updatedUIV(location);
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }


    //update de l affichage
    private void updatedUIV(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not available");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not available");
        }

        if(location.getLatitude() >= 50.4055133 && location.getLatitude() <= 50.4055317)
        {
            imageView.setImageResource(R.drawable.marchiennes_31);
        }
        else if (location.getLatitude() >= 50.4055222 && location.getLatitude() <= 50.4056000)
        {
            imageView.setImageResource(R.drawable.marchiennes_29);
        }
        else if (location.getLatitude() >= 50.4057426 && location.getLatitude() <= 50.4059000)
        {
            imageView.setImageResource(R.drawable.marchiennes_27);
        }
        else
        {
            imageView.setImageResource(R.drawable.monokuma_triste);
        }

        Geocoder geocoder = new Geocoder(TrackerActivity.this);
        try{

            List<Address> addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            tv_address.setText("Unable to get street address");
        }
    }
}