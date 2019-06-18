package com.example.na00556573.eventregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private Button Login, Register;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };
        Login = findViewById(R.id.button2);
        Login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(user!=null){
                    Intent i = new Intent(getApplicationContext(),Events.class);
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(),"User logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(),"User not logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            }
        });
        Register = (findViewById(R.id.button3));
        Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(user!=null){
                    Intent i = new Intent(getApplicationContext(),Events.class);
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(),"User logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(),"User not logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
                }
            }
        });
    }
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop(){
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


