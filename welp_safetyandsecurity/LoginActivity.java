package com.abomicode.welp_safetyandsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.abomicode.welp_safetyandsecurity.SearchActivity.firebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void signIn(View view){


        final EditText emailField = (EditText) findViewById(R.id.login_screen_email_field);
        final EditText passwordField = (EditText) findViewById(R.id.login_screen_password_field);

        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();

        if(!(email.equals("")) && !(password.equals(""))) {
            logIn(email,password,emailField,passwordField);
            Toast.makeText(this, "Wait...", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Enter the required Fields", Toast.LENGTH_SHORT).show();
            emailField.setText("");
            passwordField.setText("");
        }
    }

    public void goToRegister(View view) {

        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    private void logIn(final String email, String password, final EditText emailField, final EditText passwordField){

        emailField.setEnabled(false);
        passwordField.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            emailField.setText("");
                            passwordField.setText("");


                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                        UserDetails userDetails = snapshot.getValue(UserDetails.class);

                                        assert userDetails != null;
                                        String email1 = userDetails.getEmail();
                                        if(email1.equals(email)){
                                            //Found the Logged In User in the database.
                                            String key = snapshot.getKey();

                                            //Toast.makeText(LoginActivity.this, key, Toast.LENGTH_SHORT).show();

                                            CurrentUserDetails currentUserDetails = new CurrentUserDetails(getApplicationContext());
                                            currentUserDetails.updateKey(key);
                                            break;

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Do something.
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                            if (user == null) {
                                //No such account.
                                //Go to sign up.
                                Toast.makeText(LoginActivity.this, "No such Account. Please Register.", Toast.LENGTH_SHORT).show();
                                emailField.setEnabled(true);
                                passwordField.setEnabled(true);
                                emailField.setText("");
                                passwordField.setText("");

                            } else {
                                //Some other problem might occur.
                                Toast.makeText(LoginActivity.this, "Cannot Sign In\nPlease Check your network connection.", Toast.LENGTH_SHORT).show();
                                emailField.setEnabled(true);
                                passwordField.setEnabled(true);
                            }
                        }
                    }
                });

    }
}
