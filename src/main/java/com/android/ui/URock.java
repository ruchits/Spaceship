package com.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Point;
import android.util.Log;

import com.android.spaceship.Global;
import com.android.util.UBitmapUtil;
import com.android.util.UImageInfo;

import java.lang.Math;

/**
 * Created by ruchitsharma on 2/9/2014.
 */
public class URock extends USprite {

    public URock(Context context, RectF pos, float[] vel, float angle,
                 float angleVel, UImageInfo info, UImageInfo expInfo) {
        super(context, pos, vel, angle, angleVel, info);
        mBitmap = UBitmapUtil.loadBitmap(mContext, info.getResID(), true);

        mExplosionTileIndex = 0;
        mExplosionInfo = expInfo;
        mExplosionBitmap = UBitmapUtil.loadBitmap(mContext, expInfo.getResID(), true);
    }

    public URock(Context context, UImageInfo info, UImageInfo expInfo) {
        super(context, null, null, 0.f, 0.f, info);
        mBitmap = UBitmapUtil.loadBitmap(mContext, info.getResID(), true);

        mExplosionTileIndex = 0;
        mExplosionInfo = expInfo;
        Log.e(TAG, "wxh= " + expInfo.getSize()[0] + "x"  + expInfo.getSize()[1]);
        Log.e(TAG, "tiles= " + expInfo.getNumTiles());
        mExplosionBitmap = UBitmapUtil.loadScaledBitmap(mContext, expInfo.getResID(),
                            (int)(expInfo.getSize()[0] * expInfo.getNumTiles()),
                            (int)expInfo.getSize()[1], true);
    }

    public void setAttributes(RectF pos, float[] vel, float angle, float angleVel, boolean alive) {
        mPos = pos;
        mVel = vel;
        mAngle = angle;
        mAngleVel = angleVel;
        mAlive = alive;
        mAnimated = false;
    }

    // draw itself
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mAlive) {
            if (mAnimated) { //exploding animation
                boolean end = animateExplosion(canvas, paint);
                mAlive = !end;
            }
            else {
                canvas.save();
                canvas.rotate(mAngleVel, getCenter().x, getCenter().y);
                canvas.drawBitmap(mBitmap, null, mPos, paint);
                canvas.restore();
            }
        }
    }

    /**
     * Animates explosion and returns true if the last tile in explosion anim
     * is done drawing.
     */
    private boolean animateExplosion(Canvas canvas, Paint paint) {
        Rect tile = new Rect((int)(mExplosionTileIndex * mExplosionInfo.getSize()[0]), 0,
                             (int)((mExplosionTileIndex + 1) * mExplosionInfo.getSize()[0]),
                             (int)(mExplosionInfo.getSize()[1]));

        canvas.drawBitmap(mExplosionBitmap, tile, mPos, paint);

        if (mExplosionTileIndex == mExplosionInfo.getNumTiles()-1) {
            mExplosionTileIndex = 0;
            mAnimated = false;
            return true;
        }
        else
            mExplosionTileIndex += 1;

        return false;
    }

    @Override
    public void update() {
        if (!mAnimated) {
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
            mAnimated = true;
            return true;
        }

        return false;
    }

    private UImageInfo mExplosionInfo;
    private Bitmap mExplosionBitmap;
    private int mExplosionTileIndex;

    private static final String TAG = "com.android.ui.URock";
}
