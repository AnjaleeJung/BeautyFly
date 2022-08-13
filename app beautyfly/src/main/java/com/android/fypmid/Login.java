package com.android.fypmid;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    Button callSignUp, login_btn,resetpass;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout email, password;
    private String loginAsStatus = null;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This line will hide the status bar from the screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
   
        //progress dialog
        pd = new ProgressDialog(Login.this);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading");
        pd.setCancelable(false);

        //Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        resetpass = findViewById(R.id.forgetpass);

        View login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(view -> {
            //sign in
            String userEmail = email.getEditText().getText().toString();
            String userPass = password.getEditText().getText().toString();
            mAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        pd.hide();
                        chooseUserActivity();
                    } else {
                        pd.hide();
                        Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

        View callSignUp = findViewById(R.id.callSignUp);
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(email, "username_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(Login.this,PasswordReset.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            chooseUserActivity();
//        }
    }

    private void chooseUserActivity() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            pd.show();
            db.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (task.isSuccessful()) {
                        UserHelperClass userHelperClass = task.getResult().getValue(UserHelperClass.class);
                        if (userHelperClass != null){
                            loginAsStatus = userHelperClass.getLoginAs();
                        if (loginAsStatus.equalsIgnoreCase("User")) {
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(Login.this, AdminActivity.class));
                            finish();
                        }
                        pd.hide();
                    }
//                    } else {
//                        pd.hide();
//                    }
                }
            });
        }
    }

}

