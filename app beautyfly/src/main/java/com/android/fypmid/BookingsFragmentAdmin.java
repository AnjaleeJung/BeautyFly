package com.android.fypmid;

import android.os.Bundle; //used to pass data between activities

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class BookingsFragmentAdmin extends Fragment {
    View root;
    TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, //ViewGroup special view contains others views
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_bookings_admin, container, false);
        tabLayout = root.findViewById(R.id.bookingtablayoutadmin);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        changeFragment(new FragmentBookingPendingAdmin());
                        break;
                    case 1:
                        changeFragment(new FragmentBookingsCompletedAdmin());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    return root;
    }
    private void changeFragment(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.bookingfragmentviewadmin,fragment)
                .commit();
    }
}