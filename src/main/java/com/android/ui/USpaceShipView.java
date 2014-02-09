package com.android.ui;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.graphics.RectF;

import com.android.util.ImageInfo;
import com.android.spaceship.R;

/**
 * Created by ruchitsharma on 2/7/2014.
 */

/*
 * This is the 2-D view for the game.
 * It is responsible for drawing all objects in the game.
 */
public class USpaceShipView extends View {

    public USpaceShipView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    /*
     * Responsible for initializing all UI elements
     */
    private void init() {
        Log.e(TAG, "init ++");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        // Init a ship
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        mShipInfo = new ImageInfo(center, size, 35.f, 5.f, false); // few dummy values for now

        RectF shipPos = new RectF(400, 400, 490, 490);
        float[] shipVel = {0.f, 2.f};
        mShip = new Ship(mContext, R.drawable.double_ship, shipPos, shipVel, 0, mShipInfo);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw++");

        // draw the ship
        mShip.draw(canvas, mPaint);

        // update position
        mShip.update();

        //invalidate the view
        invalidate();
    }

    private Paint mPaint;
    private ImageInfo mShipInfo;
    private Ship mShip;

    private Context mContext;
    private static final String TAG = "com.android.ui.USpaceShipView";
}
