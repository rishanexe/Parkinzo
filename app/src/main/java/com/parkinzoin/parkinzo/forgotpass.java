package com.parkinzoin.parkinzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class forgotpass extends AppCompatActivity {
    EditText forgotno;
    Button forgot_btn;
    FirebaseAuth fauth;
    public String pass, no;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(forgotpass.this, login.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        forgotno = findViewById(R.id.forgot_no);
        forgot_btn = findViewById(R.id.forgot_btn);

        fauth = FirebaseAuth.getInstance();

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                no = forgotno.getText().toString();
                Log.d("number", no);

                db.collection("Users").whereEqualTo("Mobile no", no)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        pass = document.get("Password").toString();
                                        Log.d("pass", pass);

                                        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                                            try{
                                                SmsManager smsManager = SmsManager.getDefault();
                                                smsManager.sendTextMessage(no, null, "Your Parkinzo login password is : \t" + pass + "\n\n - Parkinzo Support Team", null, null);
                                                Toast.makeText(getApplicationContext(), "Your password has been sent to your registered mobile number.", Toast.LENGTH_LONG).show();
                                            }catch (Exception e){
                                                e.getStackTrace();
                                                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                                        }
                                        forgotno.setText("");
                                    }
                                }
                            }
                        });

            }
        });
    }
}

//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                auth.sendPasswordResetEmail(email)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(forgotpass.this,"Password send to your Email Id",Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(forgotpass.this,"Password didnt send",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                db.collection("Users")
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        if ((document.get("Email").toString()).equals(email)) {
//
//                                            FirebaseAuth auth = FirebaseAuth.getInstance();
//
//
//                                            auth.sendPasswordResetEmail(email)
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                Toast.makeText(forgotpass.this,"Password send to your Email Id",Toast.LENGTH_SHORT).show();
//                                                            }else{
//                                                                Toast.makeText(forgotpass.this,"Password didnt send",Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                        else{
//                                            Toast.makeText(forgotpass.this,"User does not exists create a new one.",Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(getApplicationContext(), signup.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    }
//                                }
//                            }
//                        });

