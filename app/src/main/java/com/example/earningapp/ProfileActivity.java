package com.example.earningapp;

//import static kotlin.PropertyReferenceDelegatesKt.getValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.earningapp.model.profileModel;
import com.google.android.gms.common.data.DataBuffer;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImageVIew;
    private TextView nameTv, emailTv, redeemHistoryTv, coinsTv, logoutTv, shareTv;
    private ImageButton imageButton;
    private Button updateBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private Uri photoUri;
    private String imageUrl;
    private ProgressDialog progressDialog;

    private static final int IMAGE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        innit();

        loadDataFromFirebase();

        clickListener();

    }

    private void loadDataFromFirebase() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
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
                                .into(profileImageVIew);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ProfileActivity.this, "Error: "
                                +error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });

    }

    private void clickListener() {

        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();

            }
        });

        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String shareBody = "Check out the best earning app. Download "+ getString(R.string.app_name)+
                            " from Play Store\n"+
                            "https://play.google.com/store/apps/details?id="+
                            getPackageName();

                    Intent intent= new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT , shareBody);
                    intent.setType("text/plain");
                    startActivity(intent);

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(ProfileActivity.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted()){

                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, IMAGE_PICKER);

                                }else {

                                    Toast.makeText(ProfileActivity.this, "Please allow permission"
                                            , Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

            }
        });
    }

    private void uploadImage(){

        if (photoUri == null)return;

        String fileName = user.getUid() + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("Images/" + fileName);

        progressDialog.show();

        storageReference.putFile(photoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                imageUrl = uri.toString();

                                uploadImageUriToDatabase();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        long totalSi = snapshot.getTotalByteCount();
                        long transferS = snapshot.getBytesTransferred();

                        long totalSize = (totalSi/1024);
                        long transferSize = (transferS/1024);

                        progressDialog.setMessage("Uploaded"+((int) transferSize) + "KB / "+ ((int) totalSize)+"KB");

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK){

            if (data != null){

                photoUri = data.getData();
                updateBtn.setVisibility(View.VISIBLE);

            }

        }

    }

    private void uploadImageUriToDatabase(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("image", imageUrl);

        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        updateBtn.setVisibility(View.GONE);
                        progressDialog.dismiss();

                    }
                });

    }

    private void innit(){
        profileImageVIew = findViewById(R.id.profileImage);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        redeemHistoryTv = findViewById(R.id.redeemHistoryTv);
        logoutTv = findViewById(R.id.logoutTv);
        profileImageVIew = findViewById(R.id.profileImage);
        imageButton = findViewById(R.id.editImage);
        coinsTv = findViewById(R.id.coinsTv);
        updateBtn = findViewById(R.id.updateButton);
        shareTv = findViewById(R.id.shareTv);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
    }
}