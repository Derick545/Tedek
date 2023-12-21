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

import models.itemsModel;

public class AddItems extends AppCompatActivity {

    private EditText title, author, year, description;
    private ImageView addImage,backBtn;
    private Button createBtn;
    private Uri imageUri;

    //Declaring Variables for database
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference storageReference;
    private static final int REQUEST_CODE = 1;
    private String categoryId;
    private LottieAnimationView prog;
    private View decorView;//Declaring view to allow app to be full screen
    //Inheriting from class itemsModel
    models.itemsModel items = new itemsModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });
        categoryId = getIntent().getExtras().get("categoryId").toString();
        //Initialising database
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Items");
        storageReference = FirebaseStorage.getInstance().getReference().child("ItemsImages");

        //Initialising Variables
        createBtn = (Button) findViewById(R.id.createBtn);
        title = (EditText) findViewById(R.id.titleEdt);
        author = (EditText) findViewById(R.id.authorEdt);
        year = (EditText) findViewById(R.id.yearEdt);
        description = (EditText) findViewById(R.id.descriptionEdt);
        addImage = (ImageView) findViewById(R.id.addImg);
        backBtn = (ImageView)  findViewById(R.id.backImg);
        prog = (LottieAnimationView) findViewById(R.id.progress);

        //Hide progressbar
        prog.setVisibility(View.GONE);


        //OnClickListener for back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddItems.this, ViewItems.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
                finish();
            }
        });
        //onClickListener for addImage
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //onClickListener for createBtn
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling method createCategoryItem()
                createCategoryItem();
            }
        });

    }

    private void createCategoryItem() {
        //Setting variables
        items.setTitle(title.getText().toString());
        items.setAuthor(author.getText().toString());
        items.setYear(year.getText().toString());
        items.setDescription(description.getText().toString());

        //Handling Errors
        if(items.getTitle().isEmpty() || items.getTitle().length() < 2)
        {
            title.setError("Required");
            title.requestFocus();
            return;
        }
        else if (items.getAuthor().isEmpty()) {
            author.setError("Required");
            author.requestFocus();
            return;
        }
        else if (items.getYear().isEmpty()) {
           year.setError("Required");
           year.requestFocus();
           return;
        }
        else if (items.getDescription().isEmpty()) {
            description.setError("Required");
            description.requestFocus();
            return;
        }
        else if (imageUri == null) {
            Toast.makeText(this, "Image Is Required", Toast.LENGTH_SHORT).show();
        }
        else {

            storageReference.child(mUser.getUid()).child(categoryId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        storageReference.child(mUser.getUid()).child(categoryId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> mymap = new HashMap<>();
                                mymap.put("Title", items.getTitle());
                                mymap.put("Author", items.getAuthor());
                                mymap.put("Year", items.getYear());
                                mymap.put("Image", uri.toString());
                                mymap.put("Description", items.getDescription());
                                FirebaseDatabase.getInstance().getReference().child("Items").child(mUser.getUid()).child(categoryId)
                                        .push()
                                        .setValue(mymap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Hide button
                                                createBtn.setVisibility(View.VISIBLE);
                                                //displaying progressbar
                                                prog.setVisibility(View.VISIBLE);
                                                //Creating Handler to delay
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddItems.this, "Item Created Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, 5000);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddItems.this, "Failed", Toast.LENGTH_SHORT).show();
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
            addImage.setImageURI(imageUri);
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