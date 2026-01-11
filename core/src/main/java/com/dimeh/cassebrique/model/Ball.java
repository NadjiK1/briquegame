package com.dimeh.cassebrique.model;

import com.badlogic.gdx.math.MathUtils;
import com.dimeh.cassebrique.config.GameConfig;

public class Ball extends GameObject {
    private float velocityX;
    private float velocityY;
    private boolean active;

    public Ball() {
        super(0, 0, GameConfig.BALL_RADIUS * 2, GameConfig.BALL_RADIUS * 2);
        reset();
    }

    public void reset() {
        active = false;
        velocityX = 0;
        velocityY = 0;
        // Position will be set by GameScreen to follow paddle initially
    }

    public void launch() {
        active = true;
        velocityX = MathUtils.random(-200f, 200f);
        velocityY = 300f;
    }

    public void update(float delta) {
        if (!active)
            return;

        bounds.x += velocityX * delta;
        bounds.y += velocityY * delta;

        // Wall collisions
        if (bounds.x < 0) {
            bounds.x = 0;
            velocityX = -velocityX;
        }
        if (bounds.x > GameConfig.WORLD_WIDTH - bounds.width) {
            bounds.x = GameConfig.WORLD_WIDTH - bounds.width;
            velocityX = -velocityX;
        }
        if (bounds.y > GameConfig.WORLD_HEIGHT - bounds.height) {
            bounds.y = GameConfig.WORLD_HEIGHT - bounds.height;
            velocityY = -velocityY;
        }
    }

    public void reverseY() {
        velocityY = -velocityY;
    }

    public void reverseX() {
        velocityX = -velocityX;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float vx) {
        this.velocityX = vx;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float vy) {
        this.velocityY = vy;
    }
}
