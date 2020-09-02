package com.example.displayinglocationonmap;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;

    LocationListener locationListener;

    Pattern p;
    Matcher m;
    String s;
    String[] loc = {"27.9881388","86.9162203"};
    LatLng locdisplay;
    String address;
    String previousaddress;
    int count;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        previousaddress = "";
        count = 0;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                if(count == 0)
                {
                    mMap.clear();
                    locdisplay = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(locdisplay));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locdisplay, 10));
                    Toast.makeText(MapsActivity.this, "1", Toast.LENGTH_SHORT).show();
                }

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                address = "";
                try
                {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(listAddresses!=null && listAddresses.size()>0){

                        if(listAddresses.get(0).getSubThoroughfare()!=null)
                            address = listAddresses.get(0).getSubThoroughfare();
                        if(listAddresses.get(0).getThoroughfare()!=null)
                            address = address + " " + listAddresses.get(0).getThoroughfare();
                        if(listAddresses.get(0).getLocality()!=null)
                            address = address + " " + listAddresses.get(0).getLocality();
                        if(listAddresses.get(0).getPostalCode()!= null)
                            address = address + " " + listAddresses.get(0).getPostalCode();
                        if(listAddresses.get(0).getCountryName()!= null)
                            address = address + " " + listAddresses.get(0).getCountryName();

                        if(count==0) {
                            previousaddress = address;
                            Toast.makeText(MapsActivity.this, address+"    1",Toast.LENGTH_SHORT).show();
                            mMap.addMarker(new MarkerOptions().position(locdisplay).title("Marker in " + address));
                            Log.i("Place info", address);
                            count++;
                            Toast.makeText(MapsActivity.this, "1", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if(!previousaddress.equals(address))
                {
                    mMap.clear();
                    locdisplay = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(locdisplay).title("Marker in " + address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locdisplay, 10));
                    Toast.makeText(MapsActivity.this, address+" "+ " 2",Toast.LENGTH_LONG).show();
                    Log.i("Place info", address);
                    previousaddress = address;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
            }

        };

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}

/*
p = Pattern.compile("Location\\[gps\\s(.*?)\\sacc");
        s = "";
        m = p.matcher(location.toString());
                if(m.find()){
                    loc = m.group(1).split(",");
                    Log.i("Location", Arrays.toString(loc)+"  " + loc[0]+ "   "+ loc[1]);
                }
                Log.i("Location", s);
       //locdisplay = new LatLng( Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
 */