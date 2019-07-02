package com.example.na00556573.eventregister;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewPost extends AppCompatActivity {
    TextView postContent;
    Button postImgBtn, postSubmit;
    ImageView postImg;
    final int IMAGE_REQUEST=71;
    Uri imageLocationPath;
    DatabaseReference myRef;
    StorageReference mStorageRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        postContent = findViewById(R.id.newPostContent);
        postImgBtn = findViewById(R.id.newPostBtn);
        postSubmit = findViewById(R.id.newPostSubmit);
        postImg = findViewById(R.id.newPostImg);

        myRef = FirebaseDatabase.getInstance().getReference("Posts");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Posts");
        user = FirebaseAuth.getInstance().getCurrentUser();

        final String[] name = new String[1];
        DatabaseReference drhst=FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Name");
        drhst.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name[0] =dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = postContent.getText().toString();
                if (content.isEmpty()) {
                    postContent.setError("Post content cannot be empty");
                    postContent.requestFocus();
                    return;
                }
                final String key = myRef.push().getKey();
                if (imageLocationPath == null) {
                    myRef.child(key).setValue(new Posts(content, name[0], "0", key)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            postContent.setText("");
                            Toast.makeText(getApplicationContext(), "Post Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Uploading", Toast.LENGTH_LONG).show();
                    final String nameOfImage = key + "." + getExtension(imageLocationPath);
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
                            if (task.isSuccessful()) {
                                String url = task.getResult().toString();
                                myRef.child(key).setValue(new Posts(content, name[0], "0", key));
                                myRef.child(key).child("postImage").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        postContent.setText("");
                                        postImg.setImageResource(android.R.color.transparent);
                                        Toast.makeText(getApplicationContext(), "Post Added", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    public void selectPostImage(View view){
        try{
            Intent imgIntent=new Intent();
            imgIntent.setType("image/*");
            imgIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(imgIntent, IMAGE_REQUEST);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                postImg.setImageBitmap(bImg);
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
