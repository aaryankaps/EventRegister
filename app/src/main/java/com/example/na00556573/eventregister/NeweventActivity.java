package com.example.na00556573.eventregister;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

import static android.widget.RelativeLayout.TRUE;

public class NeweventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String name, date, time, venue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newevent);
        Button datebtn = (Button)findViewById(R.id.newDateBtn);
        date="";
        time="";
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        Button timebtn = (Button)findViewById(R.id.newTimeBtn);
        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button addbtn = (Button)findViewById(R.id.newEventBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText nEt=(EditText)findViewById(R.id.newName);
                final EditText vEt=(EditText)findViewById(R.id.newVenue);
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
                DatabaseReference myRef = database.getReference("Events");
                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                myRef.child(date+time+venue).child("Name").setValue(name);
                myRef.child(date+time+venue).child("Date").setValue(date);
                myRef.child(date+time+venue).child("Time").setValue(time);
                myRef.child(date+time+venue).child("Venue").setValue(venue);
                myRef.child(date+time+venue).child("AddedBY").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        nEt.getText().clear();
                        vEt.getText().clear();
                        TextView et=(TextView)findViewById(R.id.newDate);
                        et.setText("Date");
                        et=(TextView)findViewById(R.id.newTime);
                        et.setText("Time");
                        Toast.makeText(getApplicationContext(),"Event added successfully",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String d, m;
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
        date=d+m+year;
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
        TextView tv= (TextView)findViewById(R.id.newDate);
        tv.setText(currentDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv= (TextView)findViewById(R.id.newTime);
        String h, m;
        if(hourOfDay<10){
            h="0"+hourOfDay;
        }
        else{
            h=""+hourOfDay;
        }
        if(minute<10){
            m="0"+minute;
        }
        else{
            m=""+minute;
        }
        time= h+m;
        tv.setText("Hour: "+hourOfDay + " Minute: "+minute);

   }
}
