package com.android.spaceship;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.android.ui.URock;
import com.android.ui.URockPool;
import com.android.ui.UShip;
import com.android.util.UBitmapUtil;
import com.android.util.UImageInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ruchitsharma on 2/14/2014.
 */
public class GameEngine {
    public GameEngine(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        // Init a ship
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        mShipInfo = new UImageInfo(center, size, 35.f, Float.POSITIVE_INFINITY, false); // few dummy values for now

        RectF shipPos = new RectF(400, 400, 490, 490);
        float[] shipVel = {0.f, 0.f};
        mUShip = new UShip(mContext, R.drawable.double_ship, shipPos, shipVel, 0, mShipInfo);

        // Init pos
        mPrevPosition = new float[2];
        mPrevPosition[0] = shipPos.left;
        mPrevPosition[1] = shipPos.top;

        mBgrdBitmap = UBitmapUtil.loadScaledBitmap(mContext, R.drawable.nebula,
                Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);
        mBgrdScroll = mBgrdBitmap.getWidth();

        mDebrisBitmap = UBitmapUtil.loadScaledBitmap(mContext, R.drawable.debris,
                Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);
        mDebrisScroll = mDebrisBitmap.getWidth();

        // spawn all rocks
        // TODO: rocks need to spawned at a certain time interval. Move this later.
        mActiveRockList = new CopyOnWriteArrayList();
        mRockPool = new URockPool(12); // create a rock pool of size 12.

        // create a timer that will spawn rocks at regular intervals
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawnRock();
            }
        }, 0, 1000);
    }

    /*
    * spawn rocks
    */
    private void spawnRock() {
        if(mActiveRockList.size() >= 5)
            return;

        URock rock = null;

        // Init rocks
        if(!mRockPool.isEmpty()) {
            rock = mRockPool.getRock();
        }
        else {
            //Create a rock from scratch
            float[] center = {45.f, 45.f};
            float[] size = {90.f, 90.f};
            UImageInfo rockInfo = new UImageInfo(center, size, 40.f, 5.f, false);

            rock = new URock(mContext, R.drawable.asteroid, rockInfo);
        }

        Point rockSize = rock.getSize();

        Random r = new Random();
        float left = Global.SCREEN_WIDTH-rockSize.x-1;
        float top = (float) (r.nextInt(Global.SCREEN_HEIGHT - (int)rockSize.y));
        //float vel_x = -(r.nextFloat() * 5.f + 1.f);
        float vel_x = -5.f;
        float vel_y = r.nextFloat() * 2.f - 1.f;

        RectF rockPos = new RectF(left, top, left+rockSize.x, top+rockSize.y);
        float[] rockVel = {vel_x, vel_y};
        float rockAngVel = r.nextFloat() * 4.f - 2.f;

        rock.setAttributes(rockPos, rockVel, 0, rockAngVel, true);

        mActiveRockList.add(rock);
    }

    public void onDraw(Canvas canvas) {

        // animate the background
        //canvas.drawBitmap(mBgrdBitmap, null, new RectF(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT), mPaint);
        animateBackground(canvas);
        animateDebris(canvas);

        // draw the ship
        mUShip.draw(canvas, mPaint);

        // draw all rocks in the list
        Iterator it = mActiveRockList.iterator();
        while(it.hasNext()) {
            URock rock = (URock) it.next();
            rock.draw(canvas, mPaint);
            rock.update();
        }

        // Check for collision
        group_collide(mActiveRockList, mUShip);

        // update position
        mUShip.update();

        // update rocks
        updateRocks(mActiveRockList);

        //Measure frame rate (unit: frames per second).
        long now = System.currentTimeMillis();
        Log.e(TAG, framesCountAvg + " fps");
        framesCount++;
        if(now-framesTimer>1000) {
            framesTimer=now;
            framesCountAvg=framesCount;
            framesCount=0;
        }
    }

    // helper function to detect collision in groups
    private boolean group_collide(List<URock> rockList, UShip ship) {
        ArrayList<URock> removeRocks = new ArrayList<URock>();

        Iterator it = rockList.iterator();
        while(it.hasNext()) {
            URock rock = (URock) it.next();
            if (rock.collide(ship) || !rock.isAlive()) {
                removeRocks.add(rock);
                mRockPool.returnRock(rock);
            }
        }

        if (removeRocks.size() == 0) {
            return false;
        }

        rockList.removeAll(removeRocks);

        return true;
    }

    private void updateRocks(List<URock> rockList) {
        Iterator it = mActiveRockList.iterator();
        while(it.hasNext()) {
            URock rock = (URock) it.next();
            rock.update();
        }
    }

    /*
     * animate debris
     */
    private void animateDebris(Canvas canvas) {
        Rect fromRect1 = new Rect(0, 0, mDebrisBitmap.getWidth() - mDebrisScroll, mDebrisBitmap.getHeight());
        Rect toRect1 = new Rect(mDebrisScroll, 0, mDebrisBitmap.getWidth(), mDebrisBitmap.getHeight());

        Rect fromRect2 = new Rect(mDebrisBitmap.getWidth() - mDebrisScroll, 0, mDebrisBitmap.getWidth(), mDebrisBitmap.getHeight());
        Rect toRect2 = new Rect(0, 0, mDebrisScroll, mDebrisBitmap.getHeight());

        if (!mReverseDebrisFirst) {
            canvas.drawBitmap(mDebrisBitmap, fromRect1, toRect1, null);
            canvas.drawBitmap(mDebrisBitmap, fromRect2, toRect2, null);
        }
        else {
            canvas.drawBitmap(mDebrisBitmap, fromRect2, toRect2, null);
            canvas.drawBitmap(mDebrisBitmap, fromRect1, toRect1, null);
        }

        //Next value for the debris's position.
        if ( (mDebrisScroll -= mAnimDebrisTime) <= 0) {
            mDebrisScroll = mDebrisBitmap.getWidth();
            mReverseDebrisFirst = !mReverseDebrisFirst;
        }
    }

    private void animateBackground(Canvas canvas) {
        Rect fromRect1 = new Rect(0, 0, mBgrdBitmap.getWidth() - mBgrdScroll, mBgrdBitmap.getHeight());
        Rect toRect1 = new Rect(mBgrdScroll, 0, mBgrdBitmap.getWidth(), mBgrdBitmap.getHeight());

        Rect fromRect2 = new Rect(mBgrdBitmap.getWidth() - mBgrdScroll, 0, mBgrdBitmap.getWidth(), mBgrdBitmap.getHeight());
        Rect toRect2 = new Rect(0, 0, mBgrdScroll, mBgrdBitmap.getHeight());

        if (!mReverseBackroundFirst) {
            canvas.drawBitmap(mBgrdBitmap, fromRect1, toRect1, null);
            canvas.drawBitmap(mBgrdBitmap, fromRect2, toRect2, null);
        }
        else {
            canvas.drawBitmap(mBgrdBitmap, fromRect2, toRect2, null);
            canvas.drawBitmap(mBgrdBitmap, fromRect1, toRect1, null);
        }

        //Next value for the background's position.
        if ( (mBgrdScroll -= mAnimBgrdTime) <= 0) {
            mBgrdScroll = mBgrdBitmap.getWidth();
            mReverseBackroundFirst = !mReverseBackroundFirst;
        }
    }

    public boolean notifyTouch(MotionEvent event) {
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

    //Measure frames per second.
    private int framesCount = 0;
    private int framesCountAvg = 0;
    private long framesTimer = 0;


    private Paint mPaint;
    private UImageInfo mShipInfo;
    private UShip mUShip;
    private CopyOnWriteArrayList mActiveRockList;
    private URockPool mRockPool;
    private float[] mPrevPosition;

    private Timer mTimer;

    private Bitmap mBgrdBitmap;
    private UImageInfo mDebrisInfo;
    private Bitmap mDebrisBitmap;
    private Bitmap mDebrisBitmapReversed;
    private int mDebrisScroll;
    private int mBgrdScroll;
    private boolean mReverseBackroundFirst;
    private boolean mReverseDebrisFirst;
    private float mAnimDebrisTime = 5.f;
    private float mAnimBgrdTime = 1.f;

    private Context mContext;
    private static final String TAG = "com.android.ui.GameEngine";
}

