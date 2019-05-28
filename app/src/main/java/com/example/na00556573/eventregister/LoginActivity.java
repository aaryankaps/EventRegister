package com.example.na00556573.eventregister;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button logBut;
    private EditText e1, e2;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logBut=(Button)findViewById(R.id.email_sign_in_button);
        e1=(EditText) findViewById(R.id.email);
        e2=(EditText) findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();
        logBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=e1.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Email cannot be empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(),"Email is invalid", Toast.LENGTH_LONG).show();
                    return ;
                }
                String password=e2.getText().toString();
                if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Password cannot be empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this,Events.class);
                            startActivity(i);

                        }else {
                            Toast.makeText(getApplicationContext(), "Email ID or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

