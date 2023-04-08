package com.p1nsp33dball.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Pin extends GameObject{

    public Boolean hitBall = false;

    public float pinSpeed = 60;

    public Boolean beginMove = false;
    public Pin (int posX, int posY,  int width, int height){
        super(posX,posY,width,height);
    }
    public void draw(Canvas canvas){

        update();
        canvas.drawBitmap(this.sprite, transformMatrix, null);
    }



    public void setSprite(Bitmap spp){
        this.sprite = Bitmap.createScaledBitmap(spp, width, height, true);
    }

    private void update() {

        if (GameView.isTouching){
//            posX = GameView.getLastTouchX();
//            posY = GameView.getLastTouchY();
            if (!beginMove){
                GameView.PlayShootSound();
            }
            beginMove = true;

        }

        if (beginMove && !hitBall){
            this.posY -= pinSpeed;
        }

        // Generate a new matrix based off of the current rotation and x and y coordinates.
        Matrix m = new Matrix();
       // m.preTranslate(-centerX, -centerY);
        m.preRotate(rotationAngle);
        m.postTranslate(posX, posY);
        //m.postTranslate(-centerX, -centerY);
        // Set the current position to the updated rotation
        transformMatrix.set(m);


    }

    public void UpdateMatrixPos(){
        transformMatrix.setTranslate(posX, posY);
    }
}
