package com.example.na00556573.eventregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private Button Login, Register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Login = findViewById(R.id.button2);
        Login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("user", "onClick: Login Called");
                Intent i= new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
        Register = (findViewById(R.id.button3));
        Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("user", "onClick: Register Called");
                Intent i= new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });


    }

}


