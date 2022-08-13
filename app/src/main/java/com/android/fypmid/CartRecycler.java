package com.android.fypmid;

import android.annotation.SuppressLint;  // Indicates that Lint should ignore the specified warnings for the annotated element.
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartRecycler extends RecyclerView.Adapter<CartRecycler.ViewHolder> {
    private Context context;
    private ArrayList<CartItem> items;

    public CartRecycler(Context context, ArrayList<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CartRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.cart_design_recycler,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecycler.ViewHolder holder, @SuppressLint("RecyclerView") int position) { //BindViewHolder pass data to view holder
        Picasso.get().load(items.get(position).getImg())
                .placeholder(R.drawable.loading2)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(items.get(position).getImg())
                                .placeholder(R.drawable.loading2)  //placeholder provides virtual object that positions existing object
                                .into(holder.imageView);
                    }
                });

        holder.title.setText(items.get(position).getTitle());
        holder.byTitle.setText("By "+items.get(position).getByTitle());
        holder.charges.setText("RS. "+items.get(position).getCharges());
        holder.qty.setText("Qty :"+items.get(position).getQty());
        holder.date.setText("Date: "+items.get(position).getDate());
        holder.time.setText("Time: "+items.get(position).getTime());
        holder.total.setText("Total: "+items.get(position).getTotal());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,byTitle,charges,date,time,qty,total;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cart_service_img);
            title = itemView.findViewById(R.id.cart_service_title);
            byTitle = itemView.findViewById(R.id.cart_by_title);
            charges = itemView.findViewById(R.id.cart_profile_charges);
            date = itemView.findViewById(R.id.cart_date);
            time = itemView.findViewById(R.id.cart_time);
            qty = itemView.findViewById(R.id.cart_quantity);
            total = itemView.findViewById(R.id.cart_total);
        }
    }
}
