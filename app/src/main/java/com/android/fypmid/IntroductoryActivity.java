package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class IntroductoryActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;
    private ImageView image, logo, splashImg;
    private LottieAnimationView lottieAnimationView;
    private Animation topAnim, bottomAnim;
    private String loginAsStatus = "null";
    private FirebaseAuth mAuth;
    public static ArrayList<CartItem> cartItems;
    public static ArrayList<CartItem> bookingItems;
    public static boolean isFirstTime = false;
    public static String cityName = "Unavailable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introductory);
        cartItems = new ArrayList<>();
        bookingItems = new ArrayList<>();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        chooseUserActivity();

        //Hooks
        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        //  slogan = findViewById(R.id.textView2);
        splashImg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);
        //animations
        splashImg.animate().translationY(-1600).setDuration(1000).setStartDelay(4000).alpha(0);
        image.animate().translationY(1400).setDuration(1000).setStartDelay(4000).alpha(0);
        logo.animate().translationY(1400).setDuration(1000).setStartDelay(4000).alpha(0);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000).alpha(0);

        //timer
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginAsStatus.equalsIgnoreCase("User")) {
                    startActivity(new Intent(IntroductoryActivity.this, MainActivity.class));
                    finish();
                }else if(loginAsStatus.equalsIgnoreCase("null")){
                    startActivity(new Intent(IntroductoryActivity.this, Login.class));
                    finish();
                }else{
                    startActivity(new Intent(IntroductoryActivity.this, AdminActivity.class));
                    finish();
                }

            }

        }, SPLASH_SCREEN);

    }
    private void chooseUserActivity() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            db.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        UserHelperClass userHelperClass = task.getResult().getValue(UserHelperClass.class);
                        if(userHelperClass!=null)
                        loginAsStatus = userHelperClass.getLoginAs();
                    }else{
                        Toast.makeText(IntroductoryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            loginAsStatus = "null";

        }

    }
}