package com.example.inehemias.finalprojectsocialmedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private ImageView googleSignInButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink;
    private ProgressBar loadingBar;

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 1;
    //private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setters();

        mAuth = FirebaseAuth.getInstance();


    }



    private void setters(){

        LoginButton = findViewById(R.id.login_button);
        UserEmail =findViewById(R.id.login_email);
        UserPassword= findViewById(R.id.login_password);


    }



}
