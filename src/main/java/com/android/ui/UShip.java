package com.android.ui;

import com.android.spaceship.Global;
import com.android.util.UImageInfo;

import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint;
import com.android.util.UBitmapUtil;
import android.util.Log;

/**
 * Created by ruchitsharma on 2/8/2014.
 */

/*
 * This data structure encapsulates all the info w.r.t the ship
 * entity. Each ship is responsible to maintain and update its
 * attributes. It will also draw itself on the canvas.
 */
public class UShip extends USprite {

    public UShip(Context context, int resID, RectF pos, float[] vel, float angle, UImageInfo info) {
        super(context, resID, pos, vel, angle, 0, info);

        // get the source rect for each ship
        mSourceShip = new Rect(0, 0, 90, 90);
        mSourceShipWithThrust = new Rect(90, 0, 180, 90);

        mThrust = false;
        mBitmap = UBitmapUtil.loadBitmap(mContext, mResID, true);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!mThrust) {
            canvas.drawBitmap(mBitmap, mSourceShipWithThrust, mPos, paint);
        }
        else {
            canvas.drawBitmap(mBitmap, mSourceShip, mPos, paint);
        }
    }

    @Override
    public void update() {
        RectF newPosition = new RectF(mPos);

        // add friction if no thrust
        if (!mThrust) {
            mVel[0] *= (1 - FRICTION);
            mVel[1] *= (1 - FRICTION);
        }

        newPosition.top += mVel[1];
        newPosition.left +=  mVel[0];
        newPosition.right = newPosition.left + mImageSize[0];
        newPosition.bottom = newPosition.top + mImageSize[1];

        // out of bounds?
        if (newPosition.right > Global.SCREEN_WIDTH || newPosition.left < 0 ||
                newPosition.bottom > Global.SCREEN_HEIGHT || newPosition.top < 0) {
            newPosition = mPos;
        }

        mPos = newPosition;
    }

    public void setThrust(boolean enable) {
        if(enable) {
            mVel[0] = VELOCITY[0];
            mVel[1] = VELOCITY[1];
        }

        mThrust = enable;
    }

    public void notifyPosition(float x, float y, Boolean directionUp) {
        //boolean directionUp = (y > mPos.top) ? false : true;

        if (directionUp == null) // no change
            mVel[1] = 0.f;
        else if (directionUp)
            mVel[1] = (mVel[1] < 0.f) ? mVel[1] : -mVel[1];
        else
            mVel[1] = (mVel[1] > 0.f) ? mVel[1] : -mVel[1];
    }


    private boolean mThrust;

    private Rect mSourceShip;
    private Rect mSourceShipWithThrust;

    private static final float FRICTION = 0.05f;
    private static final float VELOCITY[] = {0.f, 10.f};

    private static final String TAG = "com.android.ui.UShip";
}
