package com.android.fypmid;

import androidx.appcompat.app.AppCompatActivity;  //AppCompat apps developed with newer versions work with older versions
import androidx.fragment.app.Fragment;  //Application's user interface or behavior that can be placed in an Activity

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_admin);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){    //getItemID method mainly designed to attached data with items in the list.
                case R.id.bookings_menu_admin:
                    changeFragment(new BookingsFragmentAdmin());
                    break;
                case R.id.services_menu_admin:
                    changeFragment(new ServicesFragmentAdmin());
                    break;
                case R.id.revenue_menu_admin:
                    changeFragment(new RevenueFragmentAdmin());
                    break;
                case R.id.profile_menu_admin:
                    changeFragment(new ProfileMapsAdmin());
                    break;
            }
            return true;
        });
    }
    private void changeFragment(Fragment fragment){  //replace an existing fragment in a container with an instance of a new fragment class that you provide
        getSupportFragmentManager()
                .beginTransaction()  //Start a series of edit operations on the Fragments associated with this FragmentManager.
                .setReorderingAllowed(true)  //It allows all the execution to happen all at once without changing your fragment state and then at the very end we bring up all the fragments
                .replace(R.id.fragment_container_view_admin,fragment)
                .commit();  //You will get a dialog to examine all files that will be added, enter commit messages and commit. You can deselect any files that you don't want to belong to this commit
    }
}