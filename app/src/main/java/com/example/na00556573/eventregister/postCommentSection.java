package com.example.na00556573.eventregister;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class postCommentSection extends AppCompatActivity {
    FirebaseListAdapter<Comments> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment_section);
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fabComment);
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
                EditText input = (EditText)findViewById(R.id.input);
                if(input.getText().toString().isEmpty()){
                    return;
                }
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference("postComments").child(getIntent().getStringExtra("postTitle")) //put event name
                        .push()
                        .setValue(new Comments(input.getText().toString(), name[0]));

                // Clear the input
                input.setText("");
            }
        });
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_comments);
        Query query = FirebaseDatabase.getInstance().getReference().child("postComments").child(getIntent().getStringExtra("postTitle"));

        FirebaseListOptions<Comments> options = new FirebaseListOptions.Builder<Comments>()
                .setLayout(R.layout.comments)
                .setQuery(query,Comments.class)
                .build();
        adapter = new FirebaseListAdapter<Comments>(options) {        //put eventname
            @Override
            protected void populateView(View v, Comments model, int position) {
                // Get references to the views of message.xml
                TextView commentText = (TextView) v.findViewById(R.id.comment_text);
                TextView commentUser = (TextView) v.findViewById(R.id.comment_user);
                TextView commentTime = (TextView) v.findViewById(R.id.comment_time);

                // Set their text
                commentText.setText(model.getCommentText());
                commentUser.setText(model.getCommentUser());

                // Format the date before showing it
                commentTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getCommentTime()));
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
