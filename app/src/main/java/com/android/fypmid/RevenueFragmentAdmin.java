package com.android.fypmid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RevenueFragmentAdmin extends Fragment {
    View root;
    TextView revenue;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseUser user;
    int totalRevenue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_revenue_admin, container, false);
        revenue = root.findViewById(R.id.revenue_balance_admin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();

        db.child("CompletedBookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()) {
                    CartItem item = snap.getValue(CartItem.class);
                    if(item.getUid().equals(user.getUid())){
                        totalRevenue += item.getTotal();
                        //10% commission will be given to admin
                        int afterCommissionAmount = totalRevenue - (totalRevenue / 100 * 10);
                        revenue.setText("Rs. "+afterCommissionAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
}