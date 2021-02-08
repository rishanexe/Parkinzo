package com.parkinzoin.parkinzo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class verifyOTP extends AppCompatActivity {

    private EditText email, otp;
    private Button verify;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentReference databaseReference;

    public String user_otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyotp);

        email = findViewById(R.id.user_email);
        otp = findViewById(R.id.user_otp);

        verify = findViewById(R.id.verify);

        firebaseFirestore = firebaseFirestore.getInstance();



        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailid = email.getText().toString().trim();

                final String entered_otp = otp.getText().toString().trim();

//                String userId = firebaseAuth.getInstance().getCurrentUser().getEmail();

                databaseReference = firebaseFirestore.collection("Users").document(emailid);

                databaseReference.addSnapshotListener(verifyOTP.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {

                         user_otp = value.get("OTP").toString();

                        if (entered_otp.equals(user_otp))
                        {
                            Toast.makeText(getApplicationContext(),"OTP verified !!!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Wrong OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                email.setText("");
                otp.setText("");
            }
        });
    }
}