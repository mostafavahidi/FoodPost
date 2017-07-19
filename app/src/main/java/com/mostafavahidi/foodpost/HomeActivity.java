package com.mostafavahidi.foodpost;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafavahidi.foodpost.data.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private static String myUid;
    private List<Food> allFoodPosts;
    private static LatLng userLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the main app toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //SETTING UP THE SERVER VARIABLES/CHANGES
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getCurrentUser().getUid();


        //Setting up the app Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        firebaseDatabase.getReference(
                "foods")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        allFoodPosts = new ArrayList<Food>();
                        mMap = googleMap;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Food mr = ds.getValue(Food.class);
                            allFoodPosts.add(mr);
                            Log.i("foodPost element: ", mr.getText());
                            Geocoder geoCoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geoCoder.getFromLocationName(mr.getText(), 5);
                                if (addresses.size() > 0) {
                                    Double lat = (double) (addresses.get(0).getLatitude());
                                    Double lon = (double) (addresses.get(0).getLongitude());

                                    Log.d("lat-long", "" + lat + "......." + lon);
                                    final LatLng user = new LatLng(lat, lon);
                                    /*used marker for show the location */

                                    if (mr.getImageUrl() != null){
                                        try {
                                            Bitmap iconBitmap = decodeFromFirebaseBase64(mr.getImageUrl());
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(user)
                                                    .title(mr.getFoodDesc())
                                                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("SET MAPICON2", "YES WE DIIIIIDD!");
                                    } else {
                                        mMap.addMarker(new MarkerOptions()
                                                .position(user)
                                                .title(mr.getFoodDesc())
                                                .icon(BitmapDescriptorFactory.defaultMarker()));
                                    }
//                                    // Move the camera instantly to hamburg with a zoom of 15.
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
//
//                                    // Zoom in, animating the camera.
//                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                                }
                                Log.i("SET TRY BLOCK2", "YES WE DIDDD!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //Setting up the current user's lat lon.
        Geocoder geoCoder = new Geocoder(HomeActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(firebaseDatabase.getReference(
                    "foods/" + firebaseAuth.getCurrentUser().getUid() + "/text").toString(), 5);
            if (addresses.size() > 0) {
                Double lat = (double) (addresses.get(0).getLatitude());
                Double lon = (double) (addresses.get(0).getLongitude());

                Log.d("lat-long", "" + lat + "......." + lon);
                final LatLng user = new LatLng(lat, lon);
                                    /*used marker for show the location */
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
                userLatLng = new LatLng(lat, lon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                Intent intentAdd = new Intent(this, PostActivity.class);
                this.startActivity(intentAdd);
                break;

            case R.id.action_search:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                this.startActivity(intentSearch);
                break;

            case R.id.action_settings:
                Intent intentSetting = new Intent(this, SettingActivity.class);
                this.startActivity(intentSetting);
                break;

            case R.id.action_delete:
                firebaseDatabase.getReference("foods/" + firebaseAuth.getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(HomeActivity.this,"Your Food Post has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}