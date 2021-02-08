package com.parkinzoin.parkinzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$");

    EditText username,email,mobile,pass,city;
    private Button login3;
    private Button signup3;
    TextView textview7;
    FirebaseAuth fauth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signp);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);


        mobile = findViewById(R.id.mobile);


        pass = findViewById(R.id.pass);
        city = findViewById(R.id.city);

        fauth = FirebaseAuth.getInstance();

        signup3 = findViewById(R.id.signup3);
        login3 = findViewById(R.id.login3);

        login3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = fauth.getCurrentUser();
                if(user!=null)
                {
                    startActivity(new Intent(getApplicationContext(),Map.class));
                }
                else{
                    startActivity(new Intent(getApplicationContext(), login.class));
                    finish();
                }
            }
        });


        signup3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                final String User = username.getText().toString().trim();
                final String Email = email.getText().toString().trim();

                final String Mobile = mobile.getText().toString().trim();

                final String Pass = pass.getText().toString().trim();
                final String City = city.getText().toString().trim();

                if(TextUtils.isEmpty(User)){
                    username.setError("Enter first name");
                    return;
                }

                if(TextUtils.isEmpty(Email)){
                    email.setError("Enter email-id");
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    email.setError("Please enter valid email id");
                    return;
                }

                if(TextUtils.isEmpty(Mobile)){
                    mobile.setError("Enter mobile no.");
                    return;
                }
                else if(Mobile.length() < 10){
                    mobile.setError("Enter valid mobile no.");
                    return;
                }


                if(TextUtils.isEmpty(Pass)){
                    pass.setError("Enter password");
                    return;
                }
                else if(!PASSWORD_PATTERN.matcher(Pass).matches()){
                    pass.setError("Password too weak");
                    return;
                }
                if(TextUtils.isEmpty(Pass)){
                    city.setError("Enter City Name");
                    return;
                }




                fauth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser user = fauth.getCurrentUser();
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            Toast.makeText(signup.this,"Account created",Toast.LENGTH_SHORT).show();

                            DocumentReference documentReference = firebaseFirestore.collection("Users").document(user.getEmail());

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Full Name", User);
                            userInfo.put("Email",Email);
                            userInfo.put("Mobile no",Mobile);
                            userInfo.put("City", City);
                            userInfo.put("Password",Pass);
                            userInfo.put("OTP", "0");
                            userInfo.put("Space_Id", " ");
                            userInfo.put("Wallet", 0);
                            userInfo.put("Renting PlaceName", "0");
                            userInfo.put("Rent_Url","0");
                            userInfo.put("Vehicle capacity",0);
                            userInfo.put("User_Id", "0");
                            userInfo.put("Wallet_Diff", 0);
                            userInfo.put("Wallet_Notify",1);
                            userInfo.put("Space_Notify",1);
                            userInfo.put("Cancel_Id","0");
                            userInfo.put("Cancel_Notify",1);

                            documentReference.set(userInfo);

                            startActivity(new Intent(getApplicationContext(),login.class));
                            finish();
                        }else{
                            Toast.makeText(signup.this,"Something went wrong"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });



    }
}