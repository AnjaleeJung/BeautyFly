package com.android.beautyflyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView revenue;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseUser user;
    int totalRevenue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        revenue = findViewById(R.id.revenue_balance_admin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();

        db.child("CompletedBookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()) {
                    CartItem item = snap.getValue(CartItem.class);
                        totalRevenue += item.getTotal();
                        //10% commission will be given to admin
                        int afterCommissionAmount = totalRevenue - (totalRevenue / 100 * 90);
                        revenue.setText("Rs. "+afterCommissionAmount);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}