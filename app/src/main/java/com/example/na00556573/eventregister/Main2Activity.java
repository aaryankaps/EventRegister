package com.example.na00556573.eventregister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button eventBtn = findViewById(R.id.eventBtn);
        Button socialBtn = findViewById(R.id.socialBtn);
        socialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, Social.class);
                startActivity(i);
            }
        });
        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, Events.class);
                i.putExtra("Previous", "Main2");
                startActivity(i);
            }
        });
    }
}
