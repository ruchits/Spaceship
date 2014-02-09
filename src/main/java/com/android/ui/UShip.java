package com.android.ui;

import com.android.spaceship.Global;
import com.android.util.UImageInfo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint;
import com.android.util.UBitmapUtil;

/**
 * Created by ruchitsharma on 2/8/2014.
 */

/*
 * This data structure encapsulates all the info w.r.t the ship
 * entity. Each ship is responsible to maintain and update its
 * attributes. It will also draw itself on the canvas.
 */
public class UShip {

    public UShip(Context context, int resID, RectF pos, float[] vel, float angle, UImageInfo info) {
        mContext = context;

        mPos = pos;
        mVel = vel;
        mAngle = angle;
        mResID = resID;

        mThrust = false;
        mAngleVel = 0.f;

        mImageCenter = info.getCenter();
        mImageSize = info.getSize();
        mImageRadius = info.getRadius();

        // get the source rect for each ship
        mSourceShip = new Rect(0, 0, 90, 90);
        mSourceShipWithThrust = new Rect(90, 0, 180, 90);

        mBitmap = UBitmapUtil.loadBitmap(mContext, mResID, false);
    }

    public RectF getPosition() {
        return mPos;
    }

    public float getRadius() {
        return mImageRadius;
    }

    // draw itself
    public void draw(Canvas canvas, Paint paint) {
        //canvas.draw_image(self.image, self.image_center, self.image_size, self.pos, self.image_size, self.angle)
        canvas.drawBitmap(mBitmap, mSourceShip, mPos, paint);
    }

    public void setThrust(boolean enable) {
        mThrust = enable;
    }

    // determines how to change the direction based on the new touch location
    public void notifyPosition(float x, float y) {
        boolean directionUp = (y > mPos.top) ? false : true;

        if (directionUp)
            mVel[1] = (mVel[1] < 0.f) ? mVel[1] : -mVel[1];
        else
            mVel[1] = (mVel[1] > 0.f) ? mVel[1] : -mVel[1];
    }

    // This method will increment ship's position based on its velocity, until
    // it reaches its target position
    public void update() {
        RectF newPosition = new RectF(mPos);

        // update the position
        if (mThrust) {
            newPosition.top += mVel[1];
            newPosition.left +=  mVel[0];
            newPosition.right = newPosition.left + mImageSize[0];
            newPosition.bottom = newPosition.top + mImageSize[1];

            // out of bounds?
            if (newPosition.right > Global.SCREEN_WIDTH || newPosition.left < 0 ||
                newPosition.bottom > Global.SCREEN_HEIGHT || newPosition.top < 0) {
                newPosition = mPos;
            }
        }

        mPos = newPosition;
    }

    private Context mContext;

    private int mResID;
    private Bitmap mBitmap;

    private RectF mPos;
    private float[] mVel;
    private boolean mThrust;

    private float mAngle;
    private float mAngleVel;

    private float[] mImageCenter;
    private float[] mImageSize;
    private float mImageRadius;

    private Rect mSourceShip;
    private Rect mSourceShipWithThrust;

    private static final String TAG = "com.android.ui.UShip";
}
