package com.android.fypmid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class FragmentCompletedBookings extends Fragment {
    View root;
    RecyclerView recyclerView;
    CartRecycler adapter;
    ImageView noItemsImg;
    TextView noItemsText;
    ArrayList<CartItem> bookingItems;
    ArrayList<CartItem> items;
    LinearLayoutManager llm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_completed_bookings, container, false);

        noItemsImg = root.findViewById(R.id.booking_noItemsFoundImg_completed);
        noItemsText = root.findViewById(R.id.booking_noItemsFoundText_completed);
        recyclerView = root.findViewById(R.id.booking_recycler_view_completed);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        bookingItems = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();
        items = new ArrayList<>();
        adapter = new CartRecycler(getContext(),items);
        llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        db.child("CompletedBookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot pushes : snapshot.getChildren()) {
                    CartItem cartItem = pushes.getValue(CartItem.class);
                    if (cartItem.getCartUserId().equals(user.getUid())) {
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
        if(items.size() > 0){
            noItemsImg.setVisibility(View.GONE);
            noItemsText.setVisibility(View.GONE);
        }else{
            noItemsImg.setVisibility(View.VISIBLE);
            noItemsText.setVisibility(View.VISIBLE);
        }

    }
}