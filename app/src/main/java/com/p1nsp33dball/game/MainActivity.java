package com.p1nsp33dball.game;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static int score = 0;
    public static int level = 1;
    public static int combo = 1;
    public static int pinNum = 1;
    public static float speed = 1;

    public static Boolean gameOver;


    private  static TextView levelText;
    private  static TextView speedText;
    private  static TextView scoreText;
    private  static TextView gameOverScoreText;

    private  static TextView pinNumText;
    private  static TextView comboText;

    private static View gameOverView;
    private static View gamePlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = 0;
        level = 1;
        combo = 1;
        pinNum = 1;
        speed = 1;
        gameOver =false;

        gameOverView = findViewById(R.id.GameOverUI);
        gamePlayView = findViewById(R.id.gameplayUI);

        gamePlayView.setVisibility(View.VISIBLE);
        gameOverView.setVisibility(View.GONE);

        levelText = findViewById(R.id.levelText);
        levelText.setText("LEVEL: " + level);

        scoreText = findViewById(R.id.scoreText);
        scoreText.setText("SCORE: " + score);

        gameOverScoreText = findViewById(R.id.scoreGameOver);
        gameOverScoreText.setText("SCORE: " + score);

        speedText = findViewById(R.id.speedText);
        speedText.setText("SPEED: " + speed);

        pinNumText = findViewById(R.id.pinNumText);
        pinNumText.setText(""+pinNum);

        comboText = findViewById(R.id.comboText);
        comboText.setText("COMBO: "+ combo);

        Button playAgain = findViewById(R.id.play_again);

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static int GetNumberOfPins(){
        return 2 * level + 2;
    }

    public static void SetLevel(int value){
        level = level + value;
        levelText.setText("LEVEL: " + level);
    }

    public static void SetScore(int value){
        score = value;
        scoreText.setText("SCORE: " + score);
    }

    public static void SetSpeed(float value){
        speed = value;
        speedText.setText("SPEED: " + speed);
    }

    public static void SetPinNum(int value){
        pinNum = value;
        pinNumText.setText("" + pinNum);
    }

    public static void SetCombo(int value){
        combo = value;


            HideComboUI();


        comboText.setText("COMBO: " + combo);
        scaleText(comboText);
    }

    public static void HideComboUI(){
        if (combo == 1){
            comboText.setVisibility(View.GONE);
        }
        else{
            comboText.setVisibility(View.VISIBLE);
        }

    }

    public static void GameOver(){
        gameOver = true;
        gameOverScoreText.setText("SCORE: " + score);
        gamePlayView.setVisibility(View.GONE);
        gameOverView.setVisibility(View.VISIBLE);

    }

    public static void scaleText(final TextView textView) {
        // Get the original text size
        final float originalTextSize = textView.getTextSize();

        // Scale the text by a factor of 1.5
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1.5f);
        AnimatorSet scaleAnimation = new AnimatorSet();
        scaleAnimation.playTogether(scaleX, scaleY);
        scaleAnimation.setDuration(250);

        // Bring the text back to its original position
        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(textView, "scaleX", 1f);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(textView, "scaleY", 1f);
        AnimatorSet scaleAnimationBack = new AnimatorSet();
        scaleAnimationBack.playTogether(scaleXBack, scaleYBack);
        scaleAnimationBack.setDuration(125);
        scaleAnimationBack.setStartDelay(125);

        // Create a listener to reset the text size at the end of the animation
        AnimatorListenerAdapter resetTextSize = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize);
            }
        };

        // Play the animation
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleAnimation, scaleAnimationBack);
        animatorSet.addListener(resetTextSize);
        animatorSet.start();
    }

}