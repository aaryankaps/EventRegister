package com.example.na00556573.eventregister;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    DrawerLayout draw;

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
        Toolbar tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        draw=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,draw,tool,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        draw.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.account);

        final TextView nm=navigationView.getHeaderView(0).findViewById(R.id.userName);
        final TextView em=navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        final ImageView img=navigationView.getHeaderView(0).findViewById(R.id.userImg);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2=database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nm.setText(dataSnapshot.child("Name").getValue().toString());
                em.setText(dataSnapshot.child("Email").getValue().toString());
                if(dataSnapshot.child("Image").exists()) {
                    Picasso.with(AccountActivity.this).load(dataSnapshot.child("Image").getValue().toString()).into(img);
                    img.getLayoutParams().height = 200;
                    img.getLayoutParams().width = 200;
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.home:
                i=new Intent(getApplicationContext(),Events.class);
                i.putExtra("Previous","Accounts");
                startActivity(i);
                break;
            case R.id.account:
                i=new Intent(getApplicationContext(),AccountActivity.class);
                startActivity(i);
                break;
            case R.id.post:
                i=new Intent(getApplicationContext(),NeweventActivity.class);
                startActivity(i);
                break;
            case R.id.sign_out:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Sign out Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.social:
                i=new Intent(getApplicationContext(),Social.class);
                startActivity(i);
               // Toast.makeText(this, "View our Social Media", Toast.LENGTH_SHORT).show();
                break;
        }

        draw.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (draw.isDrawerOpen(GravityCompat.START)) {
            draw.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
