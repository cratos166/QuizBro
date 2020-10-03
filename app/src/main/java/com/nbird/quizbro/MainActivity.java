package com.nbird.quizbro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button startButton,helpButton,aboutUs;
    ImageView qblogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton=(Button) findViewById(R.id.startButton);
        helpButton=(Button) findViewById(R.id.helpButton);
        qblogo=(ImageView) findViewById(R.id.qblogo);
        aboutUs=(Button) findViewById(R.id.aboutUs);




        MobileAds.initialize(this);
        loadAds();










        Animation left_to_right= AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        Animation right_to_left=AnimationUtils.loadAnimation(this,R.anim.right_to_left);
        Animation alphaanim=AnimationUtils.loadAnimation(this,R.anim.aplpaanim);

        startButton.setAnimation(right_to_left);
        helpButton.setAnimation(left_to_right);
        qblogo.setAnimation(alphaanim);
        aboutUs.setAnimation(left_to_right);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentnext=new Intent(getBaseContext(),HelpActivity.class);
                startActivity(intentnext);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),CategoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),AboutUsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }
    private void loadAds(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


}