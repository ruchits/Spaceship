package com.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;

/**
 * Created by ruchitsharma on 2/9/2014.
 */
public class UBitmapUtil {

    public static Bitmap loadScaledBitmap(Context context, int resID, int width, int height, boolean useRGB565)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = useRGB565 ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
        Bitmap tempBmp = BitmapFactory.decodeResource(context.getResources(), resID, options);

        //if no scaling needed, then return the bmp right away
        if(tempBmp.getWidth() == width && tempBmp.getHeight() == height)
            return tempBmp;

        //otherwise, create a scaled bmp, recycling the old one
        Bitmap retBmp = Bitmap.createScaledBitmap(
                tempBmp,width, height, true);
        tempBmp.recycle();

        return retBmp;
    }

    public static Bitmap loadBitmap(Context context, int resID, boolean useRGB565) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = useRGB565 ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resID, options);
        return bmp;
    }
}
