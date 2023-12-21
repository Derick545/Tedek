package com.example.tedek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

public class Login extends AppCompatActivity {

    //Declaring Variables
    private Button login; // variable for login button
    private EditText email, password; //Edittext variables
    private TextView forgotPassword, register; //Textview Variables
    private Boolean emailVerify;
    private FirebaseAuth mAuth; //Variable for Database Authentication
    private View decorView;//Declaring view to allow app to be full screen
    private LottieAnimationView prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialising Variables
        login = findViewById(R.id.loginBtn);
        email = findViewById(R.id.emailEdt);
        password = findViewById(R.id.passwordEdt);
        forgotPassword = findViewById(R.id.forgotPasswordTxt);
        register = findViewById(R.id.registerTxt);
        prog = findViewById(R.id.progress);
        mAuth = FirebaseAuth.getInstance();

        //Hide progressbar
        prog.setVisibility(View.GONE);

        //Setting onClickListener for login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        //Setting onClickListener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
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


    }

    private void loginUser() {
        //Declaring Variables
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        //if statement to determine if fields are empty
        if(TextUtils.isEmpty(mail)){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(pass)){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }
        else{
            //using mAuth to sign in with email
            mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        //displaying progressbar
                        prog.setVisibility(View.VISIBLE);
                        //create handler to delay login by 4 seconds
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Calling method loginUser

                                verifyEmailAddress();
                            }
                        }, 6000);

                    }
                    else{
                        String message = task.getException().getMessage();
                        prog.setVisibility(View.VISIBLE);
                        //create handler to delay login by 6 seconds
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Calling method loginUser

                                Toast.makeText(Login.this, "Login Failed",
                                        Toast.LENGTH_SHORT).show();
                                prog.setVisibility(View.GONE);
                            }
                        }, 6000);

                    }
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            //Calling method to send sendUserHome
            sendUserHome();
        }
    }
    //Creating Method to hideSystemBars
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //Creating method sendUserHome
    //Creating method to send user home
    private void sendUserHome(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void verifyEmailAddress()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        emailVerify = user.isEmailVerified();

        //if statement to determine if email is verified
        if(emailVerify)
        {
            sendUserHome();
            //Displaying welcome message
            Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Please Verify Your Account", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

}