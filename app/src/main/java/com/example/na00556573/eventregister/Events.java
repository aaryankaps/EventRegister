package com.example.na00556573.eventregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class Events extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout draw;
    private FirebaseDatabase database;
    private DatabaseReference myRef,myRef2;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private ListView lv;
    private FirebaseListAdapter adapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
        user = mAuth.getCurrentUser();
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
        Toolbar tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        draw=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,draw,tool,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        draw.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.home);

        final TextView nm=navigationView.getHeaderView(0).findViewById(R.id.userName);
        final TextView em=navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        final ImageView img=navigationView.getHeaderView(0).findViewById(R.id.userImg);
        myRef2=database.getReference("Users").child(userID);
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nm.setText(dataSnapshot.child("Name").getValue().toString());
                em.setText(dataSnapshot.child("Email").getValue().toString());
                if(dataSnapshot.child("Image").exists()) {
                    Picasso.with(Events.this).load(dataSnapshot.child("Image").getValue().toString()).into(img);
                    img.getLayoutParams().height = 200;
                    img.getLayoutParams().width = 200;
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lv= findViewById(R.id.EventList);

        Query query = FirebaseDatabase.getInstance().getReference().child("Events");
        //.limitToLast(1)
        FirebaseListOptions<EventValue> options = new FirebaseListOptions.Builder<EventValue>()
                .setLayout(R.layout.eventvalue)
                .setQuery(query,EventValue.class)
                .build();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                final EventValue ev= (EventValue) model;
                String ed = ev.getDate();
                final String dd = ed.substring(0,2);
                final String mm = ed.substring(2,4);
                final String yy = ed.substring(4,8);
                try {
                    final TextView host=v.findViewById(R.id.host);
                    ImageView image= v.findViewById(R.id.image);
                    TextView Name = v.findViewById(R.id.name);
                    TextView Date = v.findViewById(R.id.date);
                    TextView Time = v.findViewById(R.id.time);
                    TextView Venue= v.findViewById(R.id.venue);
                    final Button Like = v.findViewById(R.id.like);
                    TextView NoLikes= v.findViewById(R.id.nolike);
                    Button Comment = v.findViewById(R.id.comment);
                    ConstraintLayout cl =v.findViewById(R.id.event_constraint);

                    java.util.Date c = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                    java.util.Date d = sdf.parse(dd + "/" + mm + "/" + yy);
                    if (d.compareTo(c)>=0){
                        //((ViewGroup)v.getParent()).removeView(v);
                        cl.setVisibility(View.VISIBLE);
                        final int[] numL = {Integer.parseInt(ev.getLikes())};

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
                        final DatabaseReference drLike=FirebaseDatabase.getInstance().getReference("Likes").child(userID).child(yy+mm+dd+ev.getTime()+ev.getVenue());
                        drLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Like.setText("Not Interested");
                                }
                                else{
                                    Like.setText("Interested");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        final DatabaseReference drNLike = FirebaseDatabase.getInstance().getReference("Events").child(yy+mm+dd+ev.getTime()+ev.getVenue());
                        Like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(Like.getText().toString().matches("Interested")){
                                    Like.setText("Not Interested");
                                    numL[0]++;
                                    drLike.setValue("Interested");
                                }
                                else{
                                    Like.setText("Interested");
                                    numL[0]--;
                                    drLike.setValue(null);
                                }
                                drNLike.child("Likes").setValue(Integer.toString(numL[0]));
                            }
                        });
                        Comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(getApplicationContext(), "Comment Clicked", Toast.LENGTH_SHORT).show();
                                Intent i= new Intent(getApplicationContext(),CommentSec.class);
                                i.putExtra("eventTitle", yy+mm+dd+ev.getTime()+ev.getVenue());
                                startActivity(i);
                            }
                        });
                        Name.setText(ev.getName());
                        Date.setText(dd + "/" + mm + "/" + yy);
                        Time.setText(hh+":"+mn+" " +td);
                        Venue.setText(ev.getVenue());
                        NoLikes.setText(Integer.toString(numL[0]));

                        Picasso.with(Events.this).load(ev.getImage().toString()).into(image);
                        image.getLayoutParams().height = 600;
                        image.getLayoutParams().width = 600;
                        image.setScaleType(ImageView.ScaleType.FIT_XY);



                    }
                    else {
                        v.setVisibility(View.GONE);
                        host.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                        Name.setVisibility(View.GONE);
                        Date.setVisibility(View.GONE);
                        Time.setVisibility(View.GONE);
                        Venue.setVisibility(View.GONE);
                        Like.setVisibility(View.GONE);
                        NoLikes.setVisibility(View.GONE);
                        Comment.setVisibility(View.GONE);
                        cl.setVisibility(View.GONE);
                        ((ViewGroup)v.getParent()).removeView(v);
                        //v.setLayoutParams(new ListView.LayoutParams(0, 0));
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.home:
                i=new Intent(getApplicationContext(),Events.class);
                i.putExtra("Previous","Home");
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
                mAuth.signOut();
                if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                    startActivity(new Intent(Events.this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Sign out Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.social:
                i=new Intent(getApplicationContext(),Social.class);
                startActivity(i);
                //Toast.makeText(this, "View our Social Media", Toast.LENGTH_SHORT).show();
                break;
            case R.id.postSocial:
                i=new Intent(getApplicationContext(),NewPost.class);
                startActivity(i);
                break;
        }

        draw.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if(draw.isDrawerOpen(GravityCompat.START)){
            draw.closeDrawer(GravityCompat.START);
        }
        else if(getIntent().getStringExtra("Previous").equals("Main") || getIntent().getStringExtra("Previous").equals("Login")){
            finishAffinity();
            System.exit(0);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.home);
    }
}
