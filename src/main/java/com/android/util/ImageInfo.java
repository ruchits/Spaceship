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
    private float mRadius = 0;
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

    public float[] get_center() {
        return mCenter;
    }

    public float[] get_size() {
        return mSize;
    }

    public float get_radius() {
        return mRadius;
    }

    public float get_lifespan() {
        return mLifespan;
    }

    public boolean get_animated() {
        return mAnimated;
    }
}
