package com.android.fypmid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentBookingPendingAdmin extends Fragment {
    View root;
    RecyclerView recyclerView;
    RecyclerPendingBookingsAdmin adapter;
    ImageView noItemsImg;
    TextView noItemsText;
    ArrayList<CartItem> pendingbookingItemsAdmin;
    ArrayList<CartItem> items;
    LinearLayoutManager llm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_booking_pending_admin, container, false);
        noItemsImg = root.findViewById(R.id.booking_noItemsFoundImgPendingAdmin);
        noItemsText = root.findViewById(R.id.booking_noItemsFoundTextPendingAdmin);
        recyclerView = root.findViewById(R.id.fragment_pending_booking_recycler_admin);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        pendingbookingItemsAdmin = new ArrayList<>();
        items = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();

        adapter = new RecyclerPendingBookingsAdmin(getContext(), items);  //adapter helps to fill data in UI Component
        llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);


        db.child("UserBookings").addValueEventListener(new ValueEventListener() { //ValueEventListener called snapshot of the data at this location and each time that data changes
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot pushes : snapshot.getChildren()) {
                    for (DataSnapshot lists : pushes.getChildren()) {
                        UserBookingsFirebase userBookingsFirebase = lists.getValue(UserBookingsFirebase.class);
                        pendingbookingItemsAdmin = userBookingsFirebase.getBookingItems();
                        for (CartItem item : pendingbookingItemsAdmin) {  //enhanced for loop
                            if (item.getUid().equals(user.getUid())) {
                                items.add(item);
                                adapter.notifyDataSetChanged();
                                if (items.size() > 0) {
                                    noItemsImg.setVisibility(View.GONE);
                                    noItemsText.setVisibility(View.GONE);
                                } else {
                                    noItemsImg.setVisibility(View.VISIBLE);
                                    noItemsText.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (items.size() > 0) {
            noItemsImg.setVisibility(View.GONE);
            noItemsText.setVisibility(View.GONE);
        } else {
            noItemsImg.setVisibility(View.VISIBLE);
            noItemsText.setVisibility(View.VISIBLE);
        }

    }
}