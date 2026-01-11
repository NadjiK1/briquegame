package com.dimeh.cassebrique.config;

public class GameConfig {
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;

    public static final float PADDLE_WIDTH = 100f;
    public static final float PADDLE_HEIGHT = 20f;

    public static final float BALL_RADIUS = 10f;

    public static final float BRICK_WIDTH = 60f;
    public static final float BRICK_HEIGHT = 20f;

    // Game rules
    public static final int INITIAL_LIVES = 3;
    public static final int POINTS_PER_BRICK = 10;

    // Prevent instantiation
    private GameConfig() {
    }
}
