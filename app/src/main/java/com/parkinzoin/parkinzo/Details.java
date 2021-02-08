package com.parkinzoin.parkinzo;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Random;

public class Details extends AppCompatActivity {
    private TextView placename,address,count,rate;
    private Button reserve;
    private int n;
    private String no;
    FirebaseAuth fauth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference databaseReference;
    private String currentDate;
    private String currentTime;
    private String Detail_Url;
    private ImageView details_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        fauth = FirebaseAuth.getInstance();
        placename = findViewById(R.id.details_placename);
        address = findViewById(R.id.details_address);
        count = findViewById(R.id.details_count);
        rate = findViewById(R.id.details_rate);
        details_image = findViewById(R.id.details_image);

        reserve = findViewById(R.id.details_reserve);


        String Detail = getIntent().getStringExtra("Details");

        String s[] = Detail.split("\n");

        String Placename =  s[0].toString();
        String Address =  s[1].toString();
        String Count = s[2].toString();
        final String Rate =  s[3].toString();
        final String Space_Email =  s[4].toString();
        Detail_Url =  s[5].toString();

        Glide.with(getApplicationContext())
                .load(Detail_Url)
                .into(details_image);

        final int space_count = Integer.parseInt(Count);


        placename.setText(Placename);
        address.setText(Address);
        count.setText(Count);
        rate.setText(Rate);
        Random rnd = new Random();
        n = 100000 + rnd.nextInt(900000);
        no=Integer.toString(n);
        currentDate=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime=new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = fauth.getCurrentUser().getEmail();
                final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                DocumentReference docRefWallet = firebaseFirestore.collection("Users").document(user);
                docRefWallet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                                float user_balance = Float.parseFloat(document.get("Wallet").toString());

                                if (user_balance <= Float.parseFloat(Rate)) {
                                    Toast.makeText(Details.this,"Balance must be greater than rate",Toast.LENGTH_SHORT).show();

                                } else {
                                    int available_space;


                                    available_space = space_count-1;

                                    DocumentReference docRef = firebaseFirestore.collection("Users").document(Space_Email);

                                    Map<String, Object> count = new HashMap<>();
                                    count.put("Vehicle capacity", available_space);
                                    count.put("User_Id",user);
                                    count.put("Space_Notify", 0);
                                    
                                    docRef.set(count, SetOptions.merge());

                                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(user);
                                    java.util.Map<String, Object> data = new HashMap<>();
                                    data.put("OTP", no);
                                    data.put("Start_Time", currentTime);
                                    data.put("Date", currentDate);
                                    data.put("Space_Id", Space_Email);

                                    documentReference.update(data);

                                    Intent intent= new Intent(Details.this,OTP.class);
//                intent.putExtra("Random",no);
                                    intent.putExtra("Space_Email",Space_Email);
                                    startActivity(intent);
                                    finish();
                                }
                        }
                    }
                });
            }
        });
    }
}