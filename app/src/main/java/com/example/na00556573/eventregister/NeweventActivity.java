package com.example.na00556573.eventregister;

import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

import static android.widget.RelativeLayout.TRUE;

public class NeweventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String name, d, m, y, date, time, venue, url;
    EditText nEt, vEt;
    ImageView uploadPic;
    final int IMAGE_REQUEST=71;
    Uri imageLocationPath;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newevent);
        Button datebtn = findViewById(R.id.newDateBtn);
        uploadPic=findViewById(R.id.imageView);
        mStorageRef= FirebaseStorage.getInstance().getReference("Events");
        date="";
        time="";
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        Button timebtn = findViewById(R.id.newTimeBtn);
        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button addbtn = findViewById(R.id.newEventBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nEt= findViewById(R.id.newName);
                vEt= findViewById(R.id.newVenue);
                name=nEt.getText().toString();
                if(name.isEmpty()){
                    nEt.setError("Name cannot be empty");
                    nEt.requestFocus();
                    return ;
                }

                if(date.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Date cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(time.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Time cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                venue=vEt.getText().toString();
                if(venue.isEmpty()){
                    vEt.setError("Venue cannot be empty");
                    vEt.requestFocus();
                    return ;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRef = database.getReference("Events");
                upload();


            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        if(dayOfMonth<10){
            d = "0"+dayOfMonth;
        }
        else{
            d=""+dayOfMonth;
        }
        if(month<10){
            m="0"+(month+1);
        }
        else{
            m=""+(month+1);
        }
        y=""+year;
        date=d+m+y;
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
        TextView tv= findViewById(R.id.newDate);
        tv.setText(currentDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv= findViewById(R.id.newTime);
        String h, mn;
        if(hourOfDay<10){
            h="0"+hourOfDay;
        }
        else{
            h=""+hourOfDay;
        }
        if(minute<10){
            mn="0"+minute;
        }
        else{
            mn=""+minute;
        }
        time= h+mn;
        tv.setText("Hour: "+hourOfDay + " Minute: "+minute);

   }
   public void selectImage(View view){
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
                uploadPic.setImageBitmap(bImg);
            }
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void upload(){
        try{
            if(imageLocationPath!=null){
                //Toast.makeText(this,"Image Uploaded Successfully", Toast.LENGTH_SHORT);
                Toast.makeText(getApplicationContext(), "Uploading", Toast.LENGTH_LONG).show();
                final String nameOfImage=y+m+d+time+venue+"."+getExtension(imageLocationPath);
                final StorageReference imageRef=mStorageRef.child(nameOfImage);
                UploadTask ut=imageRef.putFile(imageLocationPath);
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
                        if(task.isSuccessful()){
                            url = task.getResult().toString();
                            FirebaseAuth mAuth=FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            myRef.child(y+m+d+time+venue).child("Name").setValue(name);
                            myRef.child(y+m+d+time+venue).child("Date").setValue(date);
                            myRef.child(y+m+d+time+venue).child("Time").setValue(time);
                            myRef.child(y+m+d+time+venue).child("Venue").setValue(venue);
                            myRef.child(y+m+d+time+venue).child("Image").setValue(url);
                            myRef.child(y+m+d+time+venue).child("AddedBY").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    nEt.getText().clear();
                                    name="";
                                    vEt.getText().clear();
                                    venue="";
                                    TextView et= findViewById(R.id.newDate);
                                    et.setText("Date");
                                    date="";
                                    et= findViewById(R.id.newTime);
                                    et.setText("Time");
                                    time="";
                                    uploadPic.setImageResource(android.R.color.transparent);
                                    Toast.makeText(getApplicationContext(),"Event added successfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Image not selected", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
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
