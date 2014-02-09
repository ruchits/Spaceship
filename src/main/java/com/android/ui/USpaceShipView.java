package com.android.ui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.android.spaceship.Global;
import com.android.util.UImageInfo;
import com.android.spaceship.R;
import com.android.util.UBitmapUtil;

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

        // Get the screen width/height.
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Global.SCREEN_WIDTH= size.x;
        Global.SCREEN_HEIGHT = size.y;
    }

    /*
     * Responsible for initializing all UI elements
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        // Init a ship
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        mShipInfo = new UImageInfo(center, size, 35.f, 5.f, false); // few dummy values for now

        RectF shipPos = new RectF(400, 400, 490, 490);
        float[] shipVel = {0.f, 0.f};
        mUShip = new UShip(mContext, R.drawable.double_ship, shipPos, shipVel, 0, mShipInfo);

        // Init pos
        mPrevPosition = new float[2];
        mPrevPosition[0] = shipPos.left;
        mPrevPosition[1] = shipPos.top;

        // Init rocks
        mRockInfo = new UImageInfo(center, size, 40.f, 5.f, false);

        RectF rockPos = new RectF(900, 90, 990, 180);
        float[] rockVel = {-5.f, 1.f};
        mRock = new USprite(mContext, R.drawable.asteroid, rockPos, rockVel, 0, 0, mRockInfo);

        // Init background anim resources
        mBgrdBitmap = UBitmapUtil.loadBitmap(mContext, R.drawable.nebula, false);

        float[] debrisCenter = {320.f, 240.f};
        float[] debrisSize = {640.f, 480.f};
        mDebrisInfo = new UImageInfo(debrisCenter, debrisSize);
        mDebrisBitmap = UBitmapUtil.loadBitmap(mContext, R.drawable.debris, false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // animate the background
        canvas.drawBitmap(mBgrdBitmap, null, new RectF(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT), mPaint);
        animateDebris(canvas);

        // draw the ship
        mUShip.draw(canvas, mPaint);

        // draw the rock
        mRock.draw(canvas, mPaint);

        // update position
        mUShip.update();
        mRock.update();

        invalidate();
    }

    private void animateDebris(Canvas canvas) {
        // positioned in center
        float left = (Global.SCREEN_WIDTH / 2) - (mDebrisInfo.getSize()[0] / 2);
        float top = (Global.SCREEN_HEIGHT / 2) - (mDebrisInfo.getSize()[1] / 2);

        mAnimTime += 1;
        float wtime = (mAnimTime / 4) % Global.SCREEN_WIDTH;
        canvas.drawBitmap(mDebrisBitmap, null,
                new RectF((wtime - left), top, (wtime - left + mDebrisInfo.getSize()[0]), (top + mDebrisInfo.getSize()[1])),
                mPaint);
        canvas.drawBitmap(mDebrisBitmap, null,
                new RectF((wtime + left), top, (wtime + left + mDebrisInfo.getSize()[0]), (top + mDebrisInfo.getSize()[1])),
                mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;

        if (actionCode == MotionEvent.ACTION_DOWN ||
            actionCode == MotionEvent.ACTION_MOVE) {

            Boolean motionDirectionUp = null;
            if (y > Global.SCREEN_HEIGHT/2) {
                motionDirectionUp = false;
            }
            else {
                motionDirectionUp = true;
            }

            mUShip.setThrust(true);
            mUShip.notifyPosition(x, y, motionDirectionUp);
        }
        else if (actionCode == MotionEvent.ACTION_UP) {
            mUShip.setThrust(false);
        }

        mPrevPosition[0] = x;
        mPrevPosition[1] = y;
        return true;
    }

    private Paint mPaint;
    private UImageInfo mShipInfo;
    private UShip mUShip;
    private UImageInfo mRockInfo;
    private USprite mRock;
    private float[] mPrevPosition;

    private Bitmap mBgrdBitmap;
    private UImageInfo mDebrisInfo;
    private Bitmap mDebrisBitmap;
    private float mAnimTime = 0.5f;

    private Context mContext;
    private static final String TAG = "com.android.ui.USpaceShipView";
}
