package com.android.fypmid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserWallet extends AppCompatActivity {
    int reservedBalance = 0;
    Button addBalance;
    TextInputLayout balance;
    FirebaseAuth auth;
    DatabaseReference db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wallet);
        addBalance = findViewById(R.id.add_balance);
        balance = findViewById(R.id.request_balance);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser();
        getBalance(); // get current balance

        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBalance(); // get current balance
                int newBalance = Integer.parseInt(balance.getEditText().getText().toString());
                int totalBalance = newBalance + reservedBalance;
                db.child("UserWalletBalance").child(user.getUid()).setValue(totalBalance);
                Toast.makeText(UserWallet.this, ""+newBalance + " added. Your new balance is "+totalBalance, Toast.LENGTH_SHORT).show();
                balance.getEditText().setText("");
            }
        });

    }

    public void getBalance() {
        db.child("UserWalletBalance").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    reservedBalance = Integer.parseInt(task.getResult().getValue().toString());
                }
            }
        });
    }
}