package com.android.fypmid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentBookingsCompletedAdmin extends Fragment {
    View root;
    RecyclerView recyclerView;
    CartRecycler adapter;
    ImageView noItemsImg;
    TextView noItemsText;
    ArrayList<CartItem> completedBookingItemsAdmin;
    ArrayList<CartItem> items;
    LinearLayoutManager llm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_bookings_completed_admin, container, false);
        noItemsImg = root.findViewById(R.id.booking_noItemsFoundImgCompletedAdmin);
        noItemsText = root.findViewById(R.id.booking_noItemsFoundTextCompletedAdmin);
        recyclerView = root.findViewById(R.id.fragment_completed_booking_recycler_admin);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        completedBookingItemsAdmin = new ArrayList<>();
        items = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();


        adapter = new CartRecycler(getContext(), items);
        llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        db.child("CompletedBookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot pushes : snapshot.getChildren()) {
//                    for (DataSnapshot lists : pushes.getChildren()) {
                    CartItem cartItem = pushes.getValue(CartItem.class);
//                        UserBookingsFirebase userBookingsFirebase = lists.getValue(UserBookingsFirebase.class);
//                        pendingbookingItemsAdmin = userBookingsFirebase.getBookingItems();


//                        for (CartItem item : pendingbookingItemsAdmin) {
                    if (cartItem.getUid().equals(user.getUid())) {
                        items.add(cartItem);
                        adapter.notifyDataSetChanged();
                        if (items.size() > 0) {
                            noItemsImg.setVisibility(View.GONE);
                            noItemsText.setVisibility(View.GONE);
                        } else {
                            noItemsImg.setVisibility(View.VISIBLE);
                            noItemsText.setVisibility(View.VISIBLE);
                        }
                    }
//                        }
//                    }
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