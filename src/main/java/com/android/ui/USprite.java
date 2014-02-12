package com.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.android.util.UBitmapUtil;
import com.android.util.UImageInfo;

/**
 * Created by ruchitsharma on 2/10/2014.
 */
abstract class USprite {

    protected USprite(Context context, int resID, RectF pos, float[] vel,
                   float angle, float angleVel, UImageInfo info) {
        mContext = context;
        mResID = resID;
        mAlive = true;

        mPos = pos;
        mVel = vel;
        mAngle = angle;
        mAngleVel = angleVel;

        mImageCenter = info.getCenter();
        mImageSize = info.getSize();
        mImageRadius = info.getRadius();
        mImageLifeSpan = info.getLifespan();
        mImageIsAnimated = info.isAnimated();

        mMatrix = new Matrix();
    }

    public RectF getPosition() {
        return mPos;
    }

    public Point getSize() { return new Point((int)mImageSize[0], (int)mImageSize[1]); }

    public float getRadius() {
        return mImageRadius;
    }

    public boolean isAlive() { return mAlive;}

    public Point getCenter() {
        Point center = new Point((int)(mPos.left+mImageSize[0]/2), (int)(mPos.top+mImageSize[1]/2));
        return center;
    }

    // draw itself
    abstract void draw(Canvas canvas, Paint paint);
    // update position and other characteristics
    abstract void update();


    protected Context mContext;

    protected int mResID;
    protected Bitmap mBitmap;
    protected Matrix mMatrix;
    protected boolean mAlive;

    public RectF mPos;
    protected float[] mVel;

    protected float mAngle;
    protected float mAngleVel;

    protected float[] mImageCenter;
    protected float[] mImageSize;
    public float mImageRadius;
    protected float mImageLifeSpan;
    protected boolean mImageIsAnimated;
}
