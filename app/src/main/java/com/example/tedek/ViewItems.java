package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Adapters.categoryAdapter;
import Adapters.itemsAdapter;
import models.categoryModel;
import models.itemsModel;

public class ViewItems extends AppCompatActivity {

    //Declarations
    private TextView title;
    private String titleName;
    private ImageView backBtn;
    private FloatingActionButton addBtn;
    private String categoryId,itemId;
    private RecyclerView recyclerView;
    private View decorView;//Declaring view to allow app to be full screen
    //Declaring database variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef,userRef,itemRef,cRef;
    Adapters.itemsAdapter itemsAdapter;
    private int countItems = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });
        //get Intent extras
        categoryId = getIntent().getExtras().get("categoryId").toString();

        //Initialise Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        mRef = FirebaseDatabase.getInstance().getReference("Category").child(mUser.getUid());
        cRef = FirebaseDatabase.getInstance().getReference().child("Items");
        itemRef = FirebaseDatabase.getInstance().getReference("Items").child(mUser.getUid()).child(categoryId);
        //Initialising Variables
        title = (TextView) findViewById(R.id.titleTxt);
        backBtn = (ImageView) findViewById(R.id.backImg);
        addBtn = (FloatingActionButton) findViewById(R.id.addItem);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        //setOnClickListener for actionButton
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItems.this, AddItems.class);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
            }
        });

        //setOnClickListener for backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItems.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Calling method retrieveInformation()
        retrieveInformation();

        //Set RecycleViewer
        String a = "a";
        String z = "z";
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        Query query = itemRef;

        FirebaseRecyclerOptions<itemsModel> options = new FirebaseRecyclerOptions.Builder<itemsModel>().
                setQuery(query, itemsModel.class).build();
        itemsAdapter = new itemsAdapter(options);
        recyclerView.setAdapter(itemsAdapter);
        //setOnClickListener for adapter  delete menu
         itemsAdapter.setOnItemClickListener(new itemsAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(DataSnapshot documentSnapShot, int position) {
                itemId = documentSnapShot.getKey();

                 itemRef.child(String.valueOf(itemId)).removeValue();
                 Toast.makeText(ViewItems.this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
             }
         });
         //setOnClickListener for adapter  edit menu
         itemsAdapter.setTheItemClickListener(new itemsAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(DataSnapshot documentSnapShot, int position) {
                  String id = documentSnapShot.getKey();
                 Intent intent = new Intent(ViewItems.this, editItem.class);
                 intent.putExtra("itemKey", id);
                 intent.putExtra("categoryId", categoryId);
                 startActivity(intent);
             }
         });
        //setOnClickListener for adapter  view menu
        itemsAdapter.setViewItemClickListener(new itemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot documentSnapShot, int position) {
                String id = documentSnapShot.getKey();
                Intent intent = new Intent(ViewItems.this, itemViewDialog.class);
                intent.putExtra("itemKey", id);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemsAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        itemsAdapter.stopListening();
    }

    private void getCategoryInformation(){

    }
   private void totalNumberOfItems(){
       //Create Value event to count items
       mRef.child(mUser.getUid()).child(categoryId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists())
               {
                   //Count items per category
                   countItems = (int) snapshot.getChildrenCount();
                   //Get values from database
                  // name = snapshot.child("Name").getValue().toString();

               }
               else
               {


               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    private void retrieveInformation() {
      //Get information from database
        mRef.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    titleName = snapshot.child("Title").getValue().toString();

                    //Initialise variable
                    title.setText(titleName);
                }
                else
                {
                    Toast.makeText(ViewItems.this, "Failed to Load Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewItems.this, "Cancelled", Toast.LENGTH_SHORT).show();
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