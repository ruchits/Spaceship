package com.android.ui;

import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.android.spaceship.Global;
import com.android.spaceship.GameThread;
import com.android.spaceship.GameEngine;

/**
 * Created by ruchitsharma on 2/7/2014.
 */

/*
 * This is the 2-D view for the game.
 * It is responsible for drawing all objects in the game.
 */
public class USpaceShipView extends SurfaceView implements SurfaceHolder.Callback {

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

        // set thread
        getHolder().addCallback(this);

        // set focusable to true
        setFocusable(true);
    }

    /*
     * Responsible for initializing all UI elements
     */
    private void init() {
        mEngine = new GameEngine(mContext);
        setFocusable(true);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        //ignore
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGameThread = new GameThread(getHolder(), this, mEngine);
        mGameThread.setRunning(true);
        mGameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (mGameThread != null) {
            mGameThread.setRunning(false);

            while (retry) {
                try {
                mGameThread.join();
                retry = false;
                } catch (InterruptedException e) {

                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mEngine.notifyTouch(event);
    }

    private GameThread mGameThread;
    private GameEngine mEngine;
    private Context mContext;

    private static final String TAG = "com.android.ui.USpaceShipView";
}
