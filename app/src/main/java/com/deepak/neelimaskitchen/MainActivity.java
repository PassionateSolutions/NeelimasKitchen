package com.deepak.neelimaskitchen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.shashank.sony.fancytoastlib.FancyToast;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button bLogin, bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bLogin = findViewById(R.id.bLogin);
        bRegister = findViewById(R.id.bRegister);

        //Init Paper (Similar to Shared Prefs)
        Paper.init(this);
        String user = Paper.book().read(Common.USER_KEY);
        String password = Paper.book().read(Common.PW_KEY);


            //If user has saved info then log them in automatically
        if (user != null && password != null) {
            if (!user.isEmpty() && !password.isEmpty()) {
                login(user, password);
            }
        }
    }

    //Get saved info from Paper
    private void login(final String phone, final String password) {

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        //Dialogue to show progress
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //scan Database to ensure User is in system with correct credentials
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {


                //Check if User exists in Database
                if (dataSnapshot.child(phone).exists()) {

                    progressDialog.dismiss();

                    // Get User Information
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    assert user != null;
                    user.setPhone(phone);

                    if (user.getPassword().equals(password)) {

                        //SUCCESS LOGIN
                        FancyToast.makeText(MainActivity.this, "Success!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        //Send user to Menu Screen
                        Intent menu = new Intent(MainActivity.this, Home.class);
                        Common.currentUser = user;
                        startActivity(menu);
                        finish();



                    } else {

                        progressDialog.dismiss();
                        FancyToast.makeText(MainActivity.this, "Login Failed.  Try again!", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                    }

                } else {

                    progressDialog.dismiss();
//                    FancyToast.makeText(MainActivity.this, "Welcome!  Login or Register :)", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();

                }


            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

    }

    //Login Button

    public void loginButton(View view) {

        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
        finish();

    }

    //Register Button
    public void registerButton(View view) {

        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
        finish();

    }
}
