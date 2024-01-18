package com.example.earningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder;
import com.example.earningapp.fragment.fragmentReplacerActivity;

public class RedeemActivity extends AppCompatActivity {

    private ImageView amazonImage;
    private CardView amazonCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        innit();
        loadImages();
        clickListener();

    }

    private void clickListener() {

        amazonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RedeemActivity.this, fragmentReplacerActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);

            }
        });

    }

    private void loadImages() {

        String amazonGiftImageUrl = "https://toppng.com/uploads/preview/amazon-gift-card-11549868480mv0semfsfp.png";

        Glide.with(RedeemActivity.this)
                .load(amazonGiftImageUrl)
                .into(amazonImage);

    }

    private void innit(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amazonCard = findViewById(R.id.amazonGiftCard);
        amazonImage = findViewById(R.id.amazonImage);

    }

}