package com.example.earningapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earningapp.R;
import com.example.earningapp.model.profileModel;
import com.example.earningapp.spin.SpinItem;
import com.example.earningapp.spin.WheelVIew;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LuckySpin extends Fragment {
    private Button playBtn;
    private TextView coinsTv;
    private WheelVIew wheelVIew;
    List<SpinItem> spinItemList = new ArrayList<>();
    private FirebaseUser user;
    DatabaseReference reference;
    int currentSpins;

    public LuckySpin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lucky_spin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        innit(view);
        loadData();

        spinList();

        clickListener();

    }
    private void innit(View view){

        playBtn = view.findViewById(R.id.playBtn);
        wheelVIew = view.findViewById(R.id.wheelView);
        coinsTv = view.findViewById(R.id.coinsTv);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

    }
    private void spinList() {

        SpinItem item1 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFF3E0;
        spinItemList.add(item1);

        SpinItem item2 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFE0B2;
        spinItemList.add(item2);

        SpinItem item3 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFCC80;
        spinItemList.add(item3);

        SpinItem item4 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFF3E0;
        spinItemList.add(item4);

        SpinItem item5 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFE0B2;
        spinItemList.add(item5);

        SpinItem item6 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFCC80;
        spinItemList.add(item6);

        SpinItem item7 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFF3E0;
        spinItemList.add(item7);

        SpinItem item8 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFE0B2;
        spinItemList.add(item8);

        SpinItem item9 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFCC80;
        spinItemList.add(item9);

        SpinItem item10 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFF3E0;
        spinItemList.add(item10);

        SpinItem item11 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFE0B2;
        spinItemList.add(item11);

        SpinItem item12 = new SpinItem();
        item1.text = "a";
        item1.color = 0xffFFCC80;
        spinItemList.add(item12);


        wheelVIew.setData(spinItemList);
        wheelVIew.setRound(getRandCircleRound());

        wheelVIew.LuckyRoundItemSelectedListener(new WheelVIew.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                playBtn.setEnabled(true);
                playBtn.setAlpha(1f);

                String value = spinItemList.get(index - 1).text;

                updateDataFirebase(Integer.parseInt(value));
            }
        });

    }
    private void clickListener(){
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = getRandomIndex();

                if (currentSpins >=1){
                    wheelVIew.startWheelWithTargetIndex(index);
                    Toast.makeText(getActivity(), "Watch video to " +
                            "get more spins", Toast.LENGTH_SHORT).show();
                }
                if(currentSpins < 1){
                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);
                    Toast.makeText(getActivity(), "Watch video to " +
                            "get more spins", Toast.LENGTH_SHORT).show();
                }else {
                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);

                    wheelVIew.startWheelWithTargetIndex(index);
                }
            }
        });
    }
    private int getRandomIndex(){
        int[] index = new int[]{1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,4,4,4,4,5,5,5,6,6,7,7,9,9,10,10,11,12};
        int random = new Random().nextInt(index.length);
        return index[random];
    }
    private int getRandCircleRound(){

        Random random = new Random();
        return random.nextInt(10)+15;

    }
    private void loadData(){

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        profileModel model = snapshot.getValue(profileModel.class);
                        coinsTv.setText(String.valueOf(model.getCoins()));

                        currentSpins = model.getSpins();

                        String currentSpin ="Spin The Wheel"+ String.valueOf(currentSpins);
                        playBtn.setText(currentSpin);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        if (getActivity() != null){
                            getActivity().finish();
                        }
                    }
                });

    }
    private void updateDataFirebase(int reward){

        int currentCoins = Integer.parseInt(coinsTv.getText().toString());
        int updatedCoins = currentCoins + reward;

        int updatedSpins = currentSpins -1;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoins);
        map.put("spins", updatedSpins);

        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Coins added", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "Error: "
                                            +task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}