package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button logInButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private TextView signUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        innit();

        auth = FirebaseAuth.getInstance();
        
        clickListener();

    }

    private void clickListener() {

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (TextUtils.isEmpty(email)){
                    emailInput.setError("Input void email");
                    return;
                }if (TextUtils.isEmpty(password)){
                    passwordInput.setError("Required");
                    return;
                }
                signIn(email, password);
            }
        });

    }

    private void signIn(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else {
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Error" +
                                    task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void innit(){

        emailInput = findViewById(R.id.LoginEmailInput);
        passwordInput = findViewById(R.id.LogInPasswordInout);
        progressBar = findViewById(R.id.logInProgressBar);
        logInButton = findViewById(R.id.logInButton);
        signUpTV = findViewById(R.id.signUp_Tv);

    }
}