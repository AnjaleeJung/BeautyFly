package com.android.fypmid;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    private TextInputLayout dropdown_login, name, password, email, phoneNo;
    private AutoCompleteTextView Loginas;
    private Button reg_btn, Login;
    private ArrayList<String> arrayList_Loginas;
    private ArrayAdapter<String> arrayAdapter_Loginas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    private UserHelperClass helperClass;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //This line will hide the status bar from the screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // realtime database
        firebaseDatabase = FirebaseDatabase.getInstance();
        db = firebaseDatabase.getReference();

        helperClass = new UserHelperClass();

        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        phoneNo = findViewById(R.id.reg_phoneNo);
        password = findViewById(R.id.reg_password);
        Loginas = findViewById(R.id.Loginas);
        reg_btn = findViewById(R.id.reg_btn);
        Login = findViewById(R.id.login);
        pd = new ProgressDialog(SignUp.this);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading");
        pd.setCancelable(false);

        reg_btn.setOnClickListener(view -> {
            //if validation is okay
            if( validateEmail() && validatePassword() && validatephoneNo()){
                pd.show();
                //create user in firebase
                String userEmail = email.getEditText().getText().toString();
                String userPass = password.getEditText().getText().toString();
                String userPhone = phoneNo.getEditText().getText().toString();
                mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            //Get all the values
                            String reg_name = name.getEditText().getText().toString();
                            String reg_email = email.getEditText().getText().toString();
                            String reg_phoneNo = phoneNo.getEditText().getText().toString();
                            String LoginAs = dropdown_login.getEditText().getText().toString();

                            helperClass.setName(reg_name);
                            helperClass.setEmail(reg_email);
                            helperClass.setPhoneNo(reg_phoneNo);
                            helperClass.setLoginAs(LoginAs);
                            db.child("Users").child(userId).setValue(helperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.hide();
                                    Toast.makeText(SignUp.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    if(LoginAs.equalsIgnoreCase("User")){
                                        startActivity(new Intent(SignUp.this,MainActivity.class));
                                        finish();
                                    }else{
                                        startActivity(new Intent(SignUp.this,AdminActivity.class));
                                        finish();
                                    }
                                }
                            });

                        } else {
                            pd.hide();
                            Toast.makeText(SignUp.this, "Account sign up failed!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }//validation end

        });


        //Save data in Firebase on button click
//        reg_btn.setOnClickListener(new View.OnClickListener() {
//
//
//
//
//            @Override
//            public void onClick (View view)  {
//                rootNode = FirebaseDatabase.getInstance();
//                reference = rootNode.getReference().child("UserHelperClass");
//
//                UserHelperClass helperClass = new UserHelperClass();
//
//                reg_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!validateName() | !validateEmail() | !validatePassword()){
//                            return;
//                        }
//
//                        //Get all the values
//
//                        String reg_name = name.getEditText().getText().toString();
//                        String reg_email = email.getEditText().getText().toString();
//                        String reg_phoneNo = phoneNo.getEditText().getText().toString();
//                        String reg_password = password.getEditText().getText().toString();
//                        String Loginas = dropdown_login.getEditText().getText().toString();
//
//                        helperClass.setName(reg_name);
//                        helperClass.setEmail(reg_email);
//                        helperClass.setPhoneNo(reg_phoneNo);
//                        helperClass.setPassword(reg_password);
//                        helperClass.setLoginAs(Loginas);
//
//                        reference.push().setValue(helperClass);
//                        Toast.makeText(SignUp.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });


        dropdown_login = (TextInputLayout) findViewById(R.id.dropdown_login);
        Loginas = (AutoCompleteTextView) findViewById(R.id.Loginas);

        arrayList_Loginas = new ArrayList<>();
        arrayList_Loginas.add("User");
        //arrayList_Loginas.add("Admin");
        arrayList_Loginas.add("Salon");
        arrayList_Loginas.add("Beautician");

        arrayAdapter_Loginas = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList_Loginas);
        Loginas.setAdapter(arrayAdapter_Loginas);
        ;

        Loginas.setThreshold(1);


        View Login = findViewById(R.id.login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this);
                    startActivity(intent, options.toBundle());

                }
            }
        });
    }

    private Boolean validateName() {
        String val = name.getEditText().getText().toString().trim();
        String NoWhiteSpaces = "\\A\\w{1,25}\\z";
        if (val.isEmpty()) {
            name.setError(" Field cannot be empty");
            return false;

        } else if (val.length() >= 25) {
            name.setError(" Full Name is too long");
            return false;
        } else if (!val.matches(NoWhiteSpaces)) {
            name.setError(" No white spaces are allowed");
            return false;

        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; //regex expression
        if (val.isEmpty()) {
            email.setError(" Field cannot be empty");
            return false;

        } else if (!val.matches(checkEmail)) {
            email.setError(" Invalid Email!");
            return false;

        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatephoneNo() {
        String val = phoneNo.getEditText().getText().toString().trim();
        String NoWhiteSpaces = "\\A\\w{1,25}\\z";
        if (val.isEmpty()) {
            phoneNo.setError(" Field cannot be empty");
            return false;

        } else if (!(val.length() >= 11 && val.length() <= 11)) {
            phoneNo.setError(" Invalid Phone Number!");
            return false;

        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
        String checkPassword = "^" +     //starting of the string
                "(?=.*[0-9])" +    //at least 1 digit
                "(?=.*[a-zA-Z])" +  //at letter
                "(?=.*[@#$%^&+=])" +  // at least one special character
                "(?=\\S+$)" +         //no white spaces
                ".{6,}" +             //at least 6 characters
                "$";
        if (val.isEmpty()) {
            password.setError(" Field cannot be empty");
            return false;

        } else if (!val.matches(checkPassword)) {
            password.setError(" Password must contain: \n • At least one digit \n • Minimum 4 alphabetic characters \n • No white spaces \n • At least one special character");
            return false;

        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

}
