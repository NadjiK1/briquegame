package com.dimeh.cassebrique.model;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected Rectangle bounds;

    public GameObject(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        this.bounds.setPosition(x, y);
    }
}
