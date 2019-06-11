package com.example.na00556573.eventregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Events extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String userID;
    private ListView lv;

    private FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),NeweventActivity.class);
                startActivity(i);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = database.getReference("Unverified").child(userID);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String age = dataSnapshot.child("Age").getValue().toString();
                    String name = dataSnapshot.child("Name").getValue().toString();
                    myRef.setValue(null);
                    myRef = database.getReference("Users");
                    myRef.child(user.getUid()).child("Email").setValue(email);
                    myRef.child(user.getUid()).child("Name").setValue(name);
                    myRef.child(user.getUid()).child("Age").setValue(age);
                } else Toast.makeText(getApplicationContext(), "Testing", Toast.LENGTH_SHORT);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lv= findViewById(R.id.EventList);

        Query query = FirebaseDatabase.getInstance().getReference().child("Events");
        FirebaseListOptions<EventValue> options = new FirebaseListOptions.Builder<EventValue>()
                .setLayout(R.layout.eventvalue)
                .setQuery(query,EventValue.class)
                .build();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                EventValue ev= (EventValue) model;
                String ed = ev.getDate();
                String dd = ed.substring(0,2);
                String mm = ed.substring(2,4);
                String yy = ed.substring(4,8);
                try {
                    java.util.Date c = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                    java.util.Date d = sdf.parse(dd + "/" + mm + "/" + yy);
                    if (d.compareTo(c)>=0){
                        //((ViewGroup)v.getParent()).removeView(v);
                        final TextView host=v.findViewById(R.id.host);
                        ImageView image= v.findViewById(R.id.image);
                        TextView Name = v.findViewById(R.id.name);
                        TextView Date = v.findViewById(R.id.date);
                        TextView Time = v.findViewById(R.id.time);
                        TextView Venue= v.findViewById(R.id.venue);

                        String tm = ev.getTime();
                        String td= tm.substring(0,2).compareTo("12")>=0 ? "PM":  "AM";
                        String hh = tm.substring(0,2).compareTo("12")>0 ? (tm.charAt(0)-'1')+""+(tm.charAt(1)-'2'):  tm.substring(0,2);
                        String mn = tm.substring(2,4);

                        DatabaseReference drhst=FirebaseDatabase.getInstance().getReference("Users").child(ev.getAddedBy()).child("Name");
                        drhst.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String hst=dataSnapshot.getValue(String.class);
                                host.setText(hst);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Name.setText(ev.getName());
                        Date.setText(dd + "/" + mm + "/" + yy);
                        Time.setText(hh+":"+mn+" " +td);
                        Venue.setText(ev.getVenue());

                        Picasso.with(Events.this).load(ev.getImage().toString()).into(image);
                        image.getLayoutParams().height = 600;

                        image.getLayoutParams().width = 600;

                        image.setScaleType(ImageView.ScaleType.FIT_XY);



                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        lv.setAdapter(adapter);
    }
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
    public void openDialog(){

    }
}
