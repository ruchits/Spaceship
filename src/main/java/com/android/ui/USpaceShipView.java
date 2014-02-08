package com.android.ui;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.graphics.RectF;

import com.android.util.ImageInfo;
import com.android.spaceship.R;

/**
 * Created by ruchitsharma on 2/7/2014.
 */
public class USpaceShipView extends View {

    ImageInfo mShipInfo;
    Context mContext;
    private static final String TAG = "com.android.ui.USpaceShieView";

    public USpaceShipView(Context context) {
        super(context);
        init();
        mContext = context;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        // Init a ship
        float[] center = {45, 45};
        float[] size = {90, 90};
        mShipInfo = new ImageInfo(center, size, 35, 5, false); // few dummy values for now
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // decode the ship image into a bitmap.
        // TODO: need to move this out of the draw call
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.double_ship, options);


        //draw the ship on blank canvas
        Log.e(TAG, "is bmp null?= " + (bmp == null));
        Log.e(TAG, "is paint null?= " + (mPaint == null));
        RectF dest = new RectF(10, 10, 45, 45);
        canvas.drawBitmap(bmp, null, dest, mPaint);
        bmp.recycle();
    }

    private Paint mPaint;
}
