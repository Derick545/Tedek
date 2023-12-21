package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class updateProfile extends AppCompatActivity {

    //Declaring Variables
    private View decorView;//Declaring view to allow app to be full screen
    //Variables for EditText information
    private EditText userName,name,surname,phonNumber;
    private CircleImageView displayPic;
    private String firstName, lastName, username, number, Date,gen,pic;
    private ImageView backBtn;
    private Uri imageUri;
    private Button updateBtn;
    private CheckBox male,female,other;
    private TextView date;
    private LottieAnimationView prog;
    private static final int REQUEST_CODE =1; //Declaration for request code for image

    //Declaring Variables for database
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private StorageReference storageReference;

    //Inheriting from model folder
    models.userInfo userinfo = new models.userInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Initialising Database variables
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        //Initialising Variables
        date = findViewById(R.id.dateTxt);
        userName = findViewById(R.id.usernameEdt);
        name = findViewById(R.id.firstNameEdt);
        surname = findViewById(R.id.lastNameEdt);
        phonNumber = findViewById(R.id.phoneNumberEdt);
        displayPic = findViewById(R.id.displayPicture);
        prog = findViewById(R.id.progress);
        male = findViewById(R.id.maleCb);
        female = findViewById(R.id.femaleCb);
        other = findViewById(R.id.otherCb);
        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backImg);

        //Hide progressbar
        prog.setVisibility(View.GONE);

        //OnClickListener for back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(updateProfile.this, viewProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });

        //Create onClickListener for date
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call method calenderDialog
                calenderDialog();
            }
        });

        //Create onClickListener for saveBtn
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call method saveUserInformation()
                saveUserInformation();
            }
        });

        //onClickListener for picture
        displayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //Calling Method get Information
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
                    Date = snapshot.child("DateOfBirth").getValue().toString();
                    gen = snapshot.child("Gender").getValue().toString();
                    pic = snapshot.child("DisplayPicture").getValue().toString();

                    //Set values to textview and imageview
                    name.setText(firstName);
                    surname.setText(lastName);
                    userName.setText(username);
                    phonNumber.setText(number);
                    date.setText(Date);
                    Picasso.get().load(pic).into(displayPic);

                    //IF statement to determine gender
                    if(gen == "Male")
                    {
                        male.isChecked();
                    }
                    else if(gen == "Female")
                    {
                        female.isChecked();
                    }
                    else if(gen == "Other")
                    {
                        other.isChecked();
                    }

                }
                else {
                    //calling method sendUserToSetupActivity()
                    Toast.makeText(updateProfile.this, "No Information Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(updateProfile.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

            }

        private void saveUserInformation() {
        //Declaring Variables
        String gender;
        userinfo.setName(name.getText().toString());
        userinfo.setUsername(userName.getText().toString());
        userinfo.setSurname(surname.getText().toString());
        userinfo.setPhoneNumber(phonNumber.getText().toString());
        userinfo.setDateOfBirth(String.valueOf(date));

        if(male.isChecked())
        {
            gender = "Male";
            userinfo.setGender(gender);
        }
        else  if(female.isChecked())
        {
            gender = "Female";
            userinfo.setGender(gender);
        } else if (other.isChecked())
        {
            gender = "Other";
            userinfo.setGender(gender);
        }
        else
        {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();
        }

        if(userinfo.getName().isEmpty() || userinfo.getName().length() < 2){
            name.setError("Name is required");
            name.requestFocus();
            return;
        } else if (userinfo.getUsername().isEmpty() || userinfo.getUsername().length() < 5)
        {
            userName.setError("Username is required");
            name.requestFocus();
            return;
        } else if(userinfo.getSurname().isEmpty() || userinfo.getSurname().length() <2)
        {
            surname.setError("Lastname is required");
            surname.requestFocus();
            return;
        }
        else if(userinfo.getPhoneNumber().isEmpty() || userinfo.getPhoneNumber().length() < 10)
        {
            phonNumber.setError("Number is required");
            return;
        }
        else if(imageUri == null)
        {
            Toast.makeText(this, "Pick an Image", Toast.LENGTH_SHORT).show();
        }

        else
        {
            storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap userMap = new HashMap();
                                        userMap.put("Username", userinfo.getUsername());
                                        userMap.put("Name", userinfo.getName());
                                        userMap.put("Surname", userinfo.getSurname());
                                        userMap.put("Gender", userinfo.getGender());
                                        userMap.put("DateOfBirth", userinfo.getDateOfBirth());
                                        userMap.put("CellNumber", userinfo.getPhoneNumber());
                                        userMap.put("DisplayPicture", uri.toString());

                                        mRef.child(mUser.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                //Hide save button
                                                updateBtn.setVisibility(View.GONE);
                                                //Display progressbar
                                                prog.setVisibility(View.VISIBLE);
                                                //create handler to delay login by 4 seconds
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //Calling method loginUser

                                                        Intent intent = new Intent(updateProfile.this,
                                                                MainActivity.class);
                                                        startActivity(intent);

                                                        prog.setVisibility(View.GONE);
                                                        updateBtn.setVisibility(View.VISIBLE);
                                                        Toast.makeText(updateProfile.this, "Information Successfully Saved", Toast.LENGTH_SHORT).show();

                                                    }
                                                }, 6000);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(updateProfile.this, "Information not saved", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
            );
        }
    }

    //Create method calenderDialog
    private void calenderDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                date.setText(String.valueOf(year) +"." +String.valueOf(month+1)+ "."+ String.valueOf(day));
            }
        }, 2023, 12, 15);

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            displayPic.setImageURI(imageUri);


        }
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