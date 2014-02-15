package com.android.spaceship;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.android.ui.USpaceShipView;

/**
 * Created by ruchitsharma on 2/11/2014.
 */
public class GameThread extends Thread {

    public GameThread(SurfaceHolder surfaceHolder, USpaceShipView gameView,
            GameEngine engine) {
        this.mSurfaceHolder = surfaceHolder;
        this.mGameView = gameView;
        this.mEngine = engine;
    }

    public void setRunning(boolean run) {
        this.mRun = run;
        if(!run) interrupt();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    @Override
    public void run() {
        Canvas c;
        while (mRun && !Thread.interrupted()) {
            c = null;

            //limit frame rate to max 60fps
            timeNow = System.currentTimeMillis();
            timeDelta = timeNow - timePrevFrame;
            if ( timeDelta < 16) {
                try {
                    Thread.sleep(16 - timeDelta);
                }
                catch(InterruptedException e) {

                }
            }
            timePrevFrame = System.currentTimeMillis();

            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    //call methods to draw and process next fame
                    if (c != null) {
                        mEngine.onDraw(c);
                    }
                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    //Frame speed
    private long timeNow;
    private long timePrev = 0;
    private long timePrevFrame = 0;
    private long timeDelta;

    private GameEngine mEngine;
    private SurfaceHolder mSurfaceHolder;
    private USpaceShipView mGameView;
    private boolean mRun = false;
}
