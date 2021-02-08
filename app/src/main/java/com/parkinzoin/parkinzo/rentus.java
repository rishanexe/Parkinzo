package com.parkinzoin.parkinzo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class rentus extends AppCompatActivity {
    EditText Address, vehicle_count,Name,rate;
    private Button rent,back;
    private ImageButton rentus_image;
    TextView username;
    CheckBox tnc;
    FirebaseAuth fauth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference databaseReference;
    private Bitmap image;
    private StorageReference mStorageRef;
    public String user;

    private String Rent_Url,PlaceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentus);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        rentus_image=findViewById(R.id.rentus_image);
        fauth = FirebaseAuth.getInstance();

        Name=findViewById(R.id.rentus_name);
        Address = findViewById(R.id.address);
        vehicle_count = findViewById(R.id.vehicle_count);
        rate = findViewById(R.id.rentus_rate);
        tnc = findViewById(R.id.checkBox);

        username = findViewById(R.id.rentus_username);

        rent = findViewById(R.id.rent);
        back = findViewById(R.id.back);

        rentus_image = findViewById(R.id.rentus_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        user = fauth.getCurrentUser().getEmail();
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        databaseReference = firebaseFirestore.collection("Users").document(user);

        databaseReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()) {
                    username.setText(value.getString("Full Name"));
                }
            }
        });
        rentus_image.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 0);

            }
        });
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PlaceName = Name.getText().toString().trim();
                String address = Address.getText().toString().trim();
                String count = vehicle_count.getText().toString().trim();
                String Rate = rate.getText().toString().trim();

                if (TextUtils.isEmpty(PlaceName)) {
                    Name.setError("Enter Place Name");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Address.setError("Enter full address");
                    return;
                }
                if (TextUtils.isEmpty(count)) {
                    vehicle_count.setError("Enter vehicle count");
                    return;
                }
                if (TextUtils.isEmpty(Rate)) {
                    rate.setError("Enter rate");
                    return;
                }
                if(tnc.isChecked()== true){
                    int v_count = Integer.parseInt(count);

                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(user);

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("Renting PlaceName", PlaceName);
                    userInfo.put("Renting address", address);
                    userInfo.put("Vehicle capacity",v_count);
                    userInfo.put("Rate per hour",Rate);

                    Context context = view.getContext();
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocationName(address, 1);
                        if(addressList.size()>0) {
                            Address cords = addressList.get(0);
                            userInfo.put("lat",cords.getLatitude());
                            userInfo.put("long",cords.getLongitude());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    documentReference.update(userInfo);

                    Name.setText("");
                    Address.setText("");
                    vehicle_count.setText("");
                    rate.setText("");
                    tnc.setChecked(false);
                    Toast.makeText(getApplicationContext(),"Details submitted ",Toast.LENGTH_SHORT).show();


                }
                else{
                    Toast.makeText(getApplicationContext(),"Please agree Terms & Conditions",Toast.LENGTH_SHORT).show();
                }
                upload();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rentus.this, profile.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");

        }
    }
    private void upload() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);


        final String random = PlaceName;
        StorageReference imageRef = mStorageRef.child("rentus_image/" + random);

        byte[] b = stream.toByteArray();
        imageRef.putBytes(b)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                                Rent_Url=downloadUri.toString();
                                java.util.Map<String, Object> data = new HashMap<>();
                                data.put("Rent_Url", Rent_Url);
                                databaseReference.set(data, SetOptions.merge());
                            }
                        });

                        Toast.makeText(rentus.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
//                        Glide.with(getApplicationContext())
//                                .load(Profile_Url)
//                                .into(display_image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(rentus.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
