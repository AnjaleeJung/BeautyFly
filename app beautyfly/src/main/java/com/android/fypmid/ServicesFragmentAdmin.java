package com.android.fypmid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServicesFragmentAdmin extends Fragment {
    private View root;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<BusinessService> servicesList;
    private AdminServicesRecyclerView adapter;
    private String uId;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db;
    private TextView noServicesFound;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_services_admin, container, false);
        fab = root.findViewById(R.id.fab_admin_service);
        noServicesFound = root.findViewById(R.id.no_services_found);

        recyclerView = root.findViewById(R.id.services_recyclerView);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fab.setOnClickListener(view -> {
            startActivity(new Intent(getContext(),AddService.class));
        });

        uId = user.getUid();
        db = FirebaseDatabase.getInstance().getReference();
        servicesList = new ArrayList<>();
        db.child("BusinessServices").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servicesList.clear();
                for (DataSnapshot snap:snapshot.getChildren()) {
                    BusinessService service = snap.getValue(BusinessService.class);
                    servicesList.add(service);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new AdminServicesRecyclerView(getContext(),servicesList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        return root;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        //no service found textview text hide or show
//        if(servicesList.size() == 0){
//            noServicesFound.setVisibility(View.VISIBLE);
//        }else{
//            noServicesFound.setVisibility(View.GONE);
//        }
//    }
}