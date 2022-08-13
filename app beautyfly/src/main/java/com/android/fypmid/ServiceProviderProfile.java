package com.android.fypmid;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceProviderProfile extends AppCompatActivity {
    ImageView img;
    TextView title, contact, open, close, address;
    RatingBar ratings;
    private PopularServicesRecycler servicesAdapter;
    private RecyclerView recyclerView;
    private ArrayList<BusinessService> servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);
        img = findViewById(R.id.p_img);
        title = findViewById(R.id.p_title);
        address = findViewById(R.id.p_address);
        contact = findViewById(R.id.p_contact);
        open = findViewById(R.id.p_opening_time);
        close = findViewById(R.id.p_closing_time);

        recyclerView = findViewById(R.id.recyclerview_profile);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        String pUid = getIntent().getStringExtra("PUId");
        String pImg = getIntent().getStringExtra("PImg");
        String pTitle = getIntent().getStringExtra("PTitle");
        String pContact = getIntent().getStringExtra("PContact");
        String pAddress = getIntent().getStringExtra("PAddress");
        String pOpenTime = getIntent().getStringExtra("POpenTime");
        String pCloseTime = getIntent().getStringExtra("PCloseTime");

        title.setText(pTitle);
        address.setText("Address: "+pAddress);
        contact.setText("Cell#: "+pContact);
        open.setText( "Opening Time: "+pOpenTime);
        close.setText("Closing Time: "+pCloseTime);
        Picasso.get().load(pImg)
                .placeholder(R.drawable.loading2)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(pImg)
                                .placeholder(R.drawable.loading2)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(img);
                    }
                });

        //services
        servicesList = new ArrayList<>();
        db.child("BusinessServices").child(pUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                        BusinessService service = snap.getValue(BusinessService.class);
                        servicesList.add(service);
                        servicesAdapter.notifyDataSetChanged();
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



    }
}