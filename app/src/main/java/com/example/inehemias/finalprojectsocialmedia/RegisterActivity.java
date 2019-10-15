package com.example.inehemias.finalprojectsocialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText Useremail, Userpassword, confirmation;
    private Button register;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Useremail=findViewById(R.id.email_edit);
        Userpassword=findViewById(R.id.password_edit);
        confirmation= findViewById(R.id.confirm_password);
        register= findViewById(R.id.register_button);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAcoount();

            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void CreateAcoount() {

        String email= Useremail.getText().toString();
        String password = Userpassword.getText().toString();
        String confirm = confirmation.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Please Enter A valid email", Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_LONG).show();

        } else if(TextUtils.isEmpty(confirm)){
            Toast.makeText(RegisterActivity.this, "Please Confirm Password", Toast.LENGTH_LONG).show();

        }

        else if (!password.equals(confirm)){

            Toast.makeText(RegisterActivity.this, "Password Does not Match", Toast.LENGTH_LONG).show();
        }

        else {
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            Toast.makeText(RegisterActivity.this, "Creating Account", Toast.LENGTH_LONG).show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        SendUserToSetupActivity();

                        Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }

    }
    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, CreateProfile.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
