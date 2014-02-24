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
import android.os.AsyncTask;

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

        // Set current level to easy.
        CURRENT_LEVEL = LEVEL_EASY;

        // Init a ship
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        mShipInfo = new UImageInfo(R.drawable.double_ship, center,
                        size, 35.f, Float.POSITIVE_INFINITY, false, 0); // few dummy values for now

        RectF shipPos = new RectF(400, 400, 490, 490);
        float[] shipVel = {0.f, 0.f};
        mUShip = new UShip(mContext, shipPos, shipVel, 0, mShipInfo);

        // Create background resources
        mBgrdBitmap = UBitmapUtil.loadScaledBitmap(mContext, R.drawable.nebula,
                Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);
        mBgrdScroll = mBgrdBitmap.getWidth();

        mDebrisBitmap = UBitmapUtil.loadScaledBitmap(mContext, R.drawable.debris,
                Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);
        mDebrisScroll = mDebrisBitmap.getWidth();
        mDebrisReversedBitmap = mDebrisBitmap;

        // initialize a set of rocks
        mRockPool = new URockPool(INIT_NUM_ROCKS);
        for (int i = 0; i < INIT_NUM_ROCKS; i++) {
            URock rock = createRock();
            mRockPool.returnRock(rock);
        }
        mActiveRockList = new CopyOnWriteArrayList();

        setRocksDodged(0);

        // create a timer that will spawn rocks at regular intervals
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int low, high;

                switch(CURRENT_LEVEL) {
                    case LEVEL_EASY:
                        low = 1;
                        high = 2;
                        break;
                    case LEVEL_MEDIUM:
                        low = 2;
                        high = 3;
                        break;
                    case LEVEL_HARD:
                        low = 3;
                        high = 4;
                        break;
                    default:
                        low = 1;
                        high = 2;
                }
                spawnRocks(low, high);
            }
        }, 0, 1000);
    }

    /*
     * helper method to create a rock
     */
    private URock createRock() {
        //Create a rock from scratch
        float[] center = {45.f, 45.f};
        float[] size = {90.f, 90.f};
        UImageInfo rockInfo = new UImageInfo(R.drawable.asteroid, center,
                size, 40.f, 5.f, false, 0);

        // setup explosion info
        float[] expCenter = {64.f, 64.f};
        float[] expSize = {128.f, 128.f};
        UImageInfo explosionInfo = new UImageInfo(R.drawable.explosion_blue, expCenter,
                expSize, 17.f, 24, true, 24);

        return new URock(mContext, rockInfo, explosionInfo);
    }

    /*
    * spawn rocks - number is chosen randomly between {low, high}
    */
    private void spawnRocks(int low, int high) {
        // let's not spam the screen
        if(mActiveRockList.size() >= MAX_NUM_ROCKS)
            return;

        // choose at random how many do we want to span
        Random r = new Random();
        int numRocks = Math.round(r.nextFloat() * (high - low) + low);

        URock rock = null;
        for (int i = 0; i < numRocks; i++) {
            // Init rocks
            if(!mRockPool.isEmpty()) {
            rock = mRockPool.getRock();
            }
            else {
                rock = createRock();
            }

            Point rockSize = rock.getSize();
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
    }

    public void onDraw(Canvas canvas) {

        // animate the background
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
        int numRocksCollided = 0;
        ArrayList<URock> removeRocks = new ArrayList<URock>();

        Iterator it = rockList.iterator();
        while(it.hasNext()) {
            URock rock = (URock) it.next();
            if (rock.collide(ship))
                numRocksCollided += 1;

            //if (rock.collide(ship) || !rock.isAlive()) {
            if (!rock.isAlive()) {
                removeRocks.add(rock);
                mRockPool.returnRock(rock);
            }
        }

        if (removeRocks.size() == 0) {
            return false;
        }

        // for now just assume that all rocks were dodged
        setRocksDodged(removeRocks.size());

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
        // for now do a dump replacement of debris bitmap
        if (mDebrisNextBitmap != null) {
            mDebrisBitmap = mDebrisReversedBitmap = mDebrisNextBitmap;
            mDebrisNextBitmap = null;
        }

        Rect fromRect1 = new Rect(0, 0, mDebrisBitmap.getWidth() - mDebrisScroll, mDebrisBitmap.getHeight());
        Rect toRect1 = new Rect(mDebrisScroll, 0, mDebrisBitmap.getWidth(), mDebrisBitmap.getHeight());

        Rect fromRect2 = new Rect(mDebrisReversedBitmap.getWidth() - mDebrisScroll, 0, mDebrisReversedBitmap.getWidth(), mDebrisReversedBitmap.getHeight());
        Rect toRect2 = new Rect(0, 0, mDebrisScroll, mDebrisReversedBitmap.getHeight());

        if (!mReverseDebrisFirst) {
            canvas.drawBitmap(mDebrisBitmap, fromRect1, toRect1, null);
            canvas.drawBitmap(mDebrisBitmap, fromRect2, toRect2, null);
        }
        else {
            canvas.drawBitmap(mDebrisReversedBitmap, fromRect2, toRect2, null);
            canvas.drawBitmap(mDebrisReversedBitmap, fromRect1, toRect1, null);
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

        return true;
    }

    private void setRocksDodged(int numRocks) {
        mNumRocksDodged += numRocks;

        if (mNumRocksDodged < 30) {
            CURRENT_LEVEL = LEVEL_EASY;
            new DecodeDebrisBitmapTask().execute(R.drawable.debris1_blue);
        }
        else if (mNumRocksDodged >= 30 && mNumRocksDodged < 75) {
            CURRENT_LEVEL = LEVEL_MEDIUM;
            new DecodeDebrisBitmapTask().execute(R.drawable.debris2_blue);
        }
        else if (mNumRocksDodged >= 75) {
            CURRENT_LEVEL = LEVEL_HARD;
            new DecodeDebrisBitmapTask().execute(R.drawable.debris3_blue);
        }
    }

    private class DecodeDebrisBitmapTask extends AsyncTask<Integer, Integer, Void> {
        protected Void doInBackground(Integer... resources) {
            //assume only one bitmap will be passed at a time.
            Bitmap bmp = UBitmapUtil.loadScaledBitmap(mContext, resources[0],
                        Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, true);

            mDebrisNextBitmap = bmp;
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //ignore
        }

        protected void onPostExecute(Long result) {
            //ignore
        }
    }

    // Level of difficulty
    private final int LEVEL_EASY = 1;
    private final int LEVEL_MEDIUM = 2;
    private final int LEVEL_HARD = 3;
    private int CURRENT_LEVEL;
    private static int mNumRocksDodged;

    //Measure frames per second.
    private int framesCount = 0;
    private int framesCountAvg = 0;
    private long framesTimer = 0;

    private final int MAX_NUM_ROCKS = 25;
    private final int INIT_NUM_ROCKS = 15;

    private Paint mPaint;
    private UImageInfo mShipInfo;
    private UShip mUShip;
    private CopyOnWriteArrayList mActiveRockList;
    private URockPool mRockPool;

    private Timer mTimer;

    private Bitmap mBgrdBitmap;
    private Bitmap mDebrisBitmap;
    private Bitmap mDebrisReversedBitmap;
    private Bitmap mDebrisNextBitmap = null;

    private int mDebrisScroll;
    private int mBgrdScroll;
    private boolean mReverseBackroundFirst;
    private boolean mReverseDebrisFirst;
    private float mAnimDebrisTime = 5.f;
    private float mAnimBgrdTime = 1.f;

    private Context mContext;
    private static final String TAG = "com.android.ui.GameEngine";
}

