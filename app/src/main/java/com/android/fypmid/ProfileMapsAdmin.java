package com.android.fypmid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ProfileMapsAdmin extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private double lat = 0;
    private double longg = 0;
    private FloatingActionButton fab;
    private SupportMapFragment mapFragment;
    private View root;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    private Button updateProfile, logout, openTime, closeTime;
    private TextInputLayout title, phone, address;
    private ImageView img;
    private String openingTimeString;
    private int openingHour24, closingHour24;
    private String imgUrl;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private SwitchMaterial monSwitch, tueSwitch, wedSwitch, thurSwitch, friSwitch, satSwitch, sunSwitch;
    SimpleDateFormat parser = null;
    double oT, cT;
    Date opTime = null;
    Date clTime = null;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.clear();
            LatLng sydney = new LatLng(lat, longg);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile_maps_admin, container, false);
        title = root.findViewById(R.id.profile_admin_title);
        phone = root.findViewById(R.id.profile_admin_phone);
        address = root.findViewById(R.id.profile_admin_address);
        img = root.findViewById(R.id.profile_admin_img);
        fab = root.findViewById(R.id.fab_exact_location);
        //initializing switches
        openTime = root.findViewById(R.id.openingTime);
        closeTime = root.findViewById(R.id.closingTime);
        monSwitch = root.findViewById(R.id.monday_switch);
        tueSwitch = root.findViewById(R.id.tuesday_switch);
        wedSwitch = root.findViewById(R.id.wednesday_switch);
        thurSwitch = root.findViewById(R.id.thursday_switch);
        friSwitch = root.findViewById(R.id.friday_switch);
        satSwitch = root.findViewById(R.id.saturday_switch);
        sunSwitch = root.findViewById(R.id.sunday_switch);
        updateProfile = root.findViewById(R.id.profile_admin_update);
        logout = root.findViewById(R.id.profile_admin_logout);
        parser = new SimpleDateFormat("HH:mm a");

        mAuth = FirebaseAuth.getInstance();
        //get data from firebase
        getBusinessProfile();

        //gps
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        //getting opening time and converting it to 24 hours
//        oT = Double.parseDouble(openTime.getText().toString());

        //loading
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please Wait");
        dialog.setMessage("Processing...");

        //upload image
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileMapsAdmin.this)
//                        .crop()
//                        .galleryOnly()//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        fab.setOnClickListener(view -> {
            if (mapFragment != null) {
                getLocationUpdates();
                mapFragment.getMapAsync(callback);
            }
        });

        openingTimeString = openTime.getText().toString();
        openTime.setOnClickListener(view -> {
            showTimePicker(8, 0, "Opening Time", openTime, true);
        });
        closeTime.setOnClickListener(view -> {
            showTimePicker(10, 0, "Closing Time", closeTime, true);
        });

        updateProfile.setOnClickListener(view -> {
            String titleTxt = title.getEditText().getText().toString();
            String phoneTxt = phone.getEditText().getText().toString();
            String addressTxt = address.getEditText().getText().toString();

            ArrayList<String> openDays;

            //opening closing time
            String startTime = openTime.getText().toString();
            String endTime = closeTime.getText().toString();

            //monday
            openDays = new ArrayList<>();
            if (monSwitch.isChecked())
                openDays.add("Monday");
            //tuesday
            if (tueSwitch.isChecked())
                openDays.add("Tuesday");
            //wednesday
            if (wedSwitch.isChecked())
                openDays.add("Wednesday");
            //Thursday
            if (thurSwitch.isChecked())
                openDays.add("Thursday");
            //Friday
            if (friSwitch.isChecked())
                openDays.add("Friday");
            //Saturday
            if (satSwitch.isChecked())
                openDays.add("Saturday");
            //Sunday
            if (sunSwitch.isChecked())
                openDays.add("Sunday");
            dialog.show();

            addProfileToFirebase(titleTxt, phoneTxt, addressTxt, imgUrl, Double.toString(lat), Double.toString(longg), startTime, endTime, openDays);
        });

        logout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        });

        //                openingHour24 = hour;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//
//            try {
//                opTime = parser.parse(openTime.getText().toString());
//                Toast.makeText(getContext(), ""+opTime, Toast.LENGTH_SHORT).show();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

        return root;
    }

    //image picker result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            img.setImageURI(uri);
            dialog.show();
            uploadToFirebase(uri);
        }
    }

    //firebase storage for saving images
    public void uploadToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        StorageReference sRef = reference.child("BusinessImages/" + uri.getLastPathSegment());
        UploadTask task = sRef.putFile(uri);
        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                Toast.makeText(getContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        imgUrl = task.getResult().toString();
                    }
                });
            }
        });
    }

    // adding profile data to realtime database
    public void addProfileToFirebase(String title, String phone, String address, String imgUrl, String lat, String lon, String startTime, String endTime, ArrayList<String> days) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();

        BusinessProfile profile = new BusinessProfile();
        profile.setTitle(title);
        profile.setContact(phone);
        profile.setAddress(address);
        profile.setImageUrl(imgUrl);
        profile.setLat(lat);
        profile.setLon(lon);
        profile.setOpenTime(startTime);
        profile.setCloseTime(endTime);
        profile.setOpeningDays(days);
        profile.setUid(uId);

        db.child("BusinessProfile").child(uId).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.hide();
                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //getting profile from firebase realtime db
    private void getBusinessProfile() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();
        db.child("BusinessProfile").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BusinessProfile profile = snapshot.getValue(BusinessProfile.class);
                    title.getEditText().setText(profile.getTitle());
                    phone.getEditText().setText(profile.getContact());
                    address.getEditText().setText(profile.getAddress());
                    lat = Double.parseDouble(profile.getLat());
                    longg = Double.parseDouble(profile.getLon());
                    openTime.setText(profile.getOpenTime());
                    closeTime.setText(profile.getCloseTime());
                    //getting days
                    ArrayList<String> openDays = profile.getOpeningDays();
                    if (openDays.contains("Monday"))
                        monSwitch.setChecked(true);
                    else
                        monSwitch.setChecked(false);
                    //tuesday
                    if (openDays.contains("Tuesday"))
                        tueSwitch.setChecked(true);
                    else
                        tueSwitch.setChecked(false);
                    //wednesday
                    if (openDays.contains("Wednesday"))
                        wedSwitch.setChecked(true);
                    else
                        wedSwitch.setChecked(false);
                    //Thursday
                    if (openDays.contains("Thursday"))
                        thurSwitch.setChecked(true);
                    else
                        thurSwitch.setChecked(false);
                    //Friday
                    if (openDays.contains("Friday"))
                        friSwitch.setChecked(true);
                    else
                        friSwitch.setChecked(false);
                    //Saturday
                    if (openDays.contains("Saturday"))
                        satSwitch.setChecked(true);
                    else
                        satSwitch.setChecked(false);
                    //Sunday
                    if (openDays.contains("Sunday"))
                        sunSwitch.setChecked(true);
                    else
                        sunSwitch.setChecked(false);

                    Picasso.get().load(profile.getImageUrl()).placeholder(R.drawable.loading2).into(img);
                } else {
                    getLocationUpdates();
                }
                //update map location according to firebase
                if (mapFragment != null) {
                    mapFragment.getMapAsync(callback);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showTimePicker(int defaultHout, int defaultMinute, String title, Button btn, boolean checkValidation) {
        //start time picker
        MaterialTimePicker mtp = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(defaultHout)
                .setMinute(defaultMinute)
                .setTitleText(title)
                .build();
        mtp.show(getChildFragmentManager(), null);
        mtp.addOnPositiveButtonClickListener(view1 -> {
            int hour = mtp.getHour();
            int minute = mtp.getMinute();
            String time = getTime(hour, minute);

            openingHour24 = hour;
            //global variable for validation purpose
            openingTimeString = time;

//            try {
////                oT = Double.parseDouble(convertTo24HoursFormat(time));
//            Toast.makeText(getContext(), ""+convertTo24HoursFormat(time), Toast.LENGTH_SHORT).show();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }


            //                openingHour24 = hour;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                try {
                    opTime = parser.parse(openingTimeString);
//                    Toast.makeText(getContext(), ""+opTime, Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //validation
            if (checkValidation) {


//                closingHour24 = hour;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                    try {
                        clTime = parser.parse(time);
//                        Toast.makeText(getContext(), ""+clTime, Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
//                try {
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                        opTime = parser.parse(openingTimeString);
//
////                        Toast.makeText(getContext(), ""+opTime, Toast.LENGTH_SHORT).show();
//                    }

//                    Toast.makeText(getContext(), ""+closingHour24, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), ""+openingHour24, Toast.LENGTH_SHORT).show();
//                try {
//                    cT = Double.parseDouble(convertTo24HoursFormat(closeTime.getText().toString()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                if (opTime.before(clTime)) {
                    btn.setText(time);
                } else {
                    Toast.makeText(getContext(), "Closing time cannot be set before opening! ", Toast.LENGTH_SHORT).show();
                }
//                } catch (ParseException e) {
//                    // Invalid date was entered
//                }
            } else {
                btn.setText(time);
            }
        });
    }


    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("hh:mm a");
        }

        return formatter.format(tme);
    }

    public String convertTo24HoursFormat(String twelveHourTime)
            throws ParseException {
        // Replace with KK:mma if you want 0-11 interval
        final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mm a");
        // Replace with kk:mm if you want 1-24 interval
        final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
        return TWENTY_FOUR_TF.format(
                TWELVE_TF.parse(twelveHourTime));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
//            getLocationUpdates();
            mapFragment.getMapAsync(callback);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocationUpdates() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);

        } else {
            EasyPermissions.requestPermissions((Activity) getContext(), "Please Accept Permissions ", 1, perms);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            lat = location.getLatitude();
            longg = location.getLongitude();
            Toast.makeText(getContext(), "Live location is ready, you can update now.", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), "Lat: "+lat + " Longg: "+longg, Toast.LENGTH_SHORT).show();
            mapFragment.getMapAsync(callback);

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


}