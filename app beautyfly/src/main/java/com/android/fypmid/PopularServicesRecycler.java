package com.android.fypmid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class PopularServicesRecycler extends RecyclerView.Adapter<PopularServicesRecycler.ViewHolder> {
    private Context context;
    private ArrayList<BusinessService> arrayList;
    private int layout;

    public PopularServicesRecycler(Context context, ArrayList<BusinessService> arrayList,int layout) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public PopularServicesRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularServicesRecycler.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(arrayList.get(position).getImage())
                .placeholder(R.drawable.loading2)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(arrayList.get(position).getImage())
                                .placeholder(R.drawable.loading2)
                                .into(holder.image);
                    }
                });
        holder.title.setText(arrayList.get(position).getServiceTitle());
        holder.charges.setText(arrayList.get(position).getServiceCharges());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("BusinessProfile").child(arrayList.get(position).getuId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             BusinessProfile profile = snapshot.getValue(BusinessProfile.class);
             holder.byTitle.setText("By "+profile.getTitle());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        holder.ratingBar.setRating(Float.parseFloat(arrayList.get(position).getServiceRatings()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,byTitle;
        ImageView image;
        TextView charges;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_service_title);
            image = itemView.findViewById(R.id.recycler_service_img);
            charges = itemView.findViewById(R.id.recycler_service_profile_charges);
            ratingBar = itemView.findViewById(R.id.service_rating);
            byTitle = itemView.findViewById(R.id.recycler_service_profile_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = this.getAdapterPosition();
            Intent intent = new Intent(context,PopularServiceAddCart.class);
            intent.putExtra("IServicePushKey",arrayList.get(pos).getPushKey());
            intent.putExtra("IServiceImg",arrayList.get(pos).getImage());
            intent.putExtra("IServiceTitle",arrayList.get(pos).getServiceTitle());
            intent.putExtra("IServiceUid",arrayList.get(pos).getuId());
            intent.putExtra("IServiceCharges",arrayList.get(pos).getServiceCharges());

            context.startActivity(intent);
        }
    }
}
