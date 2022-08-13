package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private double lat = 0;
    private double longg = 0;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);


        bottomNavigationView.setOnItemSelectedListener(item -> {
//            if(!IntroductoryActivity.cityName.equalsIgnoreCase("Unavailable")) {
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        changeFragment(new HomeFragment());
                        break;
                    case R.id.cart_menu:
                        changeFragment(new CartFragment());
                        break;
                    case R.id.bookings_menu:
                        changeFragment(new BookingsFragment());
                        break;
                    case R.id.others_menu:
                        changeFragment(new OthersFragment());
                        break;
                }
//            }else{
//                Toast.makeText(this, "Please wait until location get updated", Toast.LENGTH_SHORT).show();
//            }
            return true;
        });

    }
    private void changeFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view,fragment)
                .commit();
    }
    // on start


//    @SuppressLint("UnsafeOptInUsageError")
//    @Override
//    public void onStart() {
//        super.onStart();
//
////        BadgeDrawable badgeDrawable = BadgeDrawable.create(this);
////        badgeDrawable.setNumber(IntroductoryActivity.cartItems.size());
////        View badge = LayoutInflater.from(this)
////                .inflate(R.layout.activity_main, bottomNavigationView, true);
////        BadgeUtils.attachBadgeDrawable(badgeDrawable,badge);
//    }

}
