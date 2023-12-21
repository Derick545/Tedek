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

public class itemViewDialog extends AppCompatActivity {

    //Declaring variables
    private TextView title,author,year,description;
    private ImageView backBtn, itemImg;
    private View decorView;//Declaring view to allow app to be full screen
    private String titleS, authorS,yearS, descriptionS,pictureS;
    //Declaring database variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef,itemRef;
    private String categoryId,itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view_dialog);
        //Get intent extras
        categoryId = getIntent().getExtras().get("categoryId").toString();
        itemId = getIntent().getExtras().get("itemKey").toString();

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
        itemRef = FirebaseDatabase.getInstance().getReference("Items").child(mUser.getUid()).child(categoryId);

        //Initialise variables
        title = (TextView) findViewById(R.id.titleTxt);
        author = (TextView) findViewById(R.id.authorTxt);
        year = (TextView) findViewById(R.id.yearTxt);
        description = (TextView) findViewById(R.id.descriptionTxt);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        itemImg = (ImageView) findViewById(R.id.categoryImg);

        //setOnClickListener for backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemViewDialog.this, ViewItems.class);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
                finish();
            }
        });

        //calling method getInformation()
        getInformation();

    }

    private void getInformation() {
        itemRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    //Get values from database
                    titleS = snapshot.child("Title").getValue().toString();
                    authorS = snapshot.child("Author").getValue().toString();
                    yearS = snapshot.child("Year").getValue().toString();
                    descriptionS = snapshot.child("Description").getValue().toString();
                    pictureS = snapshot.child("Image").getValue().toString();

                    //set values to textview and imageview
                    title.setText(titleS);
                    author.setText(authorS);
                    year.setText(yearS);
                    description.setText(descriptionS);
                    Picasso.get().load(pictureS).into(itemImg); //using picasso to upload picture
                }
                else{
                    Toast.makeText(itemViewDialog.this, "Failed to load information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(itemViewDialog.this, "Cancelled", Toast.LENGTH_SHORT).show();
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