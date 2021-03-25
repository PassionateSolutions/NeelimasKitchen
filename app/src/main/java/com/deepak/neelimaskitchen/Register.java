package com.deepak.neelimaskitchen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.deepak.neelimaskitchen.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Timer;
import java.util.TimerTask;

public class Register extends AppCompatActivity {

    EditText etName, etPhoneRegister, etPasswordRegister;
    Button bRegister;
    ImageView ivLoadingReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ImageView
        ivLoadingReg = findViewById(R.id.ivLoadingReg);

        //Edit Texts
        etName = findViewById(R.id.etName);
        etPhoneRegister = findViewById(R.id.etNumberRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);

        //Button
        bRegister = findViewById(R.id.bRegister);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        //When Register button is pressed
        bRegister.setOnClickListener(view -> {

            //Dialogue to show progress
            final ProgressDialog progressDialog = new ProgressDialog(Register.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            //Prevent Name, Phone Number & Password from being empty
            if (etPhoneRegister.getText().toString().isEmpty() && etPasswordRegister.getText().toString().isEmpty() && etName.getText().toString().isEmpty()
                    || etName.getText().toString().isEmpty()
                    || etPhoneRegister.getText().toString().isEmpty()
                    || etPasswordRegister.getText().toString().isEmpty()) {

                progressDialog.dismiss();
                FancyToast.makeText(Register.this, "Make sure nothing is empty.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();

            } else if (etPhoneRegister.getText().toString().trim().length() < 10 ){

                FancyToast.makeText(Register.this, "Make you phone number has area code as well, must be 10 digits", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                progressDialog.dismiss();


            }

            else {

                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //If customer already exists
                        if (dataSnapshot.child(etPhoneRegister.getText().toString()).exists()) {
                            progressDialog.dismiss();
                            FancyToast.makeText(Register.this, "Customer already exists.  Perhaps login instead?", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();

                        } else {
                            //Registration Success, adds their info the Firebase
                            progressDialog.dismiss();
                            User user = new User(etName.getText().toString(), etPasswordRegister.getText().toString());
                            table_user.child(etPhoneRegister.getText().toString()).setValue(user);
                            //Makes sure user is not an Admin
                            String isStaff = (String) dataSnapshot.child(etPhoneRegister.getText().toString()).child("isStaff").getValue();
                            user.setIsStaff(isStaff);
                            FancyToast.makeText(Register.this, "Success!  Sending you to Log In Page!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            bRegister.setVisibility(View.INVISIBLE);
                            //on Success, send user to Login page with their info filled out
                            Intent registerInfoIntent = new Intent(Register.this, Login.class);
                            registerInfoIntent.putExtra("phone", etPhoneRegister.getText().toString());
                            registerInfoIntent.putExtra("password", etPasswordRegister.getText().toString());
                            //Display Loading Image before sending user to Login
                            Glide.with(Register.this)
                                    .load(R.raw.fire_loading)
                                    .into(ivLoadingReg);

                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {

                                    // run AsyncTask here.
                                    AsyncTask.execute(() -> {
                                        //code to run in background
                                        startActivity(registerInfoIntent);
                                        finish();
                                    });
                                }
                            }, 3000);

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        //Send user to Main Screen
        Intent main = new Intent(Register.this, MainActivity.class);
        startActivity(main);
        finish();
    }
}
