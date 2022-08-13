package com.android.fypmid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;

public class AddService extends AppCompatActivity {
    private Button addService;
    private ImageView img;
    private TextInputLayout title,charges,waitingTime;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private String imgUrl;
    private RadioButton men,women;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        mAuth = FirebaseAuth.getInstance();

        img = findViewById(R.id.add_service_admin_img);
        title = findViewById(R.id.add_service_admin_title);
        charges = findViewById(R.id.add_service_admin_charges);
        waitingTime = findViewById(R.id.add_service_admin_waiting);
        addService = findViewById(R.id.add_service_admin_add);

        men = findViewById(R.id.men);
        women = findViewById(R.id.women);

        //loading
        //loading
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Processing...");
        //image upload
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddService.this)
                        .start();
            }
        });

        //Add Service button
        addService.setOnClickListener(view -> {
            String t = title.getEditText().getText().toString();
            String c = charges.getEditText().getText().toString();
            String w = waitingTime.getEditText().getText().toString();
            String cat = "Men";
                    if(men.isChecked()){
                        cat = "Men";
                    }else{
                        cat = "Women";
                    }

            addServiceToFirebase(t, c, w, imgUrl,cat);
        });

    }
    //image picker result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            img.setImageURI(uri); //method to load image
            dialog.show();
            uploadToFirebase(uri);
        }
    }
    //firebase storage for saving images
    private void uploadToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();  //represents a reference to firebase storage
        StorageReference sRef = reference.child("BusinessServiceImages/" + uri.getLastPathSegment());  //image extensions i.e jpg,png
        UploadTask task = sRef.putFile(uri); //upload files on the device
        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {  //Task completes
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss();
                sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        imgUrl = task.getResult().toString();
                    }
                });
            }
        });
    }

    // adding service data to realtime database
    private void addServiceToFirebase(String title, String charges, String waiting, String image,String cat) {
        dialog.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();
        //getting pushkey first  //create a child with that key and push the value
       DatabaseReference key = db.child("BusinessServices").child(uId).push();
       String pKey = key.getKey();
        BusinessService service = new BusinessService();
        service.setServiceTitle(title);
        service.setServiceCharges(charges);
        service.setWaitingTime(waiting);
        service.setuId(uId);
        service.setImage(image);
        service.setPushKey(pKey);
        service.setCategory(cat);
        db.child("BusinessServices").child(uId).child(pKey).setValue(service).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.hide();
                Toast.makeText(AddService.this, "Service Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

}