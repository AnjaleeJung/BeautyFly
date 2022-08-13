package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class PopularServiceAddCart extends AppCompatActivity {
    private TextView title,charges,cartQty,byTitle,cartValue,selectedDate,selectedTime;
    private ImageView img,minus,plus;
    private int value;
    private Button addCart,selectDate,selectTime;
    private String sPushKey;
    private String sTitle;
    private String sCharges;
    private String sImg;
    private String sUid;
    private String byProviderTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_service_add_cart);
        title = findViewById(R.id.pop_service_title);
        img = findViewById(R.id.pop_service_img);
        charges = findViewById(R.id.pop_service_profile_charges);
        byTitle = findViewById(R.id.pop_service_by_title);
        minus = findViewById(R.id.pop_service_minus);
        plus = findViewById(R.id.pop_service_plus);
        cartValue = findViewById(R.id.pop_service_value);
        selectDate = findViewById(R.id.select_date);
        selectTime = findViewById(R.id.select_time);
        selectedDate = findViewById(R.id.selected_date);
        selectedTime = findViewById(R.id.selected_time);
        addCart = findViewById(R.id.addCartBtn);

        sPushKey = getIntent().getStringExtra("IServicePushKey");
        sTitle = getIntent().getStringExtra("IServiceTitle");
        sCharges = getIntent().getStringExtra("IServiceCharges");
        sImg = getIntent().getStringExtra("IServiceImg");
        sUid = getIntent().getStringExtra("IServiceUid");
        title.setText(sTitle);
        charges.setText(sCharges);
        showCalendar();
//        Toast.makeText(this, ""+sPushKey, Toast.LENGTH_SHORT).show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("BusinessProfile").child(sUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BusinessProfile profile = snapshot.getValue(BusinessProfile.class);
                byProviderTitle = profile.getTitle();
                byTitle.setText("By "+byProviderTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Picasso.get().load(sImg)
                .placeholder(R.drawable.loading2)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(sImg)
                                .placeholder(R.drawable.loading2)
                                .into(img);
                    }
                });


        value = 0;
        plus.setOnClickListener(view -> {
            value++;
            cartValue.setText(Integer.toString(value));
        });
        minus.setOnClickListener(view -> {
            if(value>1){
            value --;
            cartValue.setText(Integer.toString(value));
            }
        });



        addCart.setOnClickListener(view -> {
            String sD = selectedDate.getText().toString();
            String sT = selectedTime.getText().toString();

            if (!(sD.equals(""))&&!(sT.equals(""))) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    CartItem item = new CartItem();
                    item.setTitle(sTitle);
                    item.setByTitle(byProviderTitle);
                    item.setImg(sImg);
                    item.setPushKey(sPushKey);
                    item.setUid(sUid);
                    item.setCartUserId(user.getUid());
                    item.setCharges(sCharges);
                    item.setDate(selectedDate.getText().toString());
                    item.setTime(selectedTime.getText().toString());
                    item.setQty(Integer.parseInt(cartValue.getText().toString()));
                    int chrg = Integer.parseInt(sCharges);
                    int q = Integer.parseInt(cartValue.getText().toString());
                    int tot = chrg * q;
                    item.setTotal(tot);

                    boolean isFound = true;
                        //if cart is empty add new item
                        if (IntroductoryActivity.cartItems.size() == 0) {
                            IntroductoryActivity.cartItems.add(item);
                            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            // if cart is not empty then there could be two cases
                            // 1. add new item which is not in cart or
                            // 2. add same item just update qty, total
                            for (int i = 0; i < IntroductoryActivity.cartItems.size(); i++) {
                                CartItem iteminloop = IntroductoryActivity.cartItems.get(i);
                                if (iteminloop.getPushKey().equals(sPushKey)) {
                                    IntroductoryActivity.cartItems.set(i, item);
                                    Toast.makeText(this, "Cart updated", Toast.LENGTH_SHORT).show();
                                    isFound = false;
                                }
                            }

                            if (isFound) {
                                IntroductoryActivity.cartItems.add(item);
                                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                            }
                        }

                }else{
                        Toast.makeText(this, "Please select Date & Time both are mandatory", Toast.LENGTH_SHORT).show();
                    }


//          IntroductoryActivity.cartItems.add(item);
//            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        });

    }

    //Material Calendar
    private void showCalendar() {
        MaterialDatePicker datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .build();

        selectDate.setOnClickListener(view -> {
            datePicker.show(getSupportFragmentManager(), "Choose Date");
        });
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis((Long) selection);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = format.format(calendar.getTime());
            selectedDate.setText(formattedDate);
            selectedDate.setVisibility(View.VISIBLE);
        });

        selectTime.setOnClickListener(view -> {
            //time
            String am_pm = "";
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            //am pm
            if (mcurrentTime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (mcurrentTime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            TimePickerDialog mTimePicker;
            String finalAm_pm = am_pm;
            mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectedTime.setText( selectedHour + ":" + selectedMinute+" "+ finalAm_pm);
                    selectedTime.setVisibility(View.VISIBLE);
                }
            }, hour, minute, false);//no 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });


    }

}