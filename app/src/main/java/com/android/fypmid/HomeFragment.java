package com.android.fypmid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    View root;
    ImageSlider imageSlider;
    private PopularServiceProvidersRecycler adapter;
    private PopularServicesRecycler servicesAdapter;
    private RecyclerView recyclerView, servicesRecyclerView;
    private ArrayList<BusinessProfile> popularProfiles;
    private ArrayList<BusinessService> popularServices;
    private MaterialCardView menCard, womanCard;
    private TextInputEditText search;
    private double lat = 0;
    private double longg = 0;
    private TextView currentCity;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    private ImageView locationImg;
    private TextView locationCityLabel,walletAmountLabel;
    private FirebaseAuth auth;
    private DatabaseReference balanceDb;
    private FirebaseUser user;
    private int reservedBalance = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //realtime db
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        recyclerView = root.findViewById(R.id.recycler_popular_providers);
        servicesRecyclerView = root.findViewById(R.id.recycler_popular_services);
        //search bar
        search = root.findViewById(R.id.home_search);

        //city
        currentCity = root.findViewById(R.id.home_wallet_location);
        locationImg = root.findViewById(R.id.home_wallet_location_img);
        locationCityLabel = root.findViewById(R.id.home_wallet_location);
        //refresh location
        locationImg.setOnClickListener(view -> getLocationUpdates());
        locationCityLabel.setOnClickListener(view -> getLocationUpdates());
        //wallet
        walletAmountLabel = root.findViewById(R.id.home_wallet_rupees);
        auth = FirebaseAuth.getInstance();
        balanceDb = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser();
        getBalance(); // get current balance and show


        if (!IntroductoryActivity.isFirstTime) {
            getLocationUpdates();
            IntroductoryActivity.isFirstTime = true;
        }
        currentCity.setText(IntroductoryActivity.cityName);

        //men women card
        menCard = root.findViewById(R.id.men_card);
        womanCard = root.findViewById(R.id.woman_card);


        //search
        search.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        });


        imageSlider = root.findViewById(R.id.image_slider);
        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();

        imageList.add(new SlideModel(R.drawable.first, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.second, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.third, ScaleTypes.FIT));

        imageSlider.setImageList(imageList);


        //categories
        menCard.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(),Categories.class);
            intent.putExtra("CatValue","Men");
            startActivity(intent);
//            Toast.makeText(getContext(), "This feature is under development", Toast.LENGTH_SHORT).show();
        });
        womanCard.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(),Categories.class);
            intent.putExtra("CatValue","Women");
            startActivity(intent);
//            Toast.makeText(getContext(), "This feature is under development", Toast.LENGTH_SHORT).show();
        });


        //popular service providers
        popularProfiles = new ArrayList<>();
        db.child("BusinessProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    BusinessProfile profile = snap.getValue(BusinessProfile.class);
                    popularProfiles.add(profile);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new PopularServiceProvidersRecycler(getContext(), popularProfiles);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        //popular Services
        popularServices = new ArrayList<>();
        db.child("BusinessServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot snap2 : snap.getChildren()) {
                        BusinessService service = snap2.getValue(BusinessService.class);
                        popularServices.add(service);
                        servicesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        servicesAdapter = new PopularServicesRecycler(getContext(), popularServices,R.layout.recycler_popular_services);
        LinearLayoutManager llmServices = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        servicesRecyclerView.setLayoutManager(llmServices);
        servicesRecyclerView.setAdapter(servicesAdapter);

        //gps
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        return root;
    }


    @SuppressLint("MissingPermission")
    public void getLocationUpdates() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        } else {
            EasyPermissions.requestPermissions((Activity) getContext(), "Please Accept Permissions ", 1, perms);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                lat = location.getLatitude();
                longg = location.getLongitude();
                Geocoder geoCoder;
                try {
                    geoCoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> address = null;
                    address = geoCoder.getFromLocation(lat, longg, 1);
                    String addressStr = address.get(0).getLocality();
                    IntroductoryActivity.cityName = addressStr;
                    currentCity.setText(addressStr);
                } catch (IOException e) {
                    // Handle IOException
                } catch (NullPointerException e) {
                    // Handle NullPointerException
                }
            }
        }
    };

    //turning on gps
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getBalance() {
        balanceDb.child("UserWalletBalance").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    reservedBalance = Integer.parseInt(task.getResult().getValue().toString());
                    walletAmountLabel.setText("Rs. "+reservedBalance);
                }
            }
        });
    }
}//ending class