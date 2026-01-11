package com.dimeh.cassebrique;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dimeh.cassebrique.screen.MenuScreen;

public class CasseBriqueMain extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
