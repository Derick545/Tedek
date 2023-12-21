package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import Adapters.categoryAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.categoryModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Declaring Variables
    private String displayPicUri, name, surname, username;
    private TextView nameH, surnameH, usernameH;
    private CircleImageView displayPicH;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle adb;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FloatingActionButton fb;
    private TextView   usernameTxt;
    private CircleImageView userDisplayPic;
    private View decorView;//Declaring view to allow app to be full screen
    private RecyclerView recycleViewer;
    //Declaring database variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef,userRef,itemRef;
    //Declaring variables for recycle view
    FirebaseRecyclerOptions<categoryModel> options;
    //Inheriting class adapter
    Adapters.categoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });

        //Initialise Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        mRef = FirebaseDatabase.getInstance().getReference("Category").child(mUser.getUid());
        itemRef = FirebaseDatabase.getInstance().getReference("Items").child(mUser.getUid());
        //Initialising Variables
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        drawer = findViewById(R.id.draw);
        toolbar = findViewById(R.id.toolbar);
        fb = findViewById(R.id.addCategoryBtn);
        recycleViewer = findViewById(R.id.categoryList);
        nameH = header.findViewById(R.id.nameTxtH);
        surnameH = header.findViewById(R.id.surnameTxtH);
        usernameH = header.findViewById(R.id.usernameTxtH);
        displayPicH = header.findViewById(R.id.displayPicH);
        userDisplayPic = findViewById(R.id.userDisplayPic);
        usernameTxt = findViewById(R.id.userNameTxt);

        //Calling method to setToolbar()
        setToolbar();

        //SetOnClickListener for floating action button
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirecting user
                Intent intent = new Intent(MainActivity.this, AddCategory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

        //Set RecycleViewer
        String a = "a";
        String z = "z";
        recycleViewer.setLayoutManager(new LinearLayoutManager(this));
        Query query = mRef;

        FirebaseRecyclerOptions<categoryModel> options1 = new FirebaseRecyclerOptions.Builder<categoryModel>().
                setQuery(query, categoryModel.class).build();
        categoryAdapter = new categoryAdapter(options1);
        recycleViewer.setAdapter(categoryAdapter);

        //Set onClickListener for categoryAdapter recyclerView item
        categoryAdapter.setOnItemClickListener(new categoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot documentSnapShot, int position) {
                String id = documentSnapShot.getKey();

                Intent itemIntent = new Intent(MainActivity.this, ViewItems.class);
                itemIntent.putExtra("categoryId", id);
                startActivity(itemIntent);

            }
        });
        //Set onClickListener for categoryAdapter popUpMenu View item
        categoryAdapter.setViewItemClickListener(new categoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot documentSnapShot, int position) {
                //Declarations
                String id = documentSnapShot.getKey();

                Intent itemIntent = new Intent(MainActivity.this, ViewCategory.class);
                itemIntent.putExtra("categoryId", id);
                startActivity(itemIntent);

            }
        });
        //Set onClickListener for categoryAdapter popUpMenu Delete item
        categoryAdapter.setDeleteItemClickListener(new categoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot documentSnapShot, int position) {
                String id = documentSnapShot.getKey();
                mRef.child(id).removeValue();
                itemRef.child(id).removeValue();
                Toast.makeText(MainActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();

            }
        });


    }
    //Create method setToolbar()
    private void setToolbar() {
        //Set ActionBar
        setSupportActionBar(toolbar);
        //get supportActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //create adb to draw navigation view
        adb = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open,
                R.string.nav_drawer_close);
        drawer.addDrawerListener(adb);
        adb.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_home)
        {
            Toast.makeText(MainActivity.this, "You are Here", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(item.getItemId() == R.id.nav_about)
        {
                Toast.makeText(MainActivity.this, "You are Here", Toast.LENGTH_SHORT).show();
                return true;
        }
        else if(item.getItemId() == R.id.nav_settings)
        {
            Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, viewProfile.class);
            startActivity(intent);
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.nav_logout)
        {
            //Sign user out
            mAuth.signOut();
            //call method returnToLogin
            returnToLogin();
        }


        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryAdapter.startListening();
        //If statement to determine if userId equals null
        if(mUser == null)
        {
            //Calling method returnToLogin()
            returnToLogin();
        }
        else
        {
            userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        //Get values from database
                        name = snapshot.child("Name").getValue().toString();
                        surname = snapshot.child("Surname").getValue().toString();
                        username = snapshot.child("Username").getValue().toString();
                        displayPicUri = snapshot.child("DisplayPicture").getValue().toString();

                        //Set values to textview and imageview
                        nameH.setText(name);
                        surnameH.setText(surname);
                        usernameH.setText(username);
                        usernameTxt.setText(name + " " + surname);


                        Picasso.get().load(displayPicUri).into(displayPicH);
                        Picasso.get().load(displayPicUri).into(userDisplayPic);
                    }
                    else {
                        //calling method sendUserToSetupActivity()
                        sendUserToSetupActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to Load Information", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }

    //Create method to sendUserToSetupActivity()
    private void sendUserToSetupActivity() {
        Intent intent = new Intent(MainActivity.this, UserSetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //Create method to returnToLogin()
    private void returnToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    //Method to hide system bars
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}