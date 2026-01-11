package com.dimeh.cassebrique.model;

public class Brick extends GameObject {
    private boolean destroyed;

    public Brick(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.destroyed = false;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
