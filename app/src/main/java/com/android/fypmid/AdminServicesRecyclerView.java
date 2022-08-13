package com.android.fypmid;

import android.annotation.SuppressLint; // Indicates that Lint should ignore the specified warnings for the annotated element.
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;  //it takes an XML file as input and builds the View objects from it.
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener; //Task completes successfully
import com.google.android.material.chip.Chip;  //Chips are compact elements that represent an attribute, text, entity, or action. They allow users to enter information, select a choice
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot; //data from a Firebase Database location
import com.google.firebase.database.DatabaseError;  //passed to callbacks when an operation failed.
import com.google.firebase.database.DatabaseReference;  //represents a particular location
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;  //called snapshot of the data at this location and each time that data changes
import com.squareup.picasso.Callback;  //powerful image downloading and caching library
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminServicesRecyclerView extends RecyclerView.Adapter<AdminServicesRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<BusinessService> arrayList;

    public AdminServicesRecyclerView( Context context,ArrayList<BusinessService> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdminServicesRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_services_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminServicesRecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(arrayList.get(position).getImage())
                                .into(holder.imageView);
                    }
                });
        holder.title.setText(arrayList.get(position).getServiceTitle());
        holder.charges.setText("Rs. "+arrayList.get(position).getServiceCharges());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title,charges;
        Button delete;
        public ViewHolder(@NonNull View itemView) {  //itemView calls new SomeViewHolder
            super(itemView); //call super class methods from parent
            imageView = itemView.findViewById(R.id.service_recycler_image);
            title = itemView.findViewById(R.id.service_recycler_title);
            charges = itemView.findViewById(R.id.service_recycler_charges);
            delete = itemView.findViewById(R.id.delete_service_admin);

            // for deleting
            delete.setOnClickListener(view -> {
                int pos = this.getAdapterPosition();
                String pKey = arrayList.get(pos).getPushKey();
//                arrayList.remove(pos);
                notifyItemRemoved(pos);
                deleteService(pKey);

            });
        }
    }


    private void deleteService(String key){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDatabase.getReference();
        FirebaseAuth  mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();
        db.child("BusinessServices").child(uId).child(key).removeValue().addOnCompleteListener(runnable -> {
            Toast.makeText(context, "Service Deleted", Toast.LENGTH_SHORT).show();
        });
    }
}
