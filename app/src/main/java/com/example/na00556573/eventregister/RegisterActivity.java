package com.example.na00556573.eventregister;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button but;
    private EditText e1, e2, e3, e4;
    private FirebaseAuth mAuth;
    private String name, email, ageS, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);
        but= findViewById(R.id.button);
        e1= findViewById(R.id.editText);
        e2= findViewById(R.id.editText2);
        e3= findViewById(R.id.editText3);
        e4= findViewById(R.id.editText4);
        mAuth = FirebaseAuth.getInstance();
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = e1.getText().toString();
                if(name.isEmpty()){
                    //Toast.makeText(getApplicationContext(),"Name cannot be empty", Toast.LENGTH_LONG).show();
                    e1.setError("Name cannot be empty");
                    e1.requestFocus();
                    return ;
                }
                email = e2.getText().toString();
                if(email.isEmpty()){
                    e2.setError("Email cannot be empty");
                    e2.requestFocus();
                    return ;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    e2.setError("Email is invalid");
                    e2.requestFocus();
                    return ;
                }
                password = e3.getText().toString();
                if(password.isEmpty()){
                    e3.setError("Password cannot be empty");
                    e3.requestFocus();
                    return ;
                }
                if (password.length()<6){
                    e3.setError("Password should be at least 6 digits");
                    e3.requestFocus();
                    return ;
                }
                ageS = e4.getText().toString();
                if (ageS.isEmpty()) {
                    e4.setError("Age cannot be empty");
                    e4.requestFocus();
                    return;
                }
                final int age = Integer.parseInt(ageS);
                if (age >= 0 && age <= 100) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(getApplicationContext(),"User Added Successfully", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                       // Log.d("Auth", "onComplete: " + user.getUid());
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(RegisterActivity.this,"Verification Email Sent", Toast.LENGTH_SHORT );
                                            }
                                        });
                                        name="";
                                        e1.getText().clear();
                                        email="";
                                        e2.getText().clear();
                                        password="";
                                        e3.getText().clear();
                                        ageS="";
                                        e4.getText().clear();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("Unverified");
                                        myRef.child(user.getUid()).child("Email").setValue(email);
                                        myRef.child(user.getUid()).child("Name").setValue(name);
                                        myRef.child(user.getUid()).child("Age").setValue(age);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Log.w("QWJ", "createUserWithEmail:failure", task.getException());
                                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                            Toast.makeText(getApplicationContext(), "Email already registered.", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else{
                    e4.setError("Age out of bound");
                    e4.requestFocus();
                }
            }
        });
    }
}
