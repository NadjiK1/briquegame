package com.dimeh.cassebrique.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dimeh.cassebrique.CasseBriqueMain;
import com.dimeh.cassebrique.config.GameConfig;

/**
 * Main menu screen with title and play button.
 */
public class MenuScreen extends ScreenAdapter {

    private final CasseBriqueMain game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private GlyphLayout glyphLayout;

    private Rectangle playButton;
    private boolean buttonHovered;

    public MenuScreen(CasseBriqueMain game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(Color.WHITE);

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2f);
        buttonFont.setColor(Color.WHITE);

        glyphLayout = new GlyphLayout();

        // Play button centered
        float buttonWidth = 200f;
        float buttonHeight = 60f;
        float buttonX = (GameConfig.WORLD_WIDTH - buttonWidth) / 2;
        float buttonY = GameConfig.WORLD_HEIGHT / 2 - 50f;
        playButton = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        update();
        if (game.getScreen() != this)
            return; // Stop if screen changed/disposed
        draw();
    }

    // Reusable vector
    private final com.badlogic.gdx.math.Vector3 tempVector = new com.badlogic.gdx.math.Vector3();

    private void update() {
        // Check mouse hover over button
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tempVector);
        float mouseX = tempVector.x;
        float mouseY = tempVector.y;

        buttonHovered = playButton.contains(mouseX, mouseY);

        // Handle click
        if (Gdx.input.justTouched() && buttonHovered) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    private void draw() {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1f);
        viewport.apply();

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw button
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (buttonHovered) {
            shapeRenderer.setColor(0.3f, 0.5f, 0.9f, 1f);
        } else {
            shapeRenderer.setColor(0.2f, 0.4f, 0.8f, 1f);
        }
        shapeRenderer.rect(playButton.x, playButton.y, playButton.width, playButton.height);

        shapeRenderer.end();

        // Draw button border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(playButton.x, playButton.y, playButton.width, playButton.height);
        shapeRenderer.end();

        // Draw text
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Title
        glyphLayout.setText(titleFont, "CASSE-BRIQUE");
        float titleX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float titleY = GameConfig.WORLD_HEIGHT - 100f;
        titleFont.draw(game.batch, "CASSE-BRIQUE", titleX, titleY);

        // Button text
        glyphLayout.setText(buttonFont, "JOUER");
        float textX = playButton.x + (playButton.width - glyphLayout.width) / 2;
        float textY = playButton.y + (playButton.height + glyphLayout.height) / 2;
        buttonFont.draw(game.batch, "JOUER", textX, textY);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
