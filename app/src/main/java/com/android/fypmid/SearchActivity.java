package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private PopularServicesRecycler servicesAdapter;
    private RecyclerView recyclerView;
    private ArrayList<BusinessService> servicesList;
    private TextInputEditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.search_recycler);
        search = findViewById(R.id.searcheditext);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        //services
        servicesList = new ArrayList<>();
        db.child("BusinessServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot snap2 : snap.getChildren()) {
                        BusinessService service = snap2.getValue(BusinessService.class);
                        servicesList.add(service);
                        servicesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        servicesAdapter = new PopularServicesRecycler(this, servicesList,R.layout.recycler_services_vertical);
        LinearLayoutManager llmServices = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llmServices);
        recyclerView.setAdapter(servicesAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<BusinessService> model = new ArrayList<>();
                String str  = search.getText().toString();
                for (BusinessService service:servicesList) {
                    if( service.getServiceTitle().toLowerCase().contains(str.toLowerCase())){
                        model.add(service);
                        servicesAdapter = new PopularServicesRecycler(SearchActivity.this, model,R.layout.recycler_services_vertical);
                        LinearLayoutManager llmServices = new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false);
                        recyclerView.setLayoutManager(llmServices);
                        recyclerView.setAdapter(servicesAdapter);
                        servicesAdapter.notifyDataSetChanged();
                    }

                }
            }
        });


    }
}