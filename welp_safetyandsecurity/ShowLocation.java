package com.abomicode.welp_safetyandsecurity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowLocation extends AppCompatActivity implements OnMapReadyCallback{


    Marker marker1;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get the key of the databaseReference.

        key = getIntent().getExtras().getString("keys");

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Show the location of the person in this method.
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final MarkerOptions marker = new MarkerOptions().title("Last known location");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(key);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Do initially and when the latitude or the longitude is changed.

                if(marker1!=null)
                    marker1.remove();

                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);

                double latitude = userDetails.getLatitude();
                double longitude = userDetails.getLongitude();

                LatLng latLng1 = new LatLng(latitude, longitude);

                marker.position(latLng1);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,18));
                marker1 = googleMap.addMarker(marker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
