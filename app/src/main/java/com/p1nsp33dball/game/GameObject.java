package com.p1nsp33dball.game;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class GameObject {
    protected float posX, posY;
    protected int width, height;
    protected Rect rect; //for collision detection
    protected Bitmap sprite;
    protected float rotationAngle; //in degrees

    protected Matrix transformMatrix;

    public float minX, minY, maxX, maxY;

    public  GameObject (){
        this.transformMatrix = new Matrix();
    }

    public GameObject(float posX, float posY, int width, int height, Bitmap sprite) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.minX = posX;
        this.minY = posY;
        this.maxX = posX + width;
        this.maxY = posY + height;

        this.rotationAngle = 0;
        this.transformMatrix = new Matrix();
    }

    public GameObject(float posX, float posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;

        this.minX = posX;
        this.minY = posY;
        this.maxX = posX + width;
        this.maxY = posY + height;

        this.rotationAngle = 0;
        this.transformMatrix = new Matrix();
    }

    public void updateMinMax(){
        this.minX = posX;
        this.minY = posY;
        this.maxX = posX + width;
        this.maxY = posY + height;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }

    public Rect getRect() {
        return new Rect((int)this.posX,(int)this.posY, (int)this.posX+this.width, (int)this.posY+this.height);
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}

