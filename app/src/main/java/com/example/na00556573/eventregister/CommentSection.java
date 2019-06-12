package com.example.na00556573.eventregister;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Comment;

public class CommentSection extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentsection);
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fabComment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference("Comments") //put event name
                        .push()
                        .setValue(new Comments(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_comments);
        Query query = FirebaseDatabase.getInstance().getReference("Comments");

        FirebaseListOptions<Comments> options = new FirebaseListOptions.Builder<Comments>()
                .setLayout(R.layout.comments)
                .setQuery(query,Comments.class)
                .build();
        FirebaseListAdapter<Comments> adapter = new FirebaseListAdapter<Comments>(options) {        //put eventname
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
}