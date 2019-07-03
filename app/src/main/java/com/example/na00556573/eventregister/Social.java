package com.example.na00556573.eventregister;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Social extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

        FirebaseListAdapter<Posts> adapter;
        String[] key = new String[1];
        DrawerLayout draw;
        NavigationView navigationView;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_social);
            FloatingActionButton fab =
                    (FloatingActionButton)findViewById(R.id.fabPost);
            final FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();
            final String[] name= new String[1];

            DatabaseReference drhst= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Name");
            drhst.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name[0] =dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), NewPost.class);
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(), "Add New post", Toast.LENGTH_SHORT).show();
                }
            });
            ListView listOfMessages = (ListView)findViewById(R.id.list_of_posts);
            Query query = FirebaseDatabase.getInstance().getReference().child("Posts");

            FirebaseListOptions<Posts> options = new FirebaseListOptions.Builder<Posts>()
                    .setLayout(R.layout.posts)
                    .setQuery(query,Posts.class)
                    .build();
            adapter = new FirebaseListAdapter<Posts>(options) {        //put eventname
                @Override
                protected void populateView(final View v, final Posts model, final int position) {
                    // Get references to the views of message.xml
                    TextView commentText = (TextView) v.findViewById(R.id.post_text);
                    TextView commentUser = (TextView) v.findViewById(R.id.post_user);
                    TextView commentTime = (TextView) v.findViewById(R.id.post_time);
                    final Button Like = v.findViewById(R.id.post_like);
                    TextView commentLike = (TextView) v.findViewById(R.id.post_nolike);
                    final int[] numL = {Integer.parseInt(model.getPostLike())};
//                    FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postTime").equalTo(model.getPostTime()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for(DataSnapshot child: dataSnapshot.getChildren()){
//                                key[0] = child.getKey();
//                                FirebaseDatabase.getInstance().getReference("Posts").child(key[0]).child("postKey").setValue(key[0]);
//                                }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    final DatabaseReference drLike=FirebaseDatabase.getInstance().getReference("PostLike").child(user.getUid()).child(model.getPostKey());
                    drLike.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Like.setText("Unlike");
                            }
                            else{
                                Like.setText("Like");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference drNLike = FirebaseDatabase.getInstance().getReference("Posts").child(model.getPostKey());
                    Like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Like.getText().toString().matches("Like")){
                                Like.setText("Unlike");
                                numL[0]++;
                                drLike.setValue("Like");
                            }
                            else{
                                Like.setText("Like");
                                numL[0]--;
                                drLike.setValue(null);
                            }
                            drNLike.child("postLike").setValue(Integer.toString(numL[0]));
                        }
                    });

                    DatabaseReference imgRef=FirebaseDatabase.getInstance().getReference("Posts").child(model.getPostKey()).child("postImage");
                    imgRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ImageView image= v.findViewById(R.id.post_Image);
                            if(dataSnapshot.exists()){
                                Picasso.with(Social.this).load(dataSnapshot.getValue().toString()).into(image);
                                image.getLayoutParams().height = 600;
                                image.getLayoutParams().width = 600;
                                image.setScaleType(ImageView.ScaleType.FIT_XY);
                                image.setVisibility(View.VISIBLE);
                            }
                            else {
                                image.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // Set their text
                    commentText.setText(model.getPostText());
                    commentUser.setText(model.getPostUser());
                    commentLike.setText(model.getPostLike());

                    // Format the date before showing it
                    commentTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getPostTime()));

                    Button Comment = v.findViewById(R.id.post_comment);
                    Comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(), "Comment Clicked", Toast.LENGTH_SHORT).show();
                            Intent i= new Intent(getApplicationContext(),postCommentSection.class);
                            i.putExtra("postTitle", model.getPostKey());
                            startActivity(i);
                        }
                    });

                }

                @Override
                public Posts getItem(int position) {
                    return super.getItem(getCount()-position-1);
                }
            };

            listOfMessages.setAdapter(adapter);
        Toolbar tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        draw=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,draw,tool,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        draw.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.social);

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
                    Picasso.with(Social.this).load(dataSnapshot.child("Image").getValue().toString()).into(img);
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
                i.putExtra("Previous","Social");
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
                    startActivity(new Intent(Social.this, MainActivity.class));
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
        if (draw.isDrawerOpen(GravityCompat.START)) {
            draw.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.social);
    }
}
