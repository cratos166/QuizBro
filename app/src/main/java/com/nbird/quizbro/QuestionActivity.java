package com.nbird.quizbro;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionActivity extends AppCompatActivity {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference myRef=database.getReference();

    TextView questionTextView,scoreBoard;
    Button option1,option2,option3,option4,nextButton;
    LinearLayout linearLayout;
    private int count;
    Animation animation2;
    Animation animation3;
    private List<questionHolder> list;
    private int position=0;
    private int score=0;
    private String category;
    private int setNo;
    private Dialog loadingDialog;
    MediaPlayer correct,wrong;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Timer t;
        loadAds();

        correct=MediaPlayer.create(this,R.raw.correct);
        wrong=MediaPlayer.create(this,R.raw.wrong);

        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(40, 40);
        mediaPlayer.start();

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.activity_loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        questionTextView=findViewById(R.id.questionTextView);
        scoreBoard=findViewById(R.id.scoreBoard);
        option1=(Button) findViewById(R.id.option1);
        option2=(Button) findViewById(R.id.option2);
        option3=(Button) findViewById(R.id.option3);
        option4=(Button) findViewById(R.id.option4);
        nextButton=(Button) findViewById(R.id.nextButton);
        linearLayout=(LinearLayout) findViewById(R.id.linearLayout);


        category=getIntent().getStringExtra("category");
        setNo=getIntent().getIntExtra("setNo",1);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.optionanim);
        Animation animation1= AnimationUtils.loadAnimation(this,R.anim.optionanim1);
        animation2= AnimationUtils.loadAnimation(this,R.anim.nextbuttonanim);
        animation3= AnimationUtils.loadAnimation(this,R.anim.questionandscoreanim);

        scoreBoard.setAnimation(animation3);
        nextButton.setAnimation(animation2);


        list=new ArrayList<>();
        loadingDialog.show();
        myRef.child("SETS").child(category).child("questions").orderByChild("sets").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    list.add(snapshot1.getValue(questionHolder.class));
                }
                if(list.size()>0){
                    for(int i=0;i<4;i++){
                        linearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkAnswer((Button)view);
                            }
                        });
                    }
                    playAnim(questionTextView,0,list.get(position).getQuestionTextView());

                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextButton.setEnabled(false);
                            nextButton.setAlpha(0.7f);
                            enableOption(true);
                            position++;

                            if(position==list.size()){
                                Intent scoreIntent=new Intent(QuestionActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("totalScore",list.size());
                                startActivity(scoreIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                                return;
                            }
                            count=0;
                            playAnim(questionTextView,0,list.get(position).getQuestionTextView());

                        }
                    });
                }else {
                    finish();
                    Toast.makeText(QuestionActivity.this, "No Questions", Toast.LENGTH_SHORT).show();

                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }
    private void playAnim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(value==0 && count<4){
                    String option="";
                    if(count==0){
                        option=list.get(position).getOption1();
                        linearLayout.getChildAt(0).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#269BFF")));
                    }else if(count==1){
                        option=list.get(position).getOption2();
                        linearLayout.getChildAt(1).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#269BFF")));
                    }else if(count==2){
                        option=list.get(position).getOption3();
                        linearLayout.getChildAt(2).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#269BFF")));
                    }else if(count==3){
                        option=list.get(position).getOption4();
                        linearLayout.getChildAt(3).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#269BFF")));
                    }
                    playAnim(linearLayout.getChildAt(count),0,option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        scoreBoard.setText("Question "+(position+1)+"/"+list.size());
                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void checkAnswer(Button selectedOption){
        enableOption(false);
        nextButton.setEnabled(true);
        nextButton.setAlpha(1);
        if(selectedOption.getText().toString().equals(list.get(position).getCorrectAnswer())){
            //correct
            wrong.start();
           selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B1FF88")));
            score++;
        }else {
            //incorrect
            correct.start();
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF8888")));
            Button correctOption = (Button) linearLayout.findViewWithTag(list.get(position).getCorrectAnswer());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B1FF88")));
        }
        }

    private void enableOption(boolean enable){
        for (int i=0;i<4;i++) {
            linearLayout.getChildAt(i).setEnabled(enable);
            if (enable) {
                linearLayout.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E4E4E4")));
            }
        }
    }

    private void loadAds(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    @Override
    protected void onPause() {
        if (this.isFinishing()){ //basically BACK was pressed from this activity
            mediaPlayer.stop();
        }
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                mediaPlayer.stop();

            }
        }
        super.onPause();
    }

}