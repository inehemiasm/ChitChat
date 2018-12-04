package com.example.inehemias.finalprojectsocialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;



import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfile extends AppCompatActivity {
    private String TAG = "Profile Activity";

    private EditText UserName, FullName, CountryName;
    private Button save;
    private CircleImageView ProfImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UserName = findViewById(R.id.username);
        FullName = findViewById(R.id.full_name);
        CountryName = findViewById(R.id.country_text);
        save = findViewById(R.id.save_button);
        ProfImage= findViewById(R.id.set_up_profile_image);
        ProfImage = (CircleImageView) findViewById(R.id.set_up_profile_image);
        loadingBar = new ProgressDialog(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountSetupInformation();
            }
        });

        ProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image =  dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(CreateProfile.this).load(image).placeholder(R.drawable.profile).into(ProfImage);
                    }
                    else
                    {
                        Toast.makeText(CreateProfile.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

//        @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
//        {
//            Uri ImageUri = data.getData();
//                loadingBar.setTitle("Profile Image");
//                loadingBar.setMessage("Please wait, while we updating your profile image...");
//                loadingBar.show();
//                loadingBar.setCanceledOnTouchOutside(true);
//
//           // Uri resultUri = result.getUri();
//
////            StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");
////
//            assert ImageUri != null;
//            final Uri resultUri = ImageUri;
//
//                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
//
//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
//
//                    {
//                        if(task.isSuccessful())
//                        {
//                            Toast.makeText(CreateProfile.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
//
//                            final String downloadUrl1 = task.getResult().getMetadata().getPath();
//                            //final String downloadUrl = UserProfileImageRef.getDownloadUrl().toString();
//                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
//
//
//                            UsersRef.child("profileimage").setValue(result)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task)
//                                        {
//                                            if(task.isSuccessful())
//                                            {
//                                                Intent selfIntent = new Intent(CreateProfile.this, CreateProfile.class);
//                                                startActivity(selfIntent);
//
//                                                Toast.makeText(CreateProfile.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
//                                            }
//                                            else
//                                            {
//                                                String message = task.getException().getMessage();
//                                                Toast.makeText(CreateProfile.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                });
//            }
//        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();


                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please Wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(CreateProfile.this, "Success!", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String downloadUrl = uri.toString();

                                    UsersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(CreateProfile.this, CreateProfile.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(CreateProfile.this, "Image Stored Successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(CreateProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }

                                }
                            });

                                }
                            });
                        }
                    }
                });

        }
    }





    private void SaveAccountSetupInformation()
    {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(country))
        {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, Creating Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "Busy");
            userMap.put("gender", "Male");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(CreateProfile.this, "Account is created Successfully.", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message =  task.getException().getMessage();
                        Toast.makeText(CreateProfile.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(CreateProfile.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



}
