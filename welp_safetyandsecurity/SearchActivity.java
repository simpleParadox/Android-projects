package com.abomicode.welp_safetyandsecurity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
This is the launcher activity.
All the authentication things will be done here.
 */


public class SearchActivity extends AppCompatActivity{

    PopupWindow popupWindow;

    static FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    FirebaseDatabase database;
    DatabaseReference reference;



    DetailsAdapter detailsAdapter;


    ListView listView;


    public ArrayList<UserDetails> retrievedDetails;
    ArrayList<String> keys;


    private int signedIn;


    Handler handler;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");

        listView = (ListView) findViewById(R.id.search_list_view);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        handler = new Handler();

        List<UserDetails> detailsList = new ArrayList<>();
        detailsAdapter = new DetailsAdapter(getApplicationContext(), R.layout.list_details, detailsList);
        listView.setAdapter(detailsAdapter);

        retrievedDetails = new ArrayList<>();
        keys = new ArrayList<>();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //Checking if the user is signed in or not.

        //Log.i("searchAct 1 ", "Inside onStart()");
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            //The user is not signed in.
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            //The user is signed in and to be sent to the login page.
            signedIn = 1;
            //Do something.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (signedIn == 0) {
            finish();
        }
        //The user is signed in.
        startService(new Intent(getApplicationContext(),LocationService.class));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                View phoneNumber = view.findViewById(R.id.list_details_phone);
                TextView phone = (TextView) phoneNumber;

                final String recipient = (String) phone.getText();

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view1 = layoutInflater.inflate(R.layout.popup,null);

                popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ImageView call = (ImageView) view1.findViewById(R.id.popup_call);
                ImageView showLoc = (ImageView) view1.findViewById(R.id.popup_show_location);

                TextView number = (TextView) view1.findViewById(R.id.popup_contact);
                number.setText(recipient);

                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + recipient));
                        startActivity(intent);

                    }
                });

                showLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(SearchActivity.this, "Opening Map. Please wait...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),ShowLocation.class);
                        String key = keys.get(position);
                        intent.putExtra("keys",key);
                        popupWindow.dismiss();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.m1:
                signOut();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    private void signOut(){

        firebaseAuth.signOut();
        signedIn = 0;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    public void searchPeople(View view) {

        retrievedDetails.clear();

        final EditText editText = (EditText) findViewById(R.id.search);

        final String people = editText.getText().toString().toLowerCase();

        if(!people.equals("")){

            detailsAdapter.clear();

            editText.setEnabled(false);

            Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

            //Will be called every time the search icon is pressed.

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    retrievedDetails.clear();
                    if(dataSnapshot!=null) {
                        int flag = 0;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            UserDetails userDetails = snapshot.getValue(UserDetails.class);

                            assert userDetails != null;
                            String fullName = userDetails.getFullName().toLowerCase();

                            if(fullName.startsWith(people)) {

                                String phone = userDetails.getPhone();

                                ContentResolver contentResolver = getContentResolver();
                                String []selectionArgs = {phone};

                                String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "= ?";

                                //Has contacts.
                                Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,selection,selectionArgs,null);

                                detailsAdapter.clear();

                                assert cursor != null;
                                while(cursor.moveToNext()){
                                    if(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).equals(phone)){
                                        retrievedDetails.add(userDetails);
                                        detailsAdapter.add(userDetails);
                                        keys.add(snapshot.getKey());
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(detailsAdapter.getCount()>0){
                                    editText.setEnabled(true);
                                }
                            }
                        }
                        if(flag==0) {
                            editText.setEnabled(true);
                            Toast.makeText(SearchActivity.this, "No users with that name!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        else {
            super.onBackPressed();
        }
    }
}
