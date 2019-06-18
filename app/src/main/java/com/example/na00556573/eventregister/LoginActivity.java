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

import static java.sql.Types.NULL;

public class LoginActivity extends AppCompatActivity {
    private Button logBut;
    private EditText e1, e2;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logBut= findViewById(R.id.email_sign_in_button);
        e1= findViewById(R.id.email);
        e2= findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();
        logBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=e1.getText().toString();
                if(email.isEmpty()){
                    e1.setError("Email cannot be empty");
                    e1.requestFocus();
                    e2.getText().clear();
                    return ;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    e1.setError("Email is invalid");
                    e1.requestFocus();
                    e2.getText().clear();
                    return ;
                }
                String password=e2.getText().toString();
                if(password.isEmpty()){
                    e2.setError("Password cannot be empty");
                    e2.requestFocus();
                    e2.getText().clear();
                    return ;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                //Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(LoginActivity.this, Events.class);
                                i.putExtra("Previous","Login");
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Email Not Verified", Toast.LENGTH_LONG).show();
                                e2.getText().clear();
                            }

                        }else {
                            Toast.makeText(getApplicationContext(), "Email ID or password is incorrect", Toast.LENGTH_SHORT).show();
                            e2.getText().clear();
                        }
                    }
                });
            }
        });
    }
}

