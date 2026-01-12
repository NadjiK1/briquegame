package com.dimeh.cassebrique.model;

import com.dimeh.cassebrique.config.GameConfig;

public class Paddle extends GameObject {

    private static final float SPEED = 500f;

    public Paddle() {
        super(
                (GameConfig.WORLD_WIDTH - GameConfig.PADDLE_WIDTH) / 2,
                20f, // Position Y initiale
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT);
    }

    public void update(float delta, float targetX) {
        // Contrôle souris : positionnement direct
        float newX = targetX - (bounds.width / 2);
        setPositionX(newX);
    }

    public void moveLeft(float delta) {
        float newX = bounds.x - SPEED * delta;
        setPositionX(newX);
    }

    public void moveRight(float delta) {
        float newX = bounds.x + SPEED * delta;
        setPositionX(newX);
    }

    private void setPositionX(float x) {
        if (x < 0)
            x = 0;
        if (x > GameConfig.WORLD_WIDTH - bounds.width) {
            x = GameConfig.WORLD_WIDTH - bounds.width;
        }
        bounds.x = x;
    }
}
