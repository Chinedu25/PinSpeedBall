package com.p1nsp33dball.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameView extends View {

    private Handler handler;
    private Runnable r;

    private Ball ball;
    private ArrayList<Pin> pinArrayList;
    private Pin _playerPin;

    private long lastPressTime = System.currentTimeMillis();

    private Boolean DebugMode = false;

    private int ballSize = 180;
    private int pinSizeX = 6;
    private int pinSizeY = 85;

    private int NumberOfPinsThrown;

    private static float lastTouchX = -1;
    private static float lastTouchY = -1;
    public static boolean isTouching;

    public static int soundShoot;
    public static int soundHit;
    public static int soundFail;
    private float volume;
    private boolean loadedSound;
    public static SoundPool soundPool;

    public static float RotationSpeed = 1f;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        CollisionDetection.lastFrameTime = SystemClock.elapsedRealtime();

        initializeSound();
        initializeBall();
        initializePin();
        Update();

    }


    public static  void PlayFailSound(){
        int streamID = soundPool.play(soundFail, (float) 0.5, (float) 0.5, 1, 0, 1f );
    }

    public static void PlayShootSound(){
        int streamID = soundPool.play(soundShoot, (float) 0.25, (float) 0.25, 1, 0, 0.5f );
    }

    public static void PlayHitSound(){
        int streamID = soundPool.play(soundFail, (float) 0.25, (float) 0.25, 1, 0, 0.5f );
    }

    private void initializeSound(){

        if (Build.VERSION.SDK_INT >= 21){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        }
        else{
            this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedSound = true;
            }
        });

        soundFail = this.soundPool.load(this.getContext(), R.raw.fail, 1);
        soundShoot = this.soundPool.load(this.getContext(), R.raw.shoot, 1);
        soundHit = this.soundPool.load(this.getContext(), R.raw.hit, 1);
    }
private void createInitialPins(){
        if (MainActivity.level >2){
            //top
            pinArrayList.add(new Pin((int)ball.getPosX() + ball.getWidth()/2, (int)ball.getPosY(),
                    pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
            ));
            pinArrayList.get(pinArrayList.size()-1).hitBall = true;
        }

        if (MainActivity.level > 4){
            //left
            pinArrayList.add(new Pin((int)ball.getPosX(), (int)ball.getPosY() + ball.getHeight()/2,
                    pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
            ));
            pinArrayList.get(pinArrayList.size()-1).hitBall = true;
        }

        if (MainActivity.level > 6){
            //bottom
            pinArrayList.add(new Pin((int)ball.getPosX() + ball.getWidth()/2, (int)ball.getPosY()  + ball.getHeight(),
                    pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
            ));
            pinArrayList.get(pinArrayList.size()-1).hitBall = true;
        }

        if (MainActivity.level > 9){
            //right
            pinArrayList.add(new Pin((int)ball.getPosX() + ball.getWidth(), (int)ball.getPosY() + ball.getHeight()/2,
                    pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
            ));
            pinArrayList.get(pinArrayList.size()-1).hitBall = true;
        }






    for (int i =0 ; i < pinArrayList.size(); i++){
        pinArrayList.get(i).setSprite(BitmapFactory.decodeResource(this.getResources(), R.drawable.pin));
    }
}
    private void initializePin(){
        pinArrayList = new ArrayList<>();

  //createInitialPins();

        _playerPin = new Pin(Constants.SCREEN_WIDTH/2 - (pinSizeX * Constants.GetWidthRatio()/2),
                Constants.SCREEN_HEIGHT - (pinSizeY * Constants.GetHeightRatio()),
                pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
                );


        _playerPin.setSprite(BitmapFactory.decodeResource(this.getResources(), R.drawable.pin));

    }

    private void initializeBall() {
        ball = new Ball(Constants.SCREEN_WIDTH/2 - (ballSize * Constants.GetHeightRatio()/2) ,Constants.SCREEN_HEIGHT/2 - (ballSize * Constants.GetHeightRatio()/2),
                180 * Constants.GetHeightRatio(), ballSize * Constants.GetHeightRatio());

        ball.setSprite(BitmapFactory.decodeResource(this.getResources(), R.drawable.ball));
    }


    private void ScorePlayer(){
       MainActivity.SetScore( MainActivity.score + 1 * MainActivity.combo);
    }
    private void Update() {
        handler = new Handler();

        r = new Runnable() {
            @Override
            public void run() {

                invalidate();

            }
        };

    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (!MainActivity.gameOver){
            RotationSpeed = 1 + 0.5f * MainActivity.level;
        }
        else{
            RotationSpeed = 0;
        }

        MainActivity.SetSpeed(RotationSpeed);
        MainActivity.SetPinNum(MainActivity.GetNumberOfPins()- NumberOfPinsThrown);
        CheckCombo();

       ball.setRotationAngle(ball.getRotationAngle()- RotationSpeed);



        if (CollisionDetection.isCollidingCircle(_playerPin, ball) ){

            UpdateCombo();
            PlayHitSound();
            ScorePlayer();
            NumberOfPinsThrown += 1;


            //Log.d("here", "draw: its colliding");
            _playerPin.hitBall = true;
            Pin pin = _playerPin;
            pinArrayList.add(pin);

            _playerPin = new Pin(Constants.SCREEN_WIDTH/2 - (pinSizeX * Constants.GetWidthRatio()/2),
                    Constants.SCREEN_HEIGHT - (pinSizeY * Constants.GetHeightRatio()),
                    pinSizeX * Constants.GetWidthRatio(), pinSizeY *Constants.GetHeightRatio()
            );
            _playerPin.setSprite(BitmapFactory.decodeResource(this.getResources(), R.drawable.pin));


        }


        for (int i =0; i < pinArrayList.size(); i++){

            if (pinArrayList.get(i).hitBall){
                // DebugMode = true;
                CollisionDetection.stickToCircle(pinArrayList.get(i), ball,  -RotationSpeed);
            }

            if (_playerPin.getRect().intersect(pinArrayList.get(i).getRect())){

                _playerPin.pinSpeed = 0;
                if (!MainActivity.gameOver){
                    PlayFailSound();
                }

MainActivity.GameOver();
            }
            pinArrayList.get(i).draw(canvas);
        }
        ball.draw(canvas);
        _playerPin.draw(canvas);

        if (NumberOfPinsThrown >= MainActivity.GetNumberOfPins()){
            //advance level
           MainActivity.SetLevel( 1);
            NumberOfPinsThrown = 0;

            //clear pins
            pinArrayList.clear();
            createInitialPins();
        }


        handler.postDelayed(r, DebugMode ? 500 : 16);
    }

    private void CheckCombo(){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPressTime >= 1000) { // 1 second has passed since last press
            MainActivity.SetCombo(1);
        }

        MainActivity.HideComboUI();
    }
    private void UpdateCombo() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPressTime >= 1000) { // 1 second has passed since last press
            MainActivity.SetCombo(1);
        } else { // button was pressed in less than 1 second since last press
          MainActivity.SetCombo(MainActivity.combo +1);
        }

        lastPressTime = currentTime;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = event.getActionIndex();
                float touchX = event.getX(pointerIndex);
                float touchY = event.getY(pointerIndex);


                // Store the position of the last touch
                lastTouchX = touchX;
                lastTouchY = touchY;
                break;
            case MotionEvent.ACTION_UP:
                isTouching = false;
        }

        return true;
    }

    // Getter for the last touch position
    public static float getLastTouchX() {
        return lastTouchX;
    }
    // Getter for the last touch position
    public static float getLastTouchY() {
        return lastTouchY;
    }

}
