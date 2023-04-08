package com.p1nsp33dball.game;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class Ball extends GameObject{

    public Ball (int posX, int posY,  int width, int height){
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
        // Generate a new matrix based off of the current rotation and x and y coordinates.
        Matrix m = new Matrix();
        m.postRotate(rotationAngle, sprite.getWidth()/2, sprite.getHeight()/2);
        m.postTranslate(posX, posY);
        // Set the current position to the updated rotation
        transformMatrix.set(m);
    }
}
