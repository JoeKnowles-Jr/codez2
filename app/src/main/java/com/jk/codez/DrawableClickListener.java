package com.jk.codez;

public interface DrawableClickListener {
    enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT, SHOWKB, HIDEKB }
    void onDrawableClick(DrawablePosition target);
}

