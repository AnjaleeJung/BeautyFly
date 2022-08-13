package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo;
    TextView loginAs,titleName,balance;
    UserHelperClass helperClass;
    FirebaseAuth auth;
    DatabaseReference db;
    int reservedBalance = 0;
    FirebaseUser user;
    String userEmail;
    String uId;
    Button updateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        helperClass = OthersFragment.helperClass;
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser();
        getBalance();
        //Hooks
        titleName = findViewById(R.id.full_name);
        fullName = findViewById(R.id.full_name_profile);
        loginAs = findViewById(R.id.profile_as);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        balance = findViewById(R.id.payment_label);
        updateBtn = findViewById(R.id.updateBtn);



        if(helperClass !=null) {
        //ShowAllData
        titleName.setText(helperClass.getName());
        fullName.getEditText().setText(helperClass.getName());
        loginAs.setText(helperClass.getLoginAs());
        email.getEditText().setText(helperClass.getEmail());
        phoneNo.getEditText().setText(helperClass.getPhoneNo());

            userEmail = helperClass.getEmail();

    }
       uId = user.getUid();
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.child("Users").child(uId).child("name").setValue(fullName.getEditText().getText().toString());
                db.child("Users").child(uId).child("phoneNo").setValue(phoneNo.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                        titleName.setText(fullName.getEditText().getText().toString());

                    }
                });


            }
        });

    }

    private void getBalance() {
        db.child("UserWalletBalance").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    reservedBalance = Integer.parseInt(task.getResult().getValue().toString());
                    balance.setText("Rs."+reservedBalance);
                }
            }
        });
    }


}