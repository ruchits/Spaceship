package com.android.util;

/**
 * Created by ruchitsharma on 2/8/2014.
 */

/*
 * Data structure containing important information
 * about an image.
 */
public class UImageInfo {
    private int mResID;
    private float[] mCenter;
    private float[] mSize;
    private float mRadius = 0.f;
    private float mLifespan = Float.POSITIVE_INFINITY;
    boolean mAnimated = false;
    private int mTiles;

    public UImageInfo(int resID, float[] center, float[] size, float radius,
                      float lifespan, boolean animated, int tiles) {
        mResID = resID;
        mCenter = center;
        mSize = size;
        mRadius = radius;
        mLifespan = lifespan;
        mAnimated = animated;
        mTiles = tiles;
    }

    public UImageInfo(float[] center, float[] size) {
        mCenter = center;
        mSize = size;
    }

    public int getResID() { return mResID; }

    public float[] getCenter() {
        return mCenter;
    }

    public float[] getSize() {
        return mSize;
    }

    public float getRadius() {
        return mRadius;
    }

    public float getLifespan() {
        return mLifespan;
    }

    public boolean isAnimated() {
        return mAnimated;
    }

    public int getNumTiles() { return mTiles; }
}
