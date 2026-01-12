package com.dimeh.cassebrique.model;

import com.dimeh.cassebrique.config.GameConfig;

/**
 * Gère l'état global du jeu : vies, score et statut de la partie.
 */
public class GameState {
    private int lives;
    private int score;

    public GameState() {
        reset();
    }

    public void reset() {
        lives = GameConfig.INITIAL_LIVES;
        score = 0;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void addScore(int points) {
        score += points;
    }

    public void addBrickScore() {
        addScore(GameConfig.POINTS_PER_BRICK);
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }
}
