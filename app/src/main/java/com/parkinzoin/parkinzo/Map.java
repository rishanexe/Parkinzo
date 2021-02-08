package com.parkinzoin.parkinzo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map extends AppCompatActivity {

    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button next;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FirebaseAuth fauth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference databaseReference;
    public String user,p,initial;
    private char f;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fauth = FirebaseAuth.getInstance();
        next = findViewById(R.id.home_profile);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Map.this, profile.class));
            }
        });

        user = fauth.getCurrentUser().getEmail();
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        databaseReference = firebaseFirestore.collection("Users").document(user);

        databaseReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()) {
                    p=value.getString("Full Name");
                    for(int i=0;i<p.length();i++){
                        f=p.charAt(0);
                        initial=String.valueOf(f);
                        next.setText(initial.toUpperCase());

                    }
                }
            }
        });

        // Construct a GeoDataClient.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();



        db.collection("Users")
                .document(user)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        String user_id = value.get("User_Id").toString();
                        Long space_notify = value.getLong("Space_Notify");

                        Long wallet_diff = value.getLong("Wallet_Diff");
                        Long wallet_notify = value.getLong("Wallet_Notify");

                        String cancel_id = value.getString("Cancel_Id");
                        Long cancel_notify = value.getLong("Cancel_Notify");


                        if(!user_id.equals("0") && space_notify==0){
                            addNotification(user_id, (long) 0, "0", 1);

                            java.util.Map<String, Object> notify_data = new HashMap<>();
                            notify_data.put("Space_Notify", 1);

                            db.collection("Users").document(user)
                                    .set(notify_data, SetOptions.merge());
                        }


                        if(wallet_diff!=0 && wallet_notify==0) {
                            addNotification("0", wallet_diff, "0", 2);

                            java.util.Map<String, Object> notify_data = new HashMap<>();
                            notify_data.put("Wallet_Notify", 1);

                            db.collection("Users").document(user)
                                    .set(notify_data, SetOptions.merge());
                        }


                        if(cancel_id!="0" && cancel_notify==0) {
                            addNotification("0", wallet_diff, cancel_id, 3);

                            java.util.Map<String, Object> notify_data = new HashMap<>();
                            notify_data.put("Cancel_Notify", 1);
                            db.collection("Users").document(user)
                                    .set(notify_data, SetOptions.merge());
                        }

                    }
                });

    }


    private void getCurrentLocation() {

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {

                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));

                            if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            {
                                mMap.setMyLocationEnabled(true);

                                db.collection("Users")
                                        .whereGreaterThan("Vehicle capacity", 0)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if(document.get("lat")!=null) {
                                                            double lat = Double.parseDouble(document.get("lat").toString());
                                                            double lng = Double.parseDouble(document.get("long").toString());

                                                            LatLng latlng = new LatLng(lat, lng);
                                                            String snippet= document.get("Renting PlaceName").toString() + "\n" +
                                                                    document.get("Renting address").toString() + "\n" +
                                                                    document.get("Vehicle capacity").toString() + "\n"+ document.get("Rate per hour").toString() + "\n" + document.get("Email").toString() + "\n" + document.get("Rent_Url").toString();

                                                            //String space_email = document.get("Email").toString();

                                                            MarkerOptions options = new MarkerOptions().position(latlng).title(document.get("Renting PlaceName").toString())
                                                                    .snippet(snippet)
                                                                    .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.usermarker));

                                                            googleMap.addMarker(options);

                                                            //Log.d("CORDS", document.getId() + " => " + document.get("lat") + " " +lat+" "+lng);
                                                        }
                                                    }
                                                } else {
                                                    Log.d("ERROR", "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });




                            } else {
                                ActivityCompat.requestPermissions(Map.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
                            }





                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    String details=marker.getSnippet();
                                    Intent intent= new Intent(Map.this,Details.class);

                                    intent.putExtra("Details",details);
                                    startActivity(intent);

                                    return false;
                                }
                            });
                        }

                    });
                }
            }
        });
    }



    private void addNotification(String user_id, Long wallet_diff, String cancel_id, Integer diff_flag) {
        String notify_title, notify_text;

        if (diff_flag == 1) {
            notify_title = "A user has booked your space.";
            notify_text = user_id+" has booked your space.";
        } else if(diff_flag == 2) {
            notify_title = "Amount added to Payinzo";
            notify_text = "Rs."+wallet_diff+" has been added to your Payinzo wallet.";
        } else {
            notify_title = "Booking cancelled";
            notify_text = cancel_id+" has cancelled your space booking. Rs."+wallet_diff+" has been added to your wallet";
        }

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







    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
