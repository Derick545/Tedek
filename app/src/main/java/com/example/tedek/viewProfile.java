package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class viewProfile extends AppCompatActivity {

    //Declaring Variables
    private TextView userName,name,surname,phoneNumber,dateOfBirth,gender;
    private String firstName, lastName, username, number, date,gen,pic;
    private CircleImageView displayPic;
    private ImageView backBtn;
    private Button updateBtn;
    private View decorView;//Declaring view to allow app to be full screen
    //Declaring Variables for database
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private StorageReference storageReference;
    models.userInfo userinfo = new models.userInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
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
        mRef = FirebaseDatabase.getInstance().getReference("Users");

        //Initialise Variables
        userName = (TextView) findViewById(R.id.usernameTxt);
        name = (TextView) findViewById(R.id.firstNameTxt);
        surname = (TextView) findViewById(R.id.lastNameTxt);
        phoneNumber = (TextView) findViewById(R.id.numberTxt);
        dateOfBirth  = (TextView) findViewById(R.id.dateTxt);
        gender = (TextView) findViewById(R.id.genderTxt);
        displayPic = (CircleImageView) findViewById(R.id.profileImg);
        backBtn = (ImageView) findViewById(R.id.backImg);
        updateBtn = (Button) findViewById(R.id.updateBtn);

        //OnClickListener for backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewProfile.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        //setOnClickListener for backBtn
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewProfile.this, updateProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //Calling method getInformation()
        getInformation();

    }

    private void getInformation() {
        mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    //Get values from database
                    firstName = snapshot.child("Name").getValue().toString();
                    lastName = snapshot.child("Surname").getValue().toString();
                    username = snapshot.child("Username").getValue().toString();
                    number  = snapshot.child("CellNumber").getValue().toString();
                    date = snapshot.child("DateOfBirth").getValue().toString();
                    gen = snapshot.child("Gender").getValue().toString();
                    pic = snapshot.child("DisplayPicture").getValue().toString();

                    //Set values to textview and imageview
                    name.setText(firstName);
                    surname.setText(lastName);
                    userName.setText(username);
                    phoneNumber.setText(number);
                    dateOfBirth.setText(date);
                    gender.setText(gen);

                    Picasso.get().load(pic).into(displayPic);

                }
                else {
                    //calling method sendUserToSetupActivity()
                    Toast.makeText(viewProfile.this, "No Information Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(viewProfile.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
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