package com.example.inehemias.finalprojectsocialmedia;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private StorageReference Reference;
    private FirebaseStorage st;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;
    private RecyclerView postList;

    public static final String STORAGE_REFERENCE ="gs://finalprojectsocialmedia.appspot.com/";
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef;

    String currentUserID;
    private String TAG="Debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        st =  FirebaseStorage.getInstance();
        Reference = st.getReferenceFromUrl(STORAGE_REFERENCE);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");



        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_post_button);


        drawerLayout= findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);


        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_name);


        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("username"))

                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                        Log.d(TAG, "Current user  "  + dataSnapshot.child("fullname").getValue().toString());

                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();

                        Log.d(TAG, "Current user Image "  + image);

                       Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelected(menuItem);
                return false;
            }
        });


        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToPostActivity();
            }
        });


        //DisplayAllUsersPosts();
    }

//    private void DisplayAllUsersPosts() {
//
//        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
//                        (
//                                Posts.class,
//                                R.layout.display_posts,
//                                PostsViewHolder.class,
//                                PostsRef
//                        )
//                {
//                    @NonNull
//                    @Override
//                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_posts, viewGroup, false);
//                        PostsViewHolder viewHolder = new PostsViewHolder(view);
//                        return  viewHolder;
//
//                    }
//
//                    @Override
//                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
//
//
//
//                    }
//                };
//        postList.setAdapter(firebaseRecyclerAdapter);
//
//
//    }
//
//    public static class PostsViewHolder extends RecyclerView.ViewHolder
//    {
//        View mView;
//
//        public PostsViewHolder(View itemView)
//        {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setFullname(String fullname)
//        {
//            TextView username = (TextView) mView.findViewById(R.id.post_username);
//            username.setText(fullname);
//        }
//
//        public void setProfileimage(Context ctx, String profileimage)
//        {
//            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
//            Picasso.with(ctx).load(profileimage).into(image);
//        }
//
//        public void setTime(String time)
//        {
//            TextView PostTime = (TextView) mView.findViewById(R.id.time);
//            PostTime.setText("    " + time);
//        }
//
//        public void setDate(String date)
//        {
//            TextView PostDate = (TextView) mView.findViewById(R.id.date);
//            PostDate.setText("    " + date);
//        }
//
//        public void setDescription(String description)
//        {
//            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
//            PostDescription.setText(description);
//        }
//
//        public void setPostimage(Context ctx1,  String postimage)
//        {
//            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image_update);
//
//            Picasso.with(ctx1).load(postimage).into(PostImage);
//        }
//    }




    public boolean onOptionsItemSelected(MenuItem item){
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        protected void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if(currentUser == null)
            {
                SendUserToLoginActivity();
            }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {

        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SendUserToSetupActivity() {

        Intent setupIntent = new Intent(MainActivity.this, CreateProfile.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }


    private void UserMenuSelected(MenuItem menuItem) {

        switch(menuItem.getItemId()){
            case R.id.nav_new_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:

                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();

                Toast.makeText(this, "Logout Success!",Toast.LENGTH_SHORT).show();
                SendUserToLoginActivity();
                break;

        }

    }

    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();


    }
    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);


    }

    private void SendUserToPostActivity() {

        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }






}
