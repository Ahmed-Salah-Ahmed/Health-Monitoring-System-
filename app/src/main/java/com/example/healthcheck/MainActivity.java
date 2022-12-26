package com.example.healthcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView hrlist, chollist, glclist;
    FirebaseUser emailuser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference rootnode = FirebaseDatabase.getInstance().getReference();
    int sn=0, n=0, n4=0, n01 = 0, n02 = 0, n03 = 0;
    Random rand = new Random();
    int randomstart = rand.nextInt((3700 - 100) + 1) + 100;


    String uid = emailuser.getUid();

    DatabaseReference usernamereference = rootnode.child("Users").child("Patients").child(uid).child("name");
    DatabaseReference historylist = rootnode.child("DataHistory").child("History").child(uid);
    DatabaseReference currenthistorylist = rootnode.child("DataHistory").child("Current").child(uid);
    DatabaseReference doctorreadlist = rootnode.child("Doctor Checking").child("History");
    DatabaseReference currentdoctorreadlist = rootnode.child("Doctor Checking").child("Current");

    String recipientList = "healthfirebaseproject@gmail.com";
    String[] recipients = recipientList.split(",");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Toast.makeText(MainActivity.this, "Login Token Expired!  Please Login again", Toast.LENGTH_LONG).show();
            Intent relog = new Intent(MainActivity.this , StartActivity.class);
            startActivity(relog);
            finish();
        }

        Button logoutbtn, historybtn, currentbtn, refreshbtn;


        logoutbtn = findViewById(R.id.plogout);
        currentbtn = findViewById(R.id.pcurrentbtn);
        historybtn = findViewById(R.id.prhistorybtn);
        refreshbtn = findViewById(R.id.prefresh);

        hrlist = findViewById(R.id.listhr);
        chollist = findViewById(R.id.listchol);
        glclist = findViewById(R.id.listglc);

        createNotificationChannel();

        datacurrent();

        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dataentry();

            }
        });

        currentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datacurrent();
            }
        });



        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sn = sn+1;
                dataentry();
            }
        });


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this , StartActivity.class));
                finish();
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

private void dataentry() {

    int s2 = randomstart - sn;
    String startinglocation = Integer.toString(s2);

    final Query hrreferencehistory = rootnode.child("DataExcel").child("HeartbeatsperMinute")
            .orderByKey().startAt(startinglocation).limitToFirst(51+sn);
    final Query cholreferencehistory = rootnode.child("DataExcel").child("Cholesterol")
            .orderByKey().startAt(startinglocation).limitToFirst(51+sn);
    final Query glcreferencehistory = rootnode.child("DataExcel").child("Glucose")
            .orderByKey().startAt(startinglocation).limitToFirst(51+sn);


    final ArrayList<String> hrlistarray = new ArrayList<>();
    final ArrayAdapter hradapter = new ArrayAdapter<String>(this, R.layout.list_item, hrlistarray);
    hrlist.setAdapter(hradapter);

    final ArrayList<String> chollistarray = new ArrayList<>();
    final ArrayAdapter choladapter = new ArrayAdapter<String>(this, R.layout.list_item, chollistarray);
    chollist.setAdapter(choladapter);

    final ArrayList<String> glclistarray = new ArrayList<>();
    final ArrayAdapter glcadapter = new ArrayAdapter<String>(this, R.layout.list_item, glclistarray);
    glclist.setAdapter(glcadapter);

    hrlist.setBackgroundColor(Color.WHITE);
    chollist.setBackgroundColor(Color.WHITE);
    glclist.setBackgroundColor(Color.WHITE);

    usernamereference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String username = dataSnapshot.getValue(String.class);
            if (n4==0) {
                doctorreadlist = doctorreadlist.child(username);
                currentdoctorreadlist = currentdoctorreadlist.child(username);
                n4++;
            }

            hrreferencehistory.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hrlistarray.clear();
                    int n1=0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        hrlistarray.add((String) snapshot.getValue());
                        doctorreadlist.child("HeartbeatsperMinute").setValue(hrlistarray);
                        historylist.child("HeartbeatsperMinute").setValue(hrlistarray);
                        if(n1==0)
                        {
                            currentdoctorreadlist.child("HeartbeatsperMinute").setValue(hrlistarray);
                            currenthistorylist.child("HeartbeatsperMinute").setValue(hrlistarray);
                            n1++;
                        }
                    }
                    hradapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


            cholreferencehistory.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chollistarray.clear();
                    int n2 = 0;
                    for (DataSnapshot cholsnapshot : dataSnapshot.getChildren()){
                        chollistarray.add((String) cholsnapshot.getValue());
                        doctorreadlist.child("Cholesterol").setValue(chollistarray);
                        historylist.child("Cholesterol").setValue(chollistarray);
                        if(n2==0)
                        {
                            currentdoctorreadlist.child("Cholesterol").setValue(chollistarray);
                            currenthistorylist.child("Cholesterol").setValue(chollistarray);
                            n2++;
                        }
                    }
                    choladapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            glcreferencehistory.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    glclistarray.clear();
                    int n3=0;
                    for (DataSnapshot glcsnapshot : dataSnapshot.getChildren()){
                        glclistarray.add((String) glcsnapshot.getValue());
                        doctorreadlist.child("Glucose").setValue(glclistarray);
                        historylist.child("Glucose").setValue(glclistarray);
                        if(n3==0)
                        {
                            currentdoctorreadlist.child("Glucose").setValue(glclistarray);
                            currenthistorylist.child("Glucose").setValue(glclistarray);
                            n3++;
                        }
                    }
                    glcadapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}

    private void datacurrent() {

        int s2 = randomstart - sn;
        String startinglocation = Integer.toString(s2);

        final Query hrreferencecurrent = rootnode.child("DataExcel").child("HeartbeatsperMinute")
                .orderByKey().startAt(startinglocation).limitToFirst(1);
        final Query cholreferencecurrent = rootnode.child("DataExcel").child("Cholesterol")
                .orderByKey().startAt(startinglocation).limitToFirst(1);
        final Query glcreferencecurrent = rootnode.child("DataExcel").child("Glucose")
                .orderByKey().startAt(startinglocation).limitToFirst(1);

        //sn= sn+1;

        final ArrayList<String> hrlistarray = new ArrayList<>();
        final ArrayAdapter hradapter = new ArrayAdapter<String>(this, R.layout.list_item, hrlistarray);
        hrlist.setAdapter(hradapter);

        final ArrayList<String> chollistarray = new ArrayList<>();
        final ArrayAdapter choladapter = new ArrayAdapter<String>(this, R.layout.list_item, chollistarray);
        chollist.setAdapter(choladapter);

        final ArrayList<String> glclistarray = new ArrayList<>();
        final ArrayAdapter glcadapter = new ArrayAdapter<String>(this, R.layout.list_item, glclistarray);
        glclist.setAdapter(glcadapter);


        usernamereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String username = dataSnapshot.getValue(String.class);


                hrreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hrlistarray.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            hrlistarray.add((String) snapshot.getValue());

                            int valueh = Integer.parseInt(hrlistarray.toString().substring(1, hrlistarray.toString().length() - 1));
                            if (valueh >= 60 && valueh <= 100)
                                hrlist.setBackgroundColor(Color.GREEN);
                            else if (valueh > 100 && valueh <= 150)
                                hrlist.setBackgroundColor(Color.YELLOW);
                            else if (valueh > 40 && valueh < 60)
                                hrlist.setBackgroundColor(Color.YELLOW);
                            else if (valueh > 150 || valueh < 40) {
                                hrlist.setBackgroundColor(Color.RED);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(username + "'s Heart Rate is critical [" + valueh + "]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(1, builder.build());

                                if(n01 == 0) {
                                    String subject = username + "'s Heart Rate is critical [" + valueh + "]";
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    intent.setType("message/rfc822");
                                    startActivity(Intent.createChooser(intent, "Choose an email client"));
                                    n01++;
                                }
                            }

                        }
                        hradapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                cholreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chollistarray.clear();
                        for (DataSnapshot cholsnapshot : dataSnapshot.getChildren()) {
                            chollistarray.add((String) cholsnapshot.getValue());

                            int valuec = Integer.parseInt(chollistarray.toString().substring(1, chollistarray.toString().length() - 1));
                            if (valuec <= 200)
                                chollist.setBackgroundColor(Color.GREEN);
                            else if (valuec > 200 && valuec <= 240)
                                chollist.setBackgroundColor(Color.YELLOW);
                            else if (valuec > 240) {
                                chollist.setBackgroundColor(Color.RED);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(username + "'s Cholesterol is critical [" + valuec + "]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(2, builder.build());

                                if(n02 == 0) {
                                    String subject = username + "'s Cholesterol is critical [" + valuec + "]";
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    intent.setType("message/rfc822");
                                    startActivity(Intent.createChooser(intent, "Choose an email client"));
                                    n02++;
                                }
                            }
                        }
                        choladapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                glcreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        glclistarray.clear();
                        for (DataSnapshot glcsnapshot : dataSnapshot.getChildren()) {
                            glclistarray.add((String) glcsnapshot.getValue());

                            int valueg = Integer.parseInt(glclistarray.toString().substring(1, glclistarray.toString().length() - 1));
                            if (valueg >= 80 && valueg <= 140)
                                glclist.setBackgroundColor(Color.GREEN);
                            else if (valueg > 140 && valueg <= 200)
                                glclist.setBackgroundColor(Color.YELLOW);
                            else if (valueg >= 60 && valueg < 80)
                                glclist.setBackgroundColor(Color.YELLOW);
                            else if (valueg > 200 || valueg < 60) {
                                glclist.setBackgroundColor(Color.RED);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "idnotify")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle("Critical Health")
                                        .setContentText(username + "'s Glucose is critical [" + valueg +"]")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(3, builder.build());

                               if(n03 == 0) {
                                   String subject = username + "'s Glucose is critical [" + valueg + "]";
                                   Intent intent = new Intent(Intent.ACTION_SEND);
                                   intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                   intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                   intent.setType("message/rfc822");
                                   startActivity(Intent.createChooser(intent, "Choose an email client"));
                                   n03++;
                               }
                            }

                        }
                        glcadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

//hrreference = hrreference.orderByKey().startAt("20").endAt("50");
//hrreference = hrreference.orderByKey().limitToLast(4);

/*
                //hrstring = listarray.toString();
                //hrtxt1.setText(listarray.toString());
                hrtxt1.setText("");

                for(int i = 0; i < listarray.size(); i++){
                    edit.append(listarray.get(i).toString());
                    //hrtxt1.append(listarray.get(i).toString());
                }
 */

        /*
       //uid = hiddenid.getText().toString();

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(MainActivity.this, "Full History!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this , HistoryActivity.class));
            }
        });*/


/*
    private void dataread()
    {
        final String uid = emailuser.getUid();
        DatabaseReference usernamereference = rootnode.child("Users").child("Patients").child(uid).child("name");
        final Query hrreferencecurrent = rootnode.child("DataHistory").child("current").child(uid).child("HeartbeatsperMinute").orderByKey().limitToFirst(1);
        final Query cholreferencecurrent = rootnode.child("DataHistory").child("current").child(uid).child("Cholesterol");
        final DatabaseReference glcreferencecurrent = rootnode.child("DataHistory").child("current").child(uid).child("Glucose");

        final ArrayList<String> hrlistarray = new ArrayList<>();
        final ArrayAdapter hradapter = new ArrayAdapter<String>(this, R.layout.list_item, hrlistarray);
        hrlist.setAdapter(hradapter);

        final ArrayList<String> chollistarray = new ArrayList<>();
        final ArrayAdapter choladapter = new ArrayAdapter<String>(this, R.layout.list_item, chollistarray);
        chollist.setAdapter(choladapter);

        final ArrayList<String> glclistarray2 = new ArrayList<>();
        final ArrayAdapter glcadapter2 = new ArrayAdapter<String>(this, R.layout.list_item, glclistarray2);
        glclist.setAdapter(glcadapter2);


                hrreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //hrlistarray.clear();
                        int n1=0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            hrlistarray.add((String) snapshot.getValue());      // Single Values from child
                        }
                        hradapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                cholreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chollistarray.clear();
                        int n2 = 0;
                        for (DataSnapshot cholsnapshot : dataSnapshot.getChildren()){
                            chollistarray.add((String) cholsnapshot.getValue());
                        }
                        choladapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                glcreferencecurrent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        glclistarray2.clear();
                        int n3=0;
                        for (DataSnapshot glcsnapshot : dataSnapshot.getChildren()) {
                            glclistarray2.add((String) glcsnapshot.getValue());      // Single Values from child
                        }
                        glcadapter2.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


    }
 */