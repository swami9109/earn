package com.example.earningapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earningapp.R;
import com.example.earningapp.model.AmazonModel;
import com.example.earningapp.model.profileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AmazonFragment extends Fragment {
    private RadioGroup radioGroup;
    private Button withdrawBtn;
    private TextView coinsTv;
    DatabaseReference reference;
    FirebaseUser user;
    RadioButton amazon25, amazon50;
    String name , email;
    private Dialog dialog;
    public AmazonFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amazon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        innit(view);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        loadData();
        clickListener();

    }
    private void clickListener() {

        withdrawBtn.setOnClickListener(v -> {

            Dexter.withContext(getActivity())
                    .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if(report.areAllPermissionsGranted()){

                                String filePath = Environment.getExternalStorageDirectory()+
                                        "/Earning App/Amazon Gift Card/";

                                File file = new File(filePath);
                                file.mkdirs();

                                int currentCoins = Integer.parseInt(coinsTv.getText().toString());
                                int checkedId = radioGroup.getCheckedRadioButtonId();

                                if (checkedId == R.id.amazon25){
                                    AmazonCard(25, currentCoins);
                                } else if (checkedId == R.id.amazon50) {
                                    AmazonCard(50, currentCoins);
                                }

                            }else {
                                Toast.makeText(getContext(), "Please allow" +
                                        "the permission", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        }
                    }).check();
        });
    }
    private void AmazonCard(int amazonCard, int currentCoins){

        if (amazonCard == 25){
            if (currentCoins >= 6000){
                sendGiftCard(1);
            }else Toast.makeText(getContext(), "You don't have enough coins", Toast.LENGTH_SHORT).show();
        } else if (amazonCard == 50) {

            if (currentCoins >= 12000){
                sendGiftCard(2);
            }else Toast.makeText(getContext(), "You don't have enough coins", Toast.LENGTH_SHORT).show();
        }
    }
    DatabaseReference amazonRef;
    Query query;
    private void sendGiftCard(int cardAmount){

        dialog.show();

        amazonRef = FirebaseDatabase.getInstance().getReference().child("Gift Cards").child("Amazon");

        if (cardAmount == 1){

            query = amazonRef.orderByChild("amazon").equalTo(25);

        }else if (cardAmount == 2){

            query = amazonRef.orderByChild("amazon").equalTo(50);

        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Random random = new Random();

                int childCount = (int) snapshot.getChildrenCount();

                int rand = random.nextInt(childCount);

                Iterator iterator = snapshot.getChildren().iterator();

                for (int i = 0; i<rand; i++){
                    iterator.next();
                }
                DataSnapshot childSnap = (DataSnapshot) iterator.next();

                AmazonModel model = childSnap.getValue(AmazonModel.class);
                String id = model.getId();
                String giftCode = model.getAmazonCode();

                printAmazonCode(id, giftCode, cardAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }
    private void printAmazonCode(String id, String amazonCode, int cardAmount) {

        updateDate(cardAmount, id);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.ENGLISH);
        String currentTime= dateFormat.format(date);

        String text = "Date: "+currentTime+"\n"+
                "Name: "+name+"\n"+
                "Email: "+email+"\n"+
                "Redeem ID: "+id+"\n\n"+
                "Amazon Claim Code:" +amazonCode;

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(800, 800,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Paint paint = new Paint();

        page.getCanvas().drawText(text, 10, 12, paint);
        pdfDocument.finishPage(page);

        String filePath = Environment.getExternalStorageDirectory()+
                "/Earning App/Amazon Gift Card/"
                +System.currentTimeMillis()
                +user.getUid()+
                "amazonCode.pdf";

         File file = new File(filePath);

         try {
             pdfDocument.writeTo(new FileOutputStream(file));
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }pdfDocument.close();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(Intent.createChooser(intent, "Open with"));
        try {

        }catch (ActivityNotFoundException e){
            Toast.makeText(getContext(), "Please wait pdf", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadData() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileModel model = snapshot.getValue(profileModel.class);
                coinsTv.setText(String.valueOf(model.getCoins()));
                name = model.getName();
                email = model.getEmail();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " +error.getMessage(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

    }
    private void innit(View view) {
        radioGroup = view.findViewById(R.id.radioGroup);
        withdrawBtn = view.findViewById(R.id.submitBtn);
        coinsTv = view.findViewById(R.id.coinsTv);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

    }
    private void updateDate(int cardAmount, String id){

        HashMap<String, Object> map = new HashMap<>();
        int currentCoins = Integer.parseInt(coinsTv.getText().toString());
        if(cardAmount ==1){
            int updatedCoins = currentCoins - 6000;
            map.put("coins", updatedCoins);
        } else if (cardAmount ==2) {
            int updatedCoins = currentCoins - 12000;
            map.put("coins", updatedCoins);
        }

        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                            Toast.makeText(getContext(), "Congrats", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Gift Cards").child("Amazon")
                .child(id).removeValue();
    }
}