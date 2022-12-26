package com.example.healthcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText name,email,password,age,phone;
    private CheckBox isdoctor;
    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        age = findViewById(R.id.age);
        phone = findViewById(R.id.phone);
        Button register = findViewById(R.id.register);
        Button back = findViewById(R.id.back);
        isdoctor = findViewById(R.id.checkBox);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this , StartActivity.class));
            }});

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_name = name.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_age = age.getText().toString();
                String txt_phone = phone.getText().toString();

                if (TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)
                        || TextUtils.isEmpty(txt_age) || TextUtils.isEmpty(txt_phone)){
                    Toast.makeText(RegisterActivity.this, "Please provide all the requested information!", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be 6 or more characters long!", Toast.LENGTH_SHORT).show();
                } else if (txt_name.length() < 10) {
                    Toast.makeText(RegisterActivity.this, "Name is too short, Please Enter your Full Name!", Toast.LENGTH_SHORT).show();
                } else if (txt_name.contains(".") || txt_name.contains("$") || txt_name.contains("#") || txt_name.contains("[")
                        || txt_name.contains("]") || txt_name.contains("/")) {
                    Toast.makeText(RegisterActivity.this, "Invalid Characters in Name  . $ # [ ] /", Toast.LENGTH_SHORT).show();
                }
                    else{
                    Toast.makeText(RegisterActivity.this, "Please Wait, registration in progress!", Toast.LENGTH_LONG).show();
                    registerUser(txt_email , txt_password, txt_name, txt_age, txt_phone);     //Authentication Method
                }
            }
        });
    }

    private void registerUser(String txt_email, String txt_password, String txt_name, String txt_age, String txt_phone) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(RegisterActivity.this , new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User Registration Successful!", Toast.LENGTH_SHORT).show();
                    FirebaseUser thisuser = FirebaseAuth.getInstance().getCurrentUser();

                    String txt_name = name.getText().toString();
                    String txt_email = email.getText().toString();
                    String txt_age = age.getText().toString();
                    String txt_phone = phone.getText().toString();
                    String txt_id = thisuser.getUid();;

                    User patient = new User(txt_name,txt_email,txt_id,txt_age,txt_phone);
                    if(isdoctor.isChecked())
                    {
                        DatabaseReference DoctorReference = database.getReference().child("Users").child("Doctors").child(txt_id);
                        DoctorReference.setValue(patient);
                    }
                    else {
                        DatabaseReference PatientReference = database.getReference().child("Users").child("Patients").child(txt_id);
                        PatientReference.setValue(patient);
                    }
                    startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}