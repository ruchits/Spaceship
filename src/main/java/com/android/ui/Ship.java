package com.android.ui;

import com.android.util.ImageInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.WindowManager;
import android.view.Display;
import android.util.Log;

/**
 * Created by ruchitsharma on 2/8/2014.
 */

/*
 * This data structure encapsulates all the info w.r.t the ship
 * entity. Each ship is responsible to maintain and update its
 * attributes. It will also draw itself on the canvas.
 */
public class Ship {

    public Ship(Context context, int resID, RectF pos, float[] vel, float angle,  ImageInfo info) {
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

        // Cache the ship bitmap
        // TODO: May have to change this later.
        // this may prove to be a bad idea, memory-wise in case we have
        // some high res bitmaps being cached across different classes
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), mResID, options);


        // Get the screen width/height.
        // TODO: Need to find the right place to store this.
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
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
        Log.e(TAG, "Drawing on= " + mPos.left + ", " + mPos.top);
        canvas.drawBitmap(mBitmap, mSourceShip, mPos, paint);
    }

    public void update() {
        RectF newPosition = new RectF(mPos);

        // update the position
        newPosition.left +=  mVel[0];
        newPosition.top += mVel[1];
        newPosition.right = newPosition.left + mImageSize[0];
        newPosition.bottom = newPosition.top + mImageSize[1];

        Log.e(TAG, "Screen= " + SCREEN_WIDTH + " x " + SCREEN_HEIGHT);
        Log.e(TAG, "New pos= " + newPosition.left + ", " + newPosition.top + ", " + newPosition.right + ", " + newPosition.bottom);
        // out of bounds?
        if(newPosition.right > SCREEN_WIDTH || newPosition.left < 0 ||
           newPosition.bottom > SCREEN_HEIGHT || newPosition.top <0) {
            newPosition = mPos;
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

    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;

    private static final String TAG = "com.android.ui.Ship";
}
