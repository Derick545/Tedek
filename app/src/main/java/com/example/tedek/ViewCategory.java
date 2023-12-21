package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewCategory extends AppCompatActivity {
    //Declaring variables
    private TextView title,numberOfItems,Description;
    private ImageView backBtn, categoryImg;
    private String titleS, numberOfItemS, descriptionS,pictureS;
    private View decorView;//Declaring view to allow app to be full screen

    //Declaring database variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private String categoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        //Get intent extras
        categoryId = getIntent().getExtras().get("categoryId").toString();
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
        mRef = FirebaseDatabase.getInstance().getReference("Category").child(mUser.getUid());

        //Initialise variables
        title = (TextView) findViewById(R.id.titleTxt);
        numberOfItems = (TextView) findViewById(R.id.numberOfItemsTxt);
        Description = (TextView) findViewById(R.id.descriptionTxt);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        categoryImg = (ImageView) findViewById(R.id.categoryImg);

        //setOnClickListener for backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCategory.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Call method getInformation()
        getInformation();

    }

    private void getInformation() {
        mRef.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    //Get values from database
                    titleS = snapshot.child("Title").getValue().toString();
                    numberOfItemS = snapshot.child("NumberOfItems").getValue().toString();
                    descriptionS = snapshot.child("Description").getValue().toString();
                    pictureS = snapshot.child("Image").getValue().toString();

                    //set values to textview and imageview
                    title.setText(titleS);
                    numberOfItems.setText(numberOfItemS);
                    Description.setText(descriptionS);
                    Picasso.get().load(pictureS).into(categoryImg); //using picasso to upload picture


                }
                else {
                    Toast.makeText(ViewCategory.this, "No Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewCategory.this, "Task Has Been Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
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
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
}