package com.android.ui;

import java.util.ArrayList;

/**
 * Created by ruchitsharma on 2/11/2014.
 */

// Convenience class wrapping pool of rocks
public class URockPool {

    // Construct a rockPool
    public URockPool(int poolLimit) {
        mPoolLimit = poolLimit;
        mPool = new ArrayList<URock>(poolLimit);
    }

    // Get a rock from the pool.
    public synchronized URock getRock() {
        int size = mPool.size();
        return size > 0 ? mPool.remove(size - 1) : null;
    }

    // Get a rock from the pool with the specified size.
    public synchronized URock getRock(int width, int height) {
        for (int i = 0; i < mPool.size(); i++) {
            URock b = mPool.get(i);
            if (b.getSize().x == width && b.getSize().y == height) {
                return mPool.remove(i);
            }
        }
        return null;
    }

    // Put a Bitmap into the pool, if the Bitmap has a proper size. Otherwise
    // the Bitmap will be recycled. If the pool is full, an old Bitmap will be
    // recycled.
    public void returnRock(URock rock) {
        if (rock == null) return;

        synchronized (this) {
            if (mPool.size() >= mPoolLimit) mPool.remove(0);
            mPool.add(rock);
        }
    }

    public synchronized void clear() {
        mPool.clear();
    }

    public synchronized boolean isEmpty() {
        return (mPool.size() == 0);
    }

    private static final String TAG = "com.room.utils.UBitmapPool";

    private final ArrayList<URock> mPool;
    private final int mPoolLimit;
}
