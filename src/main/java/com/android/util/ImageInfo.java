package com.android.util;

/**
 * Created by ruchitsharma on 2/8/2014.
 */

/*
 * Data structure containing important information
 * about an image.
 */
public class ImageInfo {
    private float[] mCenter;
    private float[] mSize;
    private float mRadius = 0.f;
    private float mLifespan = Float.POSITIVE_INFINITY;
    boolean mAnimated = false;

    public ImageInfo(float[] center, float[] size, float radius,
                     float lifespan, boolean animated) {
        mCenter = center;
        mSize = size;
        mRadius = radius;
        mLifespan = lifespan;
        mAnimated = animated;
    }

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
}
