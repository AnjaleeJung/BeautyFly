package com.android.fypmid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordReset extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    TextInputLayout resetEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        resetEmail = findViewById(R.id.reset_email);

        //progress dialog
        pd = new ProgressDialog(PasswordReset.this);
        pd.setTitle("Please Wait");
        pd.setMessage("Reseting");
        pd.setCancelable(false);
    }

    public void SendResetReq(View view) {
        String email = resetEmail.getEditText().getText().toString();
        if(email !=null){
            pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(PasswordReset.this, "Reset request sent to "+email, Toast.LENGTH_SHORT).show();
                    Toast.makeText(PasswordReset.this, "Check you email also check Spam folder.", Toast.LENGTH_SHORT).show();
                }
                pd.hide();
                }
        }
        );
        }else{
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }

    }
}