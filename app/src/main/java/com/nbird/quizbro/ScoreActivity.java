package com.nbird.quizbro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ScoreActivity extends AppCompatActivity {
    TextView yourScore,totalScore,scoreText;
    Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        loadAds();

        yourScore=findViewById(R.id.yourScore);
        totalScore=findViewById(R.id.totalScore);
        scoreText=findViewById(R.id.scoreText);
        doneButton=(Button) findViewById(R.id.doneButton);


        Animation animation= AnimationUtils.loadAnimation(this,R.anim.aplpaanim);
        Animation animation1= AnimationUtils.loadAnimation(this,R.anim.optionanim1);
        Animation animation2= AnimationUtils.loadAnimation(this,R.anim.nextbuttonanim);
        Animation animation3= AnimationUtils.loadAnimation(this,R.anim.scoreanim);

        yourScore.setAnimation(animation);
        totalScore.setAnimation(animation1);
        scoreText.setAnimation(animation);
        doneButton.setAnimation(animation2);

        yourScore.setText(String.valueOf(getIntent().getIntExtra("score",0)));
        totalScore.setText("Out Of"+" "+String.valueOf(getIntent().getIntExtra("totalScore",0)));


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void loadAds(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}