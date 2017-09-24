package com.abomicode.welp_safetyandsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.abomicode.welp_safetyandsecurity.SearchActivity.firebaseAuth;

public class SignUpActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        database = FirebaseDatabase.getInstance();


    }


    public void register(View view) {
        //Get all the information from the text fields and sign up.

        //Get all the information.
        final EditText fullNameField = (EditText) findViewById(R.id.signup_screen_name_field);
        final EditText emailField = (EditText) findViewById(R.id.signup_screen_email_field);
        final EditText passwordField = (EditText) findViewById(R.id.signup_screen_password_field);
        final EditText confirmPasswordField = (EditText) findViewById(R.id.signup_screen_confirmpassword_field);
        final EditText phoneField = (EditText) findViewById(R.id.signup_screen_phone_field);

        //Get the string equivalent of the fields.
        final String fullName = fullNameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        final String phone = phoneField.getText().toString().trim();


        if(!(fullName.equals("") && email.equals("") && phone.equals("")) && (phone.length()==10)) {

            //Check if the password field value matches with the confirm password field.
            if (password.equals(confirmPassword) && !password.equals("")) {

                fullNameField.setEnabled(false);
                emailField.setEnabled(false);
                passwordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                phoneField.setEnabled(false);

                final Button regButton = (Button) findViewById(R.id.signup_screen_register_button);
                regButton.setEnabled(false);

                Toast.makeText(SignUpActivity.this, "Please Wait", Toast.LENGTH_LONG).show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            /*
                            fullNameField.setText("");
                            emailField.setText("");
                            passwordField.setText("");
                            confirmPasswordField.setText("");
                            phoneField.setText("");
                            */

                            UserDetails userDetails = new UserDetails(fullName, email, password, phone);


                            reference = database.getReference().child("users");

                            //Creating a child with the email as the node.
                            String key = reference.push().getKey();

                            reference.child(key).setValue(userDetails);

                            Toast.makeText(SignUpActivity.this, "Welcome" + fullName, Toast.LENGTH_SHORT).show();

                            //Need to store the current user in the local database.
                            CurrentUserDetails currentUserDetails = new CurrentUserDetails(getApplicationContext());
                            currentUserDetails.updateKey(key);

                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            startActivity(intent);
                            finish();


                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Cannot sign up", Toast.LENGTH_SHORT).show();

                            fullNameField.setEnabled(true);
                            emailField.setEnabled(true);
                            passwordField.setEnabled(true);
                            confirmPasswordField.setEnabled(true);
                            phoneField.setEnabled(true);

                           // Button regButton = (Button) findViewById(R.id.signup_screen_register_button);
                            regButton.setEnabled(true);

                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Password fields do not match.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Please check your details!", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLogin(View view) {

        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
