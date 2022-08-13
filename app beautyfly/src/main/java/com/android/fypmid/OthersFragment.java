package com.android.fypmid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OthersFragment extends Fragment {
    private View root;
    private MaterialCardView profileCard,walletCard,shareCard,aboutCard,feedbackCard,logout;
    private TextView userName,userEmail;
    public static UserHelperClass helperClass;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_others, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        db = firebaseDatabase.getReference();

        profileCard = root.findViewById(R.id.profile_card);
        walletCard = root.findViewById(R.id.others_wallet);
        aboutCard = root.findViewById(R.id.others_about_us);
        logout = root.findViewById(R.id.others_logout);

        userName = root.findViewById(R.id.others_user_name);
        userEmail = root.findViewById(R.id.others_user_email);
        profileCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(),UserProfile.class));
        });
        walletCard.setOnClickListener(view -> {
//            Toast.makeText(getContext(), "This feature is under development", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(),UserWallet.class));

        });
        aboutCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(),AboutUs.class));
        });


        logout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(),Login.class));
            getActivity().finish();
        });


        showAllUserData();

        return root;
    }

    private void showAllUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null) {
            db.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    helperClass = new UserHelperClass();
                    helperClass = snapshot.getValue(UserHelperClass.class);
                    userName.setText(helperClass.getName());
                    userEmail.setText(helperClass.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}