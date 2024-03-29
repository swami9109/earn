package com.example.earningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.loader.content.AsyncTaskLoader;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.earningapp.fragment.fragmentReplacerActivity;
import com.example.earningapp.model.profileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CardView dailyCheckCard, luckyCard, taskCard, referCard, watchCard,aboutCard, redeemCard;
    private CircleImageView profileImage;
    private TextView coinsTv, nameTv, emailTv;
    Toolbar toolbar;
    DatabaseReference reference;
    private FirebaseUser user;
    private Dialog dialog;
    Internet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        innit();
        internet = new Internet(MainActivity.this);
        setSupportActionBar(toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        getDataFromDatabase();

        clickListener();

    }
    private void clickListener(){

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        referCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InviteActivity.class));
            }
        });

        dailyCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyCheck();
            }
        });

        redeemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RedeemActivity.class));

            }
        });

        luckyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, fragmentReplacerActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
            }
        });

    }
    private void checkInternetConnection(){

        if (internet.isConnected()){
            new isInternetActive().execute();
        }else Toast.makeText(this, "Please check your internet"
                , Toast.LENGTH_SHORT).show();

    }
    private void dailyCheck() {

        if (internet.isConnected()) {

            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Please Wait");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

            final Date currentDate = Calendar.getInstance().getTime();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            reference.child("Daily Check").child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                String dbDataString = snapshot.child("date").getValue(String.class);

                                try {
                                    assert dbDataString != null;
                                    Date dbDate = dateFormat.parse(dbDataString);

                                    String xDate = dateFormat.format(currentDate);
                                    Date date = dateFormat.parse(xDate);

                                    if (date.after(dbDate) && date.compareTo(dbDate) != 0) {

                                        reference.child("Users").child(user.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        profileModel model = snapshot.getValue(profileModel.class);

                                                        int currentCoins = model.getCoins();
                                                        int update = currentCoins + 10;

                                                        int spinC = model.getSpins();
                                                        int updatedSpins = spinC + 2;

                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("coins", update);
                                                        map.put("spins", updatedSpins);

                                                        reference.child("Users").child(user.getUid())
                                                                .updateChildren(map);

                                                        Date newDate = Calendar.getInstance().getTime();
                                                        String newDateString = dateFormat.format(newDate);

                                                        HashMap<String, String> dateMap = new HashMap<>();
                                                        dateMap.put("date", newDateString);

                                                        reference.child("Daily Check").child(user.getUid()).setValue(dateMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                                        sweetAlertDialog.setTitleText("Succx ess");
                                                                        sweetAlertDialog.setContentText("Coins added to your account successfully");
                                                                        sweetAlertDialog.setConfirmButton("Disiss", new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                                sweetAlertDialog.dismissWithAnimation();
                                                                            }
                                                                        }).show();
                                                                    }
                                                                });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(MainActivity.this, "Error: "
                                                                + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitle("Failed");
                                        sweetAlertDialog.setContentText("You have already rewarded, come back tomorrow");
                                        sweetAlertDialog.setConfirmButton("Dismiss", null);
                                        sweetAlertDialog.show();
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    sweetAlertDialog.dismissWithAnimation();
                                }

                            } else {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("System Busy");
                                sweetAlertDialog.setContentText("System is busy, please try again later");
                                sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                                sweetAlertDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error: "
                                    + error.getMessage(), Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
        }else {
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }

    }
    private void innit(){

        dailyCheckCard = findViewById(R.id.dailyCheckCard);
        luckyCard = findViewById(R.id.luckySpinCard);
        taskCard = findViewById(R.id.taskCard);
        referCard = findViewById(R.id.referCard);
        watchCard = findViewById(R.id.watchCard);
        aboutCard = findViewById(R.id.aboutCard);
        profileImage = findViewById(R.id.userImage);
        coinsTv = findViewById(R.id.coins);
        toolbar = findViewById(R.id.toolbar);
        nameTv = findViewById(R.id.userName);
        emailTv = findViewById(R.id.userEmail);
        redeemCard = findViewById(R.id.withdrawCard);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

    }
    private void getDataFromDatabase() {

        dialog.show();

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                profileModel model = snapshot.getValue(profileModel.class);
                nameTv.setText(model.getName());
                emailTv.setText(model.getEmail());
                coinsTv.setText(String.valueOf(model.getCoins()));

                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .timeout(6000)
                        .placeholder(R.drawable.profile)
                        .into(profileImage);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();

                Toast.makeText(MainActivity.this, "Error: "+error.getMessage()
                        , Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }
    class isInternetActive extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream = null;
            String json = "";

            try {

                String strURL = "https://icons.iconarchive.com/icons/martz90/circle/256/android-icon.png";
                URL url = new URL(strURL);

                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                inputStream = urlConnection.getInputStream();
                json = "success";

            }catch (Exception e){
                e.printStackTrace();
                json = "failed";
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){

                if (s.equals("success")){
                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Validating internet", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }
    }
}