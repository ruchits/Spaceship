package com.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Matrix;

import com.android.spaceship.Global;
import com.android.util.UBitmapUtil;
import com.android.util.UImageInfo;

/**
 * Created by ruchitsharma on 2/9/2014.
 */
public class USprite {

    public USprite(Context context, int resID, RectF pos, float[] vel, float angle, float angleVel,
                   UImageInfo info) {
        mContext = context;

        mPos = pos;
        mVel = vel;
        mAngle = angle;
        mAngleVel = angleVel;
        mResID = resID;

        mImageCenter = info.getCenter();
        mImageSize = info.getSize();
        mImageRadius = info.getRadius();
        mImageLifeSpan = info.getLifespan();
        mImageIsAnimated = info.isAnimated();

        mBitmap = UBitmapUtil.loadBitmap(mContext, mResID, false);
        mMatrix = new Matrix();
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
        mMatrix.setRotate(mAngleVel);
        Bitmap bmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mMatrix, true);
        canvas.drawBitmap(bmp, null, mPos, paint);
    }

    // This method will increment ship's position based on its velocity, until
    // it reaches its target position
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
        if (newPosition.right > Global.SCREEN_WIDTH || newPosition.left < 0 ||
            newPosition.bottom > Global.SCREEN_HEIGHT || newPosition.top < 0) {
            newPosition = mPos;
        }

        mPos = newPosition;
    }

    private Context mContext;

    private int mResID;
    private Bitmap mBitmap;
    private Matrix mMatrix;

    private RectF mPos;
    private float[] mVel;

    private float mAngle;
    private float mAngleVel;

    private float[] mImageCenter;
    private float[] mImageSize;
    private float mImageRadius;
    private float mImageLifeSpan;
    private boolean mImageIsAnimated;

    private static final String TAG = "com.android.ui.USprite";
}
