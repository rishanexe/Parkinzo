package com.parkinzoin.parkinzo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class profile extends AppCompatActivity {

    private TextView username,emailid,mobile,city,balance;
    private Button back,logout,add_money_btn;
    public static Button rentus,cancel_rent,verify_otp;
    EditText add_money;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentReference databaseReference;
    String userId, image_url;
    public String isRented;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(profile.this, Map.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        username = findViewById(R.id.profile_username);
        emailid = findViewById(R.id.profile_email);
        mobile = findViewById(R.id.profile_mobile);
        city = findViewById(R.id.profile_city);
        balance = findViewById(R.id.current_balance);
        add_money = findViewById(R.id.add_money);


        rentus = findViewById(R.id.profile_rent);

        back = findViewById(R.id.profile_back);
        logout = findViewById(R.id.profile_logout);
        add_money_btn = findViewById(R.id.add_money_btn);

        cancel_rent = findViewById(R.id.cancel_rent);
        verify_otp = findViewById(R.id.verify_otp);

        cancel_rent.setVisibility(View.INVISIBLE);
        verify_otp.setVisibility(View.INVISIBLE);


        firebaseFirestore = firebaseFirestore.getInstance();

        userId = firebaseAuth.getInstance().getCurrentUser().getEmail();

        databaseReference = firebaseFirestore.collection("Users").document(userId);



        databaseReference.addSnapshotListener(profile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                        username.setText(value.getString("Full Name"));
                        emailid.setText(value.getString("Email"));
                        mobile.setText(value.getString("Mobile no"));
                        city.setText(value.getString("City"));


                        balance.setText(value.get("Wallet").toString());
                        image_url = value.get("Rent_Url").toString();
                        isRented = value.get("Renting PlaceName").toString();

                if (!isRented.equals("0")){
                    Log.d("isRented","if "+isRented);
                    rentus.setVisibility(View.INVISIBLE);
                    cancel_rent.setVisibility(View.VISIBLE);
                    verify_otp.setVisibility(View.VISIBLE);
                }
                else{
                    Log.d("isRented","else "+isRented);
                    rentus.setVisibility(View.VISIBLE);
                    cancel_rent.setVisibility(View.INVISIBLE);
                    verify_otp.setVisibility(View.INVISIBLE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, Map.class));
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
                Toast.makeText(getApplicationContext(),"Logged-out successfully",Toast.LENGTH_SHORT).show();
            }
        });

        rentus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, rentus.class));
            }
        });



        add_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            float user_balance = Float.parseFloat(document.get("Wallet").toString());
                            float amount = Float.parseFloat(add_money.getText().toString().trim());
                            add_money.setText("");

                            if(amount <= 0) {
                                Toast.makeText(getApplicationContext(),"Amount to be greater than 0",Toast.LENGTH_SHORT).show();
                            }else {
                                user_balance = user_balance + amount;

                                java.util.Map<String, Object> UserWalletData = new HashMap<>();
                                UserWalletData.put("Wallet", user_balance);

                                databaseReference.set(UserWalletData, SetOptions.merge());
                            }

                        }
                    }

                });
            }
        });



        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, verifyOTP.class));
            }
        });

        cancel_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                java.util.Map<String, Object> cancel_renting = new HashMap<>();
                cancel_renting.put("Renting PlaceName", "0");
                cancel_renting.put("Renting address", "");
                cancel_renting.put("Vehicle capacity",0);
                cancel_renting.put("Rate per hour","");
                cancel_renting.put("lat",0);
                cancel_renting.put("long",0);
                cancel_renting.put("Rent_Url","");

                if (!image_url.equals("")) {
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(image_url);
                    storageReference.delete();
                }

                databaseReference.update(cancel_renting);

                Toast.makeText(getApplicationContext(),"You have cancelled Renting",Toast.LENGTH_SHORT).show();

                rentus.setVisibility(View.VISIBLE);
                cancel_rent.setVisibility(View.INVISIBLE);
                verify_otp.setVisibility(View.INVISIBLE);
            }
        });



    }
}