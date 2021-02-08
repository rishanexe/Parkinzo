package com.parkinzoin.parkinzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OTP extends AppCompatActivity {


    TextView random;
    Button done,cancel;
    FirebaseAuth fauth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Date EndTime;
    private Date StartTime;
    public String temp;
    public float total_amount;
    public int rate_per_hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        fauth = FirebaseAuth.getInstance();
        random = findViewById(R.id.random);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);

        Intent intent = getIntent();
//        final String temp = intent.getStringExtra("Random"); // here 0 is the default value
        final String user = fauth.getCurrentUser().getEmail();

        final String rentus_email = getIntent().getStringExtra("Space_Email");



        final DocumentReference docRef = db.collection("Users").document(user);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    temp = document.get("OTP").toString();
                    random.setText(temp);
                }
            }
        });


        final DocumentReference spaceRef = db.collection("Users").document(rentus_email);

        spaceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    rate_per_hour = Integer.parseInt(document.get("Rate per hour").toString());
                }
            }
        });



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                try {
                    EndTime=currentTime.parse(new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final DocumentReference docRef = db.collection("Users").document(user);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                StartTime=currentTime.parse(document.get("Start_Time").toString());
                                long difference = EndTime.getTime() - StartTime.getTime();
                                int hours = (int) difference/(1000 * 60 * 60);
                                float mins = (float) ((difference/(1000*60)) % 60)/60;
                                float totalDiff = (float) hours + mins;
//                                float totalDiff = (float) 1.5;


                                total_amount = rate_per_hour * totalDiff;

                                float user_balance = Float.parseFloat(document.get("Wallet").toString());
                                user_balance = user_balance - total_amount;

                                addNotification(total_amount);

                                Map<String, Object> data = new HashMap<>();
                                data.put("Wallet", user_balance);
                                data.put("OTP", "0");
                                data.put("Start_Time","0");
                                data.put("Date","0");
                                data.put("Space_Id", "");

                                db.collection("Users").document(user)
                                        .set(data, SetOptions.merge());
                                //Log.d("Rentus_email","bhb "+rentus_email);

                                final DocumentReference spaceRef = db.collection("Users").document(rentus_email);

                                spaceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            float space_balance = Float.parseFloat(document.get("Wallet").toString());
                                            space_balance = space_balance + total_amount;
                                            Log.d("TotalAmount","Space "+total_amount);
                                            Log.d("SapceBal","Space "+space_balance);
                                            int available_space = Integer.parseInt(document.get("Vehicle capacity").toString());

                                            available_space++;

                                            Map<String, Object> SpaceWalletData = new HashMap<>();
                                            SpaceWalletData.put("Wallet", space_balance);
                                            SpaceWalletData.put("Vehicle capacity", available_space);
                                            SpaceWalletData.put("Wallet_Diff", total_amount);
                                            SpaceWalletData.put("User_Id", "0");
                                            SpaceWalletData.put("Wallet_Notify",0);

                                            spaceRef.set(SpaceWalletData, SetOptions.merge());
                                        }
                                    }
                                });

                                startActivity(new Intent(OTP.this, com.parkinzoin.parkinzo.Map.class));
                                finish();

                                //Log.d("Diff"," "+totalDiff);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                        }
                    }
                });

//                databaseReference = db.collection("Users").document(rentus_email);
//                final DocumentReference spaceRef = db.collection("Users").document(rentus_email);
//
//                spaceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//
//                            float space_balance = Float.parseFloat(document.get("Wallet").toString());
//                            space_balance = space_balance + total_amount;
//
//                            int available_space = Integer.parseInt(document.get("Vehicle capacity").toString());
//
//                            available_space++;
//
//                            Map<String, Object> SpaceWalletData = new HashMap<>();
//                            SpaceWalletData.put("Wallet", space_balance);
//                            SpaceWalletData.put("Vehicle capacity", available_space);
//
//                            spaceRef.set(SpaceWalletData, SetOptions.merge());
//                        }
//                    }
//                });
//
//                startActivity(new Intent(OTP.this, com.parkinzoin.parkinzo.Map.class));
//                finish();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final float cancel_rate = (float) (rate_per_hour*0.1);

                // Update one field, creating the document if it does not already exist.
                Map<String, Object> data = new HashMap<>();
                data.put("OTP", "0");
                data.put("Start_Time","0");
                data.put("Date","0");
                data.put("Space_Id", "");

                db.collection("Users").document(user)
                        .set(data, SetOptions.merge());

                final DocumentReference spaceRefCancelled = db.collection("Users").document(rentus_email);

                spaceRefCancelled.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            float space_balance = Float.parseFloat(document.get("Wallet").toString());
                            space_balance = space_balance + cancel_rate;

                            int available_space = Integer.parseInt(document.get("Vehicle capacity").toString());

                            available_space++;

                            addNotification(cancel_rate);

                            Map<String, Object> SpaceWalletData = new HashMap<>();
                            SpaceWalletData.put("Wallet", space_balance);
                            SpaceWalletData.put("Vehicle capacity", available_space);
                            SpaceWalletData.put("Wallet_Diff", cancel_rate);
                            SpaceWalletData.put("User_Id", "0");
                            SpaceWalletData.put("Wallet_Notify",0);
                            SpaceWalletData.put("Cancel_Id", user);
                            SpaceWalletData.put("Cancel_Notify", 0);

                            spaceRefCancelled.set(SpaceWalletData, SetOptions.merge());
                        }
                    }

                });
                startActivity(new Intent(OTP.this, com.parkinzoin.parkinzo.Map.class));
                finish();
                Toast.makeText(OTP.this,"You cancelled the parking",Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void addNotification(float total_amount) {
        String notify_title, notify_text;

        notify_title = "Amount deducted from Payinzo";
        notify_text = "Rs."+total_amount+" has been deducted from your Payinzo wallet.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(notify_title)
                .setContentText(notify_text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notify_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(321, builder.build());

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this,profile.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(321, builder.build());
    }
}