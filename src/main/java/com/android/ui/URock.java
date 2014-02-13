package com.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Point;

import com.android.spaceship.Global;
import com.android.util.UBitmapUtil;
import com.android.util.UImageInfo;

import java.lang.Math;

/**
 * Created by ruchitsharma on 2/9/2014.
 */
public class URock extends USprite {

    public URock(Context context, int resID, RectF pos, float[] vel, float angle, float angleVel,
                 UImageInfo info) {
        super(context, resID, pos, vel, angle, angleVel, info);
        mBitmap = UBitmapUtil.loadBitmap(mContext, mResID, true);
    }

    public URock(Context context, int resID, UImageInfo info) {
        super(context, resID, null, null, 0.f, 0.f, info);
        mBitmap = UBitmapUtil.loadBitmap(mContext, mResID, true);
    }

    public void setAttributes(RectF pos, float[] vel, float angle, float angleVel, boolean alive) {
        mPos = pos;
        mVel = vel;
        mAngle = angle;
        mAngleVel = angleVel;
        mAlive = alive;
    }

    // draw itself
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mAlive) {
            canvas.save();
            canvas.rotate(mAngleVel, getCenter().x, getCenter().y);
            canvas.drawBitmap(mBitmap, null, mPos, paint);
            canvas.restore();
        }
    }

    @Override
    public void update() {
        RectF newPosition = new RectF(mPos);

        // update the angular velocity
        mAngleVel = (mAngleVel + 2.f)%360;

        // update the position
        newPosition.top += mVel[1];
        newPosition.left += mVel[0];
        newPosition.right = newPosition.left + mImageSize[0];
        newPosition.bottom = newPosition.top + mImageSize[1];

        // out of bounds?
        // bounce back from top and bottom edges
        if(newPosition.top <= 0 || newPosition.bottom >= Global.SCREEN_HEIGHT) {
            mVel[1] = -mVel[1];
            newPosition = mPos;
        }
        if (newPosition.right > Global.SCREEN_WIDTH || newPosition.left < 0) {
            // out of bounds. Lifespan over.
            mAlive = false;
        }

        mPos = newPosition;
    }

    public boolean collide(USprite object) {
        // collision will be detected based on the radius
        Point objectPosition = object.getCenter();
        Point myPosition = this.getCenter();

        double dist = Math.sqrt (
                        Math.pow(myPosition.x-objectPosition.x, 2) +
                        Math.pow(myPosition.y-objectPosition.y, 2)
                      );

        if (dist < (mImageRadius + object.getRadius())) {
            mAlive = false;
            return true;
        }

        return false;
    }

    private static final String TAG = "com.android.ui.URock";
}
