package com.android.fypmid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartFragment extends Fragment {
    View root;
    RecyclerView recyclerView;
    CartRecycler adapter;
    int reservedBalance = 0;
    TextView total, noItemsFoundText;
    Button bookNow, clearCart;
    MaterialCardView cardView;
    ImageView cartImage;
    FirebaseAuth auth;
    DatabaseReference balanceDb;
    FirebaseUser user;
    int t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = root.findViewById(R.id.cart_recycler_view);
        total = root.findViewById(R.id.cartCard_service_title);
        bookNow = root.findViewById(R.id.bookNowBtnCart);
        clearCart = root.findViewById(R.id.clearCart);
        cardView = root.findViewById(R.id.cartTotalCard);
        cartImage = root.findViewById(R.id.cart_image_view);
        noItemsFoundText = root.findViewById(R.id.noItemsFound);

        auth = FirebaseAuth.getInstance();
        balanceDb = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser();
        getBalance(); // get current balance

        adapter = new CartRecycler(getContext(), IntroductoryActivity.cartItems);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);  //llm linear layout manager
        recyclerView.setAdapter(adapter);

        if (IntroductoryActivity.cartItems.size() == 0) {
            cardView.setVisibility(View.INVISIBLE);
//            clearCart.setVisibility(View.INVISIBLE);

        } else {
            for (CartItem item : IntroductoryActivity.cartItems) {
                t += item.getTotal();
            }

            total.setText("Total: RS. " + t);
        }

        clearCart.setOnClickListener(view -> {
            IntroductoryActivity.cartItems.clear();
            adapter.notifyDataSetChanged();
            cardView.setVisibility(View.INVISIBLE);
            cartImage.setVisibility(View.VISIBLE);
            noItemsFoundText.setVisibility(View.VISIBLE);
        });

        bookNow.setOnClickListener(view -> {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            if(reservedBalance<t) {
                Toast.makeText(getContext(), "You don't have enough balance. Please add balance first!", Toast.LENGTH_SHORT).show();
            }else{

                String key = db.child("UserBookings").child(user.getUid()).push().getKey();
                for (CartItem item:IntroductoryActivity.cartItems) {
                    item.setPushKey(key);
                }

                UserBookingsFirebase userBookingsFirebase = new UserBookingsFirebase(IntroductoryActivity.cartItems);
                int minusFromBalance = reservedBalance - t;


//                Toast.makeText(getContext(), ""+key, Toast.LENGTH_SHORT).show();
//                db.child("UserBookings").child(user.getUid()).push().setValue(userBookingsFirebase).addOnCompleteListener(new OnCompleteListener<Void>() {
                db.child("UserBookings").child(user.getUid()).child(key).setValue(userBookingsFirebase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cardView.setVisibility(View.INVISIBLE);
                        cartImage.setVisibility(View.VISIBLE);
                        noItemsFoundText.setVisibility(View.VISIBLE);
                        IntroductoryActivity.cartItems.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Your remaining balance is "+minusFromBalance, Toast.LENGTH_SHORT).show();
                        db.child("UserWalletBalance").child(user.getUid()).setValue(minusFromBalance);
                        Toast.makeText(getContext(), "Booking Placed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (IntroductoryActivity.cartItems.size() > 0) {
            cartImage.setVisibility(View.GONE);
            noItemsFoundText.setVisibility(View.GONE);
        } else {
            cartImage.setVisibility(View.VISIBLE);
            noItemsFoundText.setVisibility(View.VISIBLE);
        }

    }

    public void getBalance() {
        balanceDb.child("UserWalletBalance").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    reservedBalance = Integer.parseInt(task.getResult().getValue().toString());  //parseInt converts string to a int
                }
            }
        });
    }
}