package com.android.ui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.android.spaceship.Global;
import com.android.util.ImageInfo;
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
        mShipInfo = new ImageInfo(center, size, 35.f, 5.f, false); // few dummy values for now

        RectF shipPos = new RectF(400, 400, 490, 490);
        float[] shipVel = {0.f, 5.f};
        mShip = new Ship(mContext, R.drawable.double_ship, shipPos, shipVel, 0, mShipInfo);

        mPrevPosition = new float[2];
        mPrevPosition[0] = shipPos.left;
        mPrevPosition[1] = shipPos.top;

        mBgrdBitmap = UBitmapUtil.loadBitmap(mContext, R.drawable.nebula, false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw the background image first
        canvas.drawBitmap(mBgrdBitmap, null,
                new RectF(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT), mPaint);

        // draw the ship
        mShip.draw(canvas, mPaint);

        // update position
        mShip.update();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;

        if (actionCode == MotionEvent.ACTION_DOWN ||
            actionCode == MotionEvent.ACTION_MOVE) {
            mShip.notifyPosition(x, y);
            mShip.setThrust(true);
        }
        else if (actionCode == MotionEvent.ACTION_UP) {
            //mShip.setThrust(false);
        }

        mPrevPosition[0] = x;
        mPrevPosition[1] = y;
        return true;
    }

    private Paint mPaint;
    private ImageInfo mShipInfo;
    private Ship mShip;
    private float[] mPrevPosition;
    private Bitmap mBgrdBitmap;

    private Context mContext;
    private static final String TAG = "com.android.ui.USpaceShipView";
}
