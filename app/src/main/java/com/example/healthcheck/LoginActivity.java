package com.example.healthcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    DatabaseReference adminreference = FirebaseDatabase.getInstance().getReference().child("Users");
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button back = findViewById(R.id.back);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , StartActivity.class));
            }});

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                loginUser(txt_email , txt_password);
            }
        });
    }


    private void loginUser(final String email, String password) {
        Toast.makeText(LoginActivity.this, "Please Wait, Login Authentication is in progress!", Toast.LENGTH_LONG).show();
        auth.signInWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser emailuser = FirebaseAuth.getInstance().getCurrentUser();
                final String uid = emailuser.getUid();

                adminreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String notadmin = String.valueOf(dataSnapshot.child("Patients").child(uid).exists());
                        String anadmin = String.valueOf(dataSnapshot.child("Doctors").child(uid).exists());
                        if(anadmin.equals("true"))
                        {
                            Toast.makeText(LoginActivity.this, "Doctor Login Successful!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this , DoctorHomepageCurrentActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else if(notadmin.equals("true"))
                        {
                            Toast.makeText(LoginActivity.this, "Patient Login Successful!", Toast.LENGTH_LONG).show();
                            Intent i2 = new Intent(LoginActivity.this , MainActivity.class);
                            startActivity(i2);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "User was Deleted!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Login Cancelled,  Please try again!", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        auth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
            }
        });
        auth.signInWithEmailAndPassword(email, password).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(LoginActivity.this, "Login Canceled!", Toast.LENGTH_LONG).show();
            }
        });
    }
}

      /*
        emailreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    String email = String.valueOf(snapshot1.child("email").getValue());
                    if (email.equals(getemail))         //Might cause delay if number of users +5000
                    {
                        uid = String.valueOf(snapshot1.child("id").getValue());
                        hiddenid.setText(uid);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/