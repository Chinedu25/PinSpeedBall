package com.p1nsp33dball.game;


public class CollisionData {
    float collisionTime;
    float normalX;
    float normalY;

    public CollisionData(){

    }



    public CollisionData(float _collisionTime, float _normalX, float _normalY){
        this.collisionTime = _collisionTime;
        this.normalX = _normalX;
        this.normalY = _normalY;
    }
}
