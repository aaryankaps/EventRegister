package com.example.na00556573.eventregister;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountActivity extends AppCompatActivity {
    EditText nameEt, phoneEt, ageEt;
    TextView emailTv;
    String name, phoneS, email, ageS, userID, url;
    Button updateBtn, imgBtn;
    ImageView imageView;
    final int IMAGE_REQUEST=71;
    Uri imageLocationPath;
    int imgTest =0;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        nameEt = findViewById(R.id.accNameVal);
        phoneEt = findViewById(R.id.accPhoneVal);
        ageEt = findViewById(R.id.accAgeVal);
        emailTv = findViewById(R.id.accEmailVal);
        updateBtn = findViewById(R.id.accSave);
        imgBtn = findViewById(R.id.accImgBtn);
        imageView = findViewById(R.id.accPic);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        myRef = database.getReference("Users").child(userID);
        mStorageRef= FirebaseStorage.getInstance().getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("Email").getValue().toString();
                ageS = dataSnapshot.child("Age").getValue().toString();
                name = dataSnapshot.child("Name").getValue().toString();
                if (dataSnapshot.child("Phone").exists()) {
                    phoneS = dataSnapshot.child("Phone").getValue().toString();
                    phoneEt.setText(phoneS);
                }
                if (dataSnapshot.child("Image").exists()){
                    imgTest = 1;
                }
                emailTv.setText(email);
                ageEt.setText(ageS);
                nameEt.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    name = nameEt.getText().toString();
                    if (name.isEmpty()) {
                        nameEt.setError("Name cannot be empty");
                        nameEt.requestFocus();
                        return;
                    }
                    phoneS = phoneEt.getText().toString();
                    if (phoneS.isEmpty()) {
                        phoneEt.setError("Phone Number cannot be empty");
                        phoneEt.requestFocus();
                        return;
                    } else if (!android.util.Patterns.PHONE.matcher(phoneS).matches() || phoneS.length() != 10) {
                        phoneEt.setError("Phone number is invalid");
                        phoneEt.requestFocus();
                        return;
                    }
                    ageS = ageEt.getText().toString();
                    if (ageS.isEmpty()) {
                        ageEt.setError("Age cannot be empty");
                        ageEt.requestFocus();
                        return;
                    }
                    final int age = Integer.parseInt(ageS);
                    if (age < 0 || age > 100) {
                        ageEt.setError("Age out of bound");
                        ageEt.requestFocus();
                        return;
                    }
                    if(imageLocationPath!=null) {
                        Toast.makeText(getApplicationContext(), "Uploading", Toast.LENGTH_LONG).show();
                        final String nameOfImage = name + phoneS + "." + getExtension(imageLocationPath);
                        final StorageReference imageRef = mStorageRef.child(nameOfImage);
                        UploadTask ut = imageRef.putFile(imageLocationPath);
                        ut.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()) {
                                    url = task.getResult().toString();
                                    myRef.child("Name").setValue(name);
                                    myRef.child("Age").setValue(ageS);
                                    myRef.child("Image").setValue(url);
                                    myRef.child("Phone").setValue(phoneS).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), Events.class);
                                            i.putExtra("Previous","Account");
                                            startActivity(i);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else if(imgTest == 1){
                        myRef.child("Name").setValue(name);
                        myRef.child("Age").setValue(ageS);
                        myRef.child("Phone").setValue(phoneS).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), Events.class);
                                i.putExtra("Previous","Account");
                                startActivity(i);
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Image not Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void selectProfileImage(View view){
        try{
            Intent imgIntent=new Intent();
            imgIntent.setType("image/*");
            imgIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(imgIntent, IMAGE_REQUEST);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(this,"Image Selected",Toast.LENGTH_SHORT);
                imageLocationPath = data.getData();
                Bitmap bImg = MediaStore.Images.Media.getBitmap(getContentResolver(), imageLocationPath);
                imageView.setImageBitmap(bImg);
            }
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private String getExtension(Uri uri){
        try{
            ContentResolver cr=getContentResolver();
            MimeTypeMap mtm=MimeTypeMap.getSingleton();

            return mtm.getExtensionFromMimeType(cr.getType(uri));
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
