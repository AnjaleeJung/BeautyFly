package com.android.fypmid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerPendingBookingsAdmin extends RecyclerView.Adapter<RecyclerPendingBookingsAdmin.ViewHolder> {
    private Context context;
    private ArrayList<CartItem> items;

    public RecyclerPendingBookingsAdmin(Context context, ArrayList<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerPendingBookingsAdmin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.recycler_fragment_pending_design_admin,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerPendingBookingsAdmin.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                                .placeholder(R.drawable.loading2)
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        Button completenow;
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
            completenow = itemView.findViewById(R.id.completenowadmin);
            completenow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            int pos = this.getAdapterPosition();
            CartItem cartItem = new CartItem();
            cartItem = items.get(pos);
            CartItem finalCartItem = cartItem;
//            Toast.makeText(context, ""+items.size(), Toast.LENGTH_SHORT).show();
//            items.remove(pos);
            db.child("CompletedBookings").push().setValue(cartItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
//                    Toast.makeText(context, ""+items.size(), Toast.LENGTH_SHORT).show();
                    UserBookingsFirebase userBookingsFirebase = new UserBookingsFirebase(items);
                    db.child("UserBookings")
                            .child(finalCartItem.getCartUserId())
                            .child(finalCartItem.getPushKey()).removeValue();
                    Toast.makeText(context, "Completed!", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }
}
