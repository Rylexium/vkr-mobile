package com.example.vkr.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnimationHideFab {
    public static void show(FloatingActionButton fab){
        fab.animate()
                .translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
    public static void hide(FloatingActionButton fab){
        fab.animate()
                .translationY(2 * fab.getHeight())
                .setInterpolator(new AccelerateInterpolator(2)).start();
    }
}
