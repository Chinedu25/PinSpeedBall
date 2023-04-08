package com.p1nsp33dball.game;

import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.material.color.utilities.MathUtils;

public class CollisionDetection {
    private CollisionData collisionData;
    public static float lastFrameTime = 0;

    private static String TAG = "CollisionCheck";

    private GameObject tempGO;

    public CollisionDetection() {
        collisionData = new CollisionData();
    }

    public CollisionData getCollisionData() {
        return this.collisionData;
    }

    public void setCollisionData(CollisionData colData) {
        this.collisionData = colData;
    }

    public Boolean isColliding(GameObject go1, GameObject go2){
        float left = go2.getPosX() - (go1.getPosX()+go1.getWidth());
        float top = (go2.getPosY() + go2.getHeight()) - go1.getPosY();
        float right = (go2.getPosX() + go2.getWidth()) - go1.getPosX();
        float bottom = go2.getPosY() - (go1.getPosY()+go1.getHeight());

        return !(left > 0 || right < 0 || top < 0 || bottom > 0);
    }

    CollisionData MySweptAABB(GameObject go1, GameObject go2, float go1SpeedX, float go1SpeedY, float go2SpeedX, float go2SpeedY){

        // Calculate the relative speeds of go1 and go2
        float relSpeedX = go1SpeedX - go2SpeedX;

        tempGO = new GameObject();

        tempGO.setPosX(relSpeedX > 0 ? go1.getPosX() : go1.getPosX() + relSpeedX);
        tempGO.setPosY(go1SpeedY > 0 ? go1.getPosY() : go1.getPosY() + go1SpeedY);
        tempGO.setHeight((int)(go1.getHeight() + Math.abs(go1SpeedY)));
        tempGO.setWidth((int)(go1.getWidth() + Math.abs(relSpeedX)));

        if (!isColliding(tempGO, go2)){
            return new CollisionData(1.0f, 0.0f, 0.0f);
        }

        float dxEntry, dxExit;
        float dyEntry, dyExit;

        if (relSpeedX > 0.0f){
            dxEntry = Math.min(go2.getPosX(),go2.getPosX() - go2SpeedX) - Math.max((go1.getPosX() + go1.getWidth()), (go1.getPosX() + go1.getWidth())-go1SpeedX) ;
            dxExit = Math.max(go2.getPosX() + go2.getWidth(),go2.getPosX() -go2SpeedX+ go2.getWidth()) - Math.min(go1.getPosX(), go1.getPosX()-go1SpeedX);
        }
        else{

            dxEntry = Math.max(go2.getPosX() + go2.getWidth(),go2.getPosX() -go2SpeedX+ go2.getWidth()) -Math.min(go1.getPosX(), go1.getPosX()-go1SpeedX);
            dxExit = Math.min(go2.getPosX(),go2.getPosX() - go2SpeedX) - Math.max((go1.getPosX() + go1.getWidth()), (go1.getPosX() + go1.getWidth())-go1SpeedX);
        }
        if (go1SpeedY > 0.0f){
            dyEntry = go2.getPosY() - (go1.getPosY() + go1.getHeight());
            dyExit = (go2.getPosY() + go2.getHeight()) - go1.getPosY();
        }
        else{

            dyEntry = (go2.getPosY() + go2.getHeight()) - go1.getPosY();
            dyExit = go2.getPosY() - (go1.getPosY() + go1.getHeight());
        }

        float txEntry, tyEntry;
        float txExit, tyExit;

        if (go1SpeedX == 0.0f) {
            txEntry = Float.NEGATIVE_INFINITY;
            txExit = Float.POSITIVE_INFINITY;
        } else {
            txEntry = dxEntry / (relSpeedX);
            txExit = dxExit / (relSpeedX);
        }

        if (go1SpeedY == 0.0f) {
            tyEntry = Float.NEGATIVE_INFINITY;

            tyExit = Float.POSITIVE_INFINITY;
        } else {
            tyEntry = dyEntry /go1SpeedY;
            tyExit = dyExit / go1SpeedY;
        }

        float entryTime = Math.max(txEntry, tyEntry);
        float exitTime = Math.min(txExit, tyExit);

        float normalX, normaly;

        if (entryTime > exitTime || (txEntry < 0.0f && tyEntry < 0.0f) || txEntry > 1.0f || tyEntry > 1.0f) {

            return new CollisionData(1.0f, 0.0f, 0.0f);
        }
        else {
            if (txEntry > tyEntry) {
                if (dxEntry < 0.0f) {
                    normalX = 1.0f;
                    normaly = 0.0f;
                } else {
                    normalX = -1.0f;
                    normaly = 0.0f;
                }
            } else {
                if (dyEntry < 0.0f) {
                    normalX = 0.0f;
                    normaly = 1.0f;
                } else {
                    normalX = 0.0f;
                    normaly = -1.0f;
                }
            }
        }

        return new CollisionData(entryTime, normalX, normaly);

    }

    public float getRemainingTime(float collisionTime){
        float remainingTime = 1.0f - collisionTime;
        return remainingTime;
    }

    public float getSlideX(float speedX, float speedY, float normalx, float normaly, float remainingtime){
        float dotprod = (speedX * normaly + speedY * normalx) * remainingtime;
        return dotprod * normaly;
    }

    public float getSlideY(float speedX, float speedY, float normalx, float normaly, float remainingtime){
        float dotprod = (speedX * normaly + speedY * normalx) * remainingtime;
        return dotprod * normalx;
    }
    public static boolean isCollidingCircle(GameObject rect, GameObject circle) {
        float circleCenterX = circle.getPosX() + circle.getWidth() / 2;
        float circleCenterY = circle.getPosY() + circle.getHeight() / 2;
        float circleRadius = circle.getWidth() / 2;

        // Find the distance between the center of the circle and the center of the rectangle
        float dx = circleCenterX - (rect.getPosX() + rect.getWidth() / 2);
        float dy = circleCenterY - (rect.getPosY() + rect.getHeight() / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Check for no collision
        if (distance > circleRadius + Math.max(rect.getWidth(), rect.getHeight()) / 2) {
            return false;
        }

        // Determine the closest point on the rectangle to the center of the circle
        float closestX = Math.max(rect.getPosX(), Math.min(circleCenterX, rect.getPosX() + rect.getWidth()));
        float closestY = Math.max(rect.getPosY(), Math.min(circleCenterY, rect.getPosY() + rect.getHeight()));

        // Find the distance between the closest point on the rectangle and the center of the circle
        dx = closestX - circleCenterX;
        dy = closestY - circleCenterY;
        distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Check for collision
        if (distance < circleRadius) {
            return true;
        }

        return false;
    }
public static void stickToCircle(GameObject rect, GameObject circle, float speed) {
    float circleCenterX = circle.getPosX() + circle.getWidth() / 2 ;
    float circleCenterY = circle.getPosY() + circle.getHeight() / 2 ;
    float circleRadius = circle.getWidth()/2;

    // Calculate the angle from the object's current position to the target point
    float dx = rect.getPosX() - circleCenterX;
    float dy = rect.getPosY() - circleCenterY;

    float w =(float)( speed/360 * 2 * Math.PI);

    float velocity = w * circleRadius;

    float angle = (velocity) / circleRadius;
    float newX = (float) (circleCenterX + (dx * Math.cos(angle)) - (dy * Math.sin(angle)));
    float newY = (float) (circleCenterY + (dx * Math.sin(angle)) + (dy * Math.cos(angle)));

    rect.setPosX(newX);
    rect.setPosY(newY);

    float dxx = circleCenterX - rect.getPosX();
    float dyy = circleCenterY -  rect.getPosY();

        double mangle = Math.toDegrees(Math.atan2(dyy, dxx));
        rect.setRotationAngle((float)(mangle-270));
}


    public static float lerp(float start, float end, float t) {
        return (1 - t) * start + t * end;
    }
}

