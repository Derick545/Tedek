package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import models.categoryModel;

public class AddCategory extends AppCompatActivity {

    //Declaring Variables
    private static final int REQUEST_CODE = 1;
    private EditText title, description, numberOfItems;
    private Button createBtn;
    private ImageView categoryImg , backBtn;
    private Uri imageUri;
    private View decorView;//Declaring view to allow app to be full screen

    //Declaring Variables for database
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference storageReference;
    private LottieAnimationView prog;

    //Inheriting
    models.categoryModel cat = new categoryModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });
        //Initialising database
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Category");
        storageReference = FirebaseStorage.getInstance().getReference().child("CategoryImages");

        //Initialising Variables
        title = findViewById(R.id.titleEdt);
        description = findViewById(R.id.descriptionEdt);
        numberOfItems = findViewById(R.id.numberOfItemsEdt);
        categoryImg = findViewById(R.id.catImg);
        backBtn = findViewById(R.id.backImg);
        createBtn = findViewById(R.id.createBtn);
        prog = findViewById(R.id.progress);

        //Hide progressbar
        prog.setVisibility(View.GONE);

        //OnClickListener for back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCategory.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //OnClickListener for create btn
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling method createCategory()
                createCategory();
            }
        });

        //OnClickListener for add category image
        categoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


    }
    //Create method create category
    private void createCategory() {
        //Setting variables
        cat.setTitle(title.getText().toString());
        cat.setDescription(description.getText().toString());
        cat.setNumberOfItems(numberOfItems.getText().toString());

        //Handling Errors
        if(cat.getTitle().isEmpty() || cat.getTitle().length() < 2)
        {
            title.setError("Required");
            title.requestFocus();
            return;
        }
        else if(cat.getDescription().isEmpty())
        {
            description.setError("Required");
            description.requestFocus();
            return;
        }
        else if(cat.getNumberOfItems() == "")
        {
            numberOfItems.setError("Required");
            numberOfItems.requestFocus();
            return;
        }
        else if(imageUri == null)
        {
            Toast.makeText(this, "Image Is Required", Toast.LENGTH_SHORT).show();
        }
        else
        {
            storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("Title", cat.getTitle());
                                map.put("Image", uri.toString());
                                map.put("Description", cat.getDescription());
                                map.put("NumberOfItems", cat.getNumberOfItems());
                                FirebaseDatabase.getInstance().getReference().child("Category").child(mUser.getUid()).push()
                                        .setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Hide button
                                                createBtn.setVisibility(View.VISIBLE);
                                                //displaying progressbar
                                                prog.setVisibility(View.VISIBLE);
                                                //create handler for delay
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddCategory.this, "Category Created Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, 5000);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddCategory.this, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                    }
                }
            });
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            categoryImg.setImageURI(imageUri);


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