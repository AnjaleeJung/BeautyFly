package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    private PopularServicesRecycler adapter;
    private RecyclerView recyclerView;
    private ArrayList<BusinessService> servicesList;
    private String catValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        recyclerView = findViewById(R.id.recycler_categories);
        servicesList = new ArrayList<>();

        adapter = new PopularServicesRecycler(this, servicesList,R.layout.recycler_popular_services);
        GridLayoutManager llm = new GridLayoutManager(this,2,RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        catValue = getIntent().getStringExtra("CatValue");
        //popular Services
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("BusinessServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot snap2 : snap.getChildren()) {
                        BusinessService service = snap2.getValue(BusinessService.class);
                        if(catValue.equalsIgnoreCase("Men")) {
                            if (service.getCategory().equalsIgnoreCase("Men")) {
                                servicesList.add(service);
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            if (service.getCategory().equalsIgnoreCase("Women")) {
                                servicesList.add(service);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}