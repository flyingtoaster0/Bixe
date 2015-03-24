package com.flyingtoaster.bixe;

import android.view.animation.Interpolator;

public class MaterialInterpolator implements Interpolator {

    public float getInterpolation(float x) {
        return (float) (6 * Math.pow(x, 2) - 8 * Math.pow(x, 3) + 3 * Math.pow(x, 4));
    }
}