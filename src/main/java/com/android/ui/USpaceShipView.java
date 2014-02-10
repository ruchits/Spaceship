package com.android.ui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import java.util.Random;

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

        // Get the screen width/height.
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Global.SCREEN_WIDTH= size.x;
        Global.SCREEN_HEIGHT = size.y;

        init();
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

        // Init background anim resources
        mBgrdBitmap = UBitmapUtil.loadBitmap(mContext, R.drawable.nebula, false);

        float[] debrisCenter = {320.f, 240.f};
        float[] debrisSize = {640.f, 480.f};
        mDebrisInfo = new UImageInfo(debrisCenter, debrisSize);
        mDebrisBitmap = UBitmapUtil.loadScaledBitmap(mContext, R.drawable.debris,
                Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);

        //create a mirror image of the debris
        mDebrisBitmapReversed = UBitmapUtil.loadBitmap(mContext, mDebrisBitmap);

        mDebrisScroll = mDebrisBitmap.getWidth();

        // spawn all rocks
        // TODO: rocks need to spawned at a certain time interval. Move this later.
        mRockList = Collections.synchronizedList(new ArrayList<USprite>());

        // create a timer that will spawn rocks at regular intervals
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawnRock();
            }
        }, 0, 2000);

    }

    private void spawnRock() {
        if(mRockList.size() >= 5)
            return;

        Log.e(TAG, "Spawning a Rock");
        // Init rocks
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        UImageInfo rockInfo = new UImageInfo(center, size, 40.f, 5.f, false);

        Random r = new Random();
        int top = r.nextInt(Global.SCREEN_HEIGHT);

        //RectF rockPos = new RectF(Global.SCREEN_WIDTH, top, Global.SCREEN_WIDTH+size[0], top+size[1]);
        RectF rockPos = new RectF(900, top, 900+size[0], top+size[1]);
        float[] rockVel = {-5.f, 1.f};
        USprite rock = new USprite(mContext, R.drawable.asteroid, rockPos, rockVel, 0, 0, rockInfo);

        synchronized (mRockList) {
            mRockList.add(rock);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // animate the background
        canvas.drawBitmap(mBgrdBitmap, null, new RectF(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT), mPaint);
        animateDebris(canvas);

        // draw the ship
        mUShip.draw(canvas, mPaint);

        // draw all rocks in the list
        synchronized(mRockList) {
            Iterator it = mRockList.iterator();
            while(it.hasNext()) {
                USprite rock = (USprite) it.next();
                rock.draw(canvas, mPaint);
                rock.update();
            }
        }

        // update position
        mUShip.update();

        invalidate();
    }

    private void animateDebris(Canvas canvas) {
        Rect fromRect1 = new Rect(0, 0, mDebrisBitmap.getWidth() - mDebrisScroll, mDebrisBitmap.getHeight());
        Rect toRect1 = new Rect(mDebrisScroll, 0, mDebrisBitmap.getWidth(), mDebrisBitmap.getHeight());

        Rect fromRect2 = new Rect(mDebrisBitmap.getWidth() - mDebrisScroll, 0, mDebrisBitmap.getWidth(), mDebrisBitmap.getHeight());
        Rect toRect2 = new Rect(0, 0, mDebrisScroll, mDebrisBitmap.getHeight());

        if (!mReverseBackroundFirst) {
            canvas.drawBitmap(mDebrisBitmap, fromRect1, toRect1, null);
            canvas.drawBitmap(mDebrisBitmapReversed, fromRect2, toRect2, null);
        }
        else {
            canvas.drawBitmap(mDebrisBitmap, fromRect2, toRect2, null);
            canvas.drawBitmap(mDebrisBitmapReversed, fromRect1, toRect1, null);
        }

        //Next value for the background's position.
        if ( (mDebrisScroll -= mAnimTime) <= 0) {
            mDebrisScroll = mDebrisBitmap.getWidth();
            mReverseBackroundFirst = !mReverseBackroundFirst;
        }
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
    private List<USprite> mRockList;
    private float[] mPrevPosition;

    private Timer mTimer;

    private Bitmap mBgrdBitmap;
    private UImageInfo mDebrisInfo;
    private Bitmap mDebrisBitmap;
    private Bitmap mDebrisBitmapReversed;
    private int mDebrisScroll;
    boolean mReverseBackroundFirst;
    private float mAnimTime = 1.f;

    private Context mContext;
    private static final String TAG = "com.android.ui.USpaceShipView";
}
