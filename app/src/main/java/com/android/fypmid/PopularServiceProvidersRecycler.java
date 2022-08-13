package com.android.fypmid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PopularServiceProvidersRecycler extends RecyclerView.Adapter<PopularServiceProvidersRecycler.ViewHolder> {
    private Context context;
    private ArrayList<BusinessProfile> arrayList;

    public PopularServiceProvidersRecycler(Context context, ArrayList<BusinessProfile> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PopularServiceProvidersRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_popular_service_providers,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularServiceProvidersRecycler.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(arrayList.get(position).getImageUrl())
                .placeholder(R.drawable.loading2)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(arrayList.get(position).getImageUrl())
                                .placeholder(R.drawable.loading2)
                                .into(holder.image);
                    }
                });
        holder.title.setText(arrayList.get(position).getTitle());
        holder.address.setText(arrayList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,address;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_pop_title);
            address = itemView.findViewById(R.id.recycler_pop_address);
            image = itemView.findViewById(R.id.recycler_pop_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,ServiceProviderProfile.class);
           int pos = this.getAdapterPosition();
            intent.putExtra("PUId",arrayList.get(pos).getUid());
            intent.putExtra("PTitle",arrayList.get(pos).getTitle());
            intent.putExtra("PImg",arrayList.get(pos).getImageUrl());
            intent.putExtra("PContact",arrayList.get(pos).getContact());
            intent.putExtra("PAddress",arrayList.get(pos).getAddress());
            intent.putExtra("PLat",arrayList.get(pos).getLat());
            intent.putExtra("PLon",arrayList.get(pos).getLon());
            intent.putExtra("PRatings",arrayList.get(pos).getRatings());
            intent.putExtra("POpenTime",arrayList.get(pos).getOpenTime());
            intent.putExtra("PCloseTime",arrayList.get(pos).getCloseTime());
            intent.putExtra("POpeningDays",arrayList.get(pos).getOpeningDays());
            context.startActivity(intent);
        }
    }
}
