package com.example.healthcheck;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoctorHomepageCurrentActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    CustomExpandableListViewAdapter customExpandableListViewAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Button historybtn, logoutbtn;
    TextView criticaltxt;

    DatabaseReference patientcurrent = FirebaseDatabase.getInstance().getReference().child("Doctor Checking").child("Current");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorhomepage);

        historybtn = findViewById(R.id.dhistorybtn);
        logoutbtn = findViewById(R.id.dlogout);
        criticaltxt = findViewById(R.id.criticaltxt);
        expandableListView = findViewById(R.id.expandableListView);

        createNotificationChannel();
        SetStandardGroups();
        customExpandableListViewAdapter = new CustomExpandableListViewAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(customExpandableListViewAdapter);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(DoctorHomepageCurrentActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DoctorHomepageCurrentActivity.this , StartActivity.class));
                finish();
            }
        });

        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DoctorHomepageCurrentActivity.this , DoctorHomepageActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void SetStandardGroups() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        patientcurrent.addChildEventListener(new ChildEventListener() {
                    int counter = 0;
                    List<String> childItem;
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        listDataHeader.add(dataSnapshot.getKey());
                        childItem = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String childNames = (String) ds.getKey() + ":  "+ ds.getValue();

                            int value = Integer.parseInt(ds.getValue().toString().substring(1, ds.getValue().toString().length() -1));
                            if(ds.getKey().contains("HeartbeatsperMinute") &&  value > 150 || value < 40)
                            {
                                criticaltxt.append("\n" + listDataHeader.get(counter) + "  \"Heartrate\" is critical < 40 or > 150 beats/min");

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(listDataHeader.get(counter) + "'s Glucose is critical [" + value +"]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(value, builder.build());
                            }
                            if(ds.getKey().contains("Cholesterol") &&  value > 240)
                            {
                                criticaltxt.append("\n" + listDataHeader.get(counter) + "  \"Cholesterol\" is critical > 240 mg/dl");

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(listDataHeader.get(counter) + "'s Glucose is critical [" + value +"]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(value, builder.build());
                            }
                            if(ds.getKey().contains("Glucose") &&  value > 200)
                            {
                                criticaltxt.append("\n" + listDataHeader.get(counter) + "  \"Glucose\" is critical > 200 mg/dl");

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(listDataHeader.get(counter) + "'s Glucose is critical [" + value +"]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(value, builder.build());
                            }
                            if(ds.getKey().contains("Glucose") &&  value < 60)
                            {
                                criticaltxt.append("\n" + listDataHeader.get(counter) + "  \"Glucose\" is critical < 60 mg/dl");

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(listDataHeader.get(counter) + "'s Glucose is critical [" + value +"]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(value, builder.build());
                            }

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Health Check";
            String description = "Critical Condition Notification!";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("idnotify", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}