package com.example.healthcheck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoctorHomepageActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    CustomExpandableListViewAdapter customExpandableListViewAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Button currentbtn, logoutbtn;
    TextView criticaltxt;
    DatabaseReference patienthistory = FirebaseDatabase.getInstance().getReference().child("Doctor Checking").child("History");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorhomepage);

        currentbtn = findViewById(R.id.dcurrentbtn);
        logoutbtn = findViewById(R.id.dlogout);
        criticaltxt = findViewById(R.id.criticaltxt);
        expandableListView = findViewById(R.id.expandableListView);
        criticaltxt.setText("");
        SetStandardGroups();
        customExpandableListViewAdapter = new CustomExpandableListViewAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(customExpandableListViewAdapter);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(DoctorHomepageActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DoctorHomepageActivity.this , StartActivity.class));
                finish();
            }
        });

        currentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DoctorHomepageActivity.this , DoctorHomepageCurrentActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void SetStandardGroups() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        patienthistory.addChildEventListener(new ChildEventListener() {
                    int counter = 0;
                    List<String> childItem;

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        listDataHeader.add(dataSnapshot.getKey());
                        childItem = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String childNames = (String) ds.getKey() + ":  "+ ds.getValue();
                            childItem.add(childNames);
                        }

                        listDataChild.put(listDataHeader.get(counter), childItem);
                        counter++;

                        customExpandableListViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}