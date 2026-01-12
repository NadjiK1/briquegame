package com.dimeh.cassebrique.model;

public class Brick extends GameObject {
    private boolean destroyed;
    private int type; // 1 = Normal, 2 = Dur (2 vies), 3 = Indestructible
    private int vies; // Nombre de coups pour détruire

    public Brick(float x, float y, float width, float height, int type) {
        super(x, y, width, height);
        this.destroyed = false;
        this.type = type;
        this.vies = type; // Simple : Type 1 = 1 vie, Type 2 = 2 vies
    }

    public void toucher() {
        // Enlève une vie quand on touche la brique
        vies--;

        // Si plus de vie, on détruit
        if (vies <= 0) {
            destroyed = true;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public int getType() {
        return type;
    }

    public int getVies() {
        return vies;
    }
}
