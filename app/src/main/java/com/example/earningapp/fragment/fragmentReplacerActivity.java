package com.example.earningapp.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.earningapp.R;

public class fragmentReplacerActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_replacer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        frameLayout = findViewById(R.id.frameLayout);

        int position = getIntent().getIntExtra("position",0);

        if (position == 1){

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Amazon");

            fragmentReplacer(new AmazonFragment());
        }

        if (position == 2){

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Lucky Spin");

            fragmentReplacer(new LuckySpin());

        }



    }
    private void fragmentReplacer(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }
}