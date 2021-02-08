package com.parkinzoin.parkinzo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.TimeUnit;

public class firstpage extends AppCompatActivity {

    private Button firstpage_login;
    private Button firstpage_signup;
    FirebaseAuth fauth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String otp;
    public String space_id;
    public boolean otpCheckFlag;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        fauth = FirebaseAuth.getInstance();

        firstpage_login= findViewById(R.id.firstpage_login);
        firstpage_signup= findViewById(R.id.firstpage_signup);

        final FirebaseUser user = fauth.getCurrentUser();


        if(user!=null) {
            final DocumentReference docRef = db.collection("Users").document(user.getEmail());

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        otp = document.get("OTP").toString();
                        space_id = document.get("Space_Id").toString();
                        Log.d("OTP", "otp "+otp);


                        if (otp.equals("0")) {
                            Log.d("OTPif", "otp "+otp);
                            Intent intent= new Intent(firstpage.this,Map.class);
                            startActivity(intent);
                        }
                        else {
                            Log.d("OTPelse","otp "+otp);
                            Intent intent= new Intent(firstpage.this,OTP.class);
                            intent.putExtra("Space_Email",space_id);
                            startActivity(intent);
                            finish();
                        }

                    }
                }
            });
        }



        firstpage_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(firstpage.this, signup.class);
                startActivity(intent2);
            }
        });

        firstpage_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user!=null){
                    startActivity(new Intent(firstpage.this, Map.class));
                    finish();
                }
                else {
                    startActivity(new Intent(firstpage.this, login.class));
                    finish();
                }
            }
        });
    }
}

