package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Register extends AppCompatActivity {

    //Declaring Variables
    private View decorView;//Declaring view to allow app to be full screen
    //Variables for EditText information
    private EditText email, password, confirmPass;
    private Button registerBtn;
    private TextView signIn;

    //Declaring Variables for database
    private FirebaseAuth mAuth;
    private LottieAnimationView prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialising Database variables
        mAuth = FirebaseAuth.getInstance();


        //Initialising Variables
        email = findViewById(R.id.emailEdt);
        password = findViewById(R.id.passwordEdt);
        confirmPass = findViewById(R.id.confirmPasswordEdt);
        registerBtn = findViewById(R.id.registerBtn);
        signIn = findViewById(R.id.loginTxt);
        prog = findViewById(R.id.progress);
        //Using decoView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                {decorView.setSystemUiVisibility(hideSystemBars());}
            }
        });

        //Hide progressbar
        prog.setVisibility(View.GONE);

        //set OnClickListener for register button

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling method createNewUser
                createNewUser();
            }
        });

        //set OnclickListener for loginTxt
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, UserSetupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    //Creating method createNewUser()
    private void createNewUser(){
        //Declaring variables
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String confirmP = confirmPass.getText().toString();

        //if statement for exception handling
        if(TextUtils.isEmpty(mail))
        {
            Toast.makeText(Register.this, "Email Address Required", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Enter valid email address ");
            email.requestFocus();
        }
        else if(TextUtils.isEmpty(pass) || pass.length() < 5)
        {
            Toast.makeText(Register.this, "Password Required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmP)){
            Toast.makeText(this, "Confirm Password", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirmP))
        {
            Toast.makeText(this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Authenticating user with email and password
            mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        //Hide progressbar
                        prog.setVisibility(View.VISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //calling method sendVerificationMessage()
                                sendVerificationMessage();

                                prog.setVisibility(View.GONE);
                            }
                        }, 6000);


                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendVerificationMessage()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Register.this, "Successfully Registered, Verify your Account", Toast.LENGTH_SHORT).show();
                        sendUserToLoginActivity();
                        mAuth.signOut();
                    }
                    else {
                        Toast.makeText(Register.this, "Failed to register", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    //Creating method to send user to setup activity
    private void sendUserToLoginActivity(){
        Intent intent = new Intent(Register.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}