package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private ProgressBar progressBar;
    private TextView backToLoginTV;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        init();
        clickListener();

    }

    private void init(){

        registerButton = findViewById(R.id.registerBtn);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.emailInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        progressBar = findViewById(R.id.progressBar);
        backToLoginTV = findViewById(R.id.backToLoginTV);

    }

    private void clickListener(){

        backToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (name.isEmpty()){
                    nameInput.setError("Required");
                    return;
                }if (email.isEmpty()){
                    emailInput.setError("Required");
                    return;
                }if (password.isEmpty()){
                    passwordInput.setError("Required");
                    return;
                }if(confirmPassword.isEmpty() && !password.equals(confirmPassword)){
                    confirmPasswordInput.setError("error");
                    return;
                }

                createAccount(email, password);

            }
        });
    }

    private void createAccount(String email, String password){

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            updateUi(user, email);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Error",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void updateUi(FirebaseUser user, String email) {

        String refer = email.substring(0, email.lastIndexOf("@"));
        String referCode = refer.replace(".", "");

        HashMap<String, Object> map = new HashMap<>();
        map.put("name" , nameInput.getText().toString());
        map.put("email", email);
        map.put("uid", user.getUid());
        map.put("image", "");
        map.put("coins", 0);
        map.put("referCode", referCode);
        map.put("spins", 2);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date previousDate = calendar.getTime();

        String dateString = dateFormat.format(previousDate);

        FirebaseDatabase.getInstance().getReference().child("Daily Check")
                .child(user.getUid())
                .child("date")
                .setValue(dateString);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.child(user.getUid())
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Welcome here", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error:"
                                    +task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }progressBar.setVisibility(View.GONE);
                    }
                });
    }

}