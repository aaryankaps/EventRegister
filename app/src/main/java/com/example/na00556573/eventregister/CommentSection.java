public class CommentSection extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fabComment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference("Comments").child() //put event name
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference("Comments").child()) {        //put eventname
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView commentText = (TextView)v.findViewById(R.id.comment_text);
                TextView commentUser = (TextView)v.findViewById(R.id.comment_user);
                TextView commentTime = (TextView)v.findViewById(R.id.comment_time);

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