package com.deepak.neelimaskitchen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    private static final String TAG = "";
    EditText etPhoneLogin, etPasswordLogin;
    String phoneLogin, passwordLogin;
    Button bEnter;
    com.rey.material.widget.Button bReset;
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Add Forgot Password Button > make sure user has number ET filled out > get that number and
        //check if it exists in database > delete that number if it exists and send user
        //to register again with new password

        final Toast doesntExist = FancyToast.makeText(Login.this, "Phone number doesn't exist, make sure you enter # correctly", FancyToast.LENGTH_LONG, FancyToast.WARNING, false);

        //Hello Message
        Toast welcomeToast = FancyToast.makeText(Login.this, "Welcome!  Please Login", FancyToast.LENGTH_SHORT, FancyToast.INFO, false);
        welcomeToast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 600);
        welcomeToast.show();


        //Edit Texts
        etPhoneLogin = findViewById(R.id.etNumberLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        phoneLogin = etPhoneLogin.getText().toString();
        passwordLogin = etPasswordLogin.getText().toString();

        //Button
        bEnter = findViewById(R.id.bEnter);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        bReset = findViewById(R.id.bResetProfile);

        //Init Paper Library (Similar to Shared Prefs)
        Paper.init(this);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        //Receive Registration info if user successfully registered.
        etPhoneLogin.setText(getIntent().getStringExtra("phone"));
        etPasswordLogin.setText(getIntent().getStringExtra("password"));

        bReset.setOnClickListener(v -> {

            if (!etPhoneLogin.getText().toString().isEmpty()) {
//            FancyToast.makeText(Login.this, "Phone # Cannot be empty.", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query profileQuery = ref.child("User").orderByChild(etPhoneLogin.getText().toString());

                profileQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(profileSnapshot.getKey(), etPhoneLogin.getText().toString())) {
                                profileSnapshot.getRef().removeValue();
                                //SUCCESS Reset send user to Register Screen
                                Intent register = new Intent(Login.this, Register.class);
                                FancyToast.makeText(Login.this, "Reset successful!  Re-Enter information with new password!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                startActivity(register);
                                finish();

                            } else
                                doesntExist.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            } else
                FancyToast.makeText(Login.this, "Enter phone number you want to reset", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
        });


        //When user clicks Enter button
        bEnter.setOnClickListener(view -> {

            //Save User Info if checkbox is selected
            if (cbRememberMe.isChecked()) {
                Paper.book().write(Common.USER_KEY, etPhoneLogin.getText().toString());
                Paper.book().write(Common.PW_KEY, etPasswordLogin.getText().toString());
            }

            //Dialogue to show progress
            final ProgressDialog progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            //Prevent Phone Number & Password from being empty
            if (etPhoneLogin.getText().toString().isEmpty() && etPasswordLogin.getText().toString().isEmpty()
                    || etPhoneLogin.getText().toString().isEmpty()
                    || etPasswordLogin.getText().toString().isEmpty()) {

                progressDialog.dismiss();
                FancyToast.makeText(Login.this, "Phone # Or Password Cannot be empty.", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show();

            }


            //scan Database to ensure User is in system with correct credentials
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    //Check if User exists in Database
                    if (dataSnapshot.child(etPhoneLogin.getText().toString()).exists()) {

                        progressDialog.dismiss();

                        // Get User Information
                        User user = dataSnapshot.child(etPhoneLogin.getText().toString()).getValue(User.class);
                        if (user != null) {
                            user.setPhone(etPhoneLogin.getText().toString());
                        }

                        if (user != null) {
                            if (user.getPassword().equals(etPasswordLogin.getText().toString())) {

                                //SUCCESS LOGIN
                                FancyToast.makeText(Login.this, "Success!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                //Send user to Menu Screen
                                Intent menu = new Intent(Login.this, Home.class);
                                Common.currentUser = user;
                                startActivity(menu);
                                finish();


                            } else {

                                progressDialog.dismiss();
                                FancyToast.makeText(Login.this, "Login Failed.  Try again!", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                            }
                        }

                    } else {

                        progressDialog.dismiss();
                        FancyToast.makeText(Login.this, "Account Not Found.  Go back and register.", FancyToast.LENGTH_LONG, FancyToast.CONFUSING, false).show();

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

    }

    @Override
    public void onBackPressed() {
        //Send user to Main Screen
        Intent main = new Intent(Login.this, MainActivity.class);
        startActivity(main);
        finish();
    }

}
