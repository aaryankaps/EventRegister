package com.example.na00556573.eventregister;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Social extends AppCompatActivity {

        FirebaseListAdapter<Posts> adapter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_social);
            FloatingActionButton fab =
                    (FloatingActionButton)findViewById(R.id.fabPost);
            final FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();
            final String[] name = new String[1];

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
                    Toast.makeText(getApplicationContext(), "New post added", Toast.LENGTH_SHORT).show();
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
                protected void populateView(View v, Posts model, int position) {
                    // Get references to the views of message.xml
                    TextView commentText = (TextView) v.findViewById(R.id.post_text);
                    TextView commentUser = (TextView) v.findViewById(R.id.post_user);
                    TextView commentTime = (TextView) v.findViewById(R.id.post_time);
                    final Button Like = v.findViewById(R.id.post_like);
                    TextView commentLike = (TextView) v.findViewById(R.id.post_nolike);
                    final int[] numL = {Integer.parseInt(model.getPostLike())};

                    final DatabaseReference drLike=FirebaseDatabase.getInstance().getReference("PostLike").child(user.getUid()).child("LhUvDSPDNSFNwLP8Iei");
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

                    final DatabaseReference drNLike = FirebaseDatabase.getInstance().getReference("Posts").child("-LhUvDSPDNSFNwLP8Iei");
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
                    // Set their text
                    commentText.setText(model.getPostText());
                    commentUser.setText(model.getPostUser());
                    commentLike.setText(model.getPostLike());

                    // Format the date before showing it
                    commentTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getPostTime()));

                }
            };

            listOfMessages.setAdapter(adapter);
        }
        protected void onStart(){
            super.onStart();
            adapter.startListening();
        }
        protected void onStop(){
            super.onStop();
            adapter.stopListening();
        }
    }
