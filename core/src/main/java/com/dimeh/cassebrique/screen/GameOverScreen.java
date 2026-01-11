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
 * Game Over screen showing final score with replay and menu options.
 */
public class GameOverScreen extends ScreenAdapter {

    private final CasseBriqueMain game;
    private final int finalScore;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont buttonFont;
    private GlyphLayout glyphLayout;

    private Rectangle replayButton;
    private Rectangle menuButton;
    private boolean replayHovered;
    private boolean menuHovered;

    public GameOverScreen(CasseBriqueMain game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(Color.RED);

        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(2.5f);
        scoreFont.setColor(Color.YELLOW);

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.8f);
        buttonFont.setColor(Color.WHITE);

        glyphLayout = new GlyphLayout();

        // Buttons
        float buttonWidth = 180f;
        float buttonHeight = 50f;
        float spacing = 30f;
        float totalWidth = buttonWidth * 2 + spacing;
        float startX = (GameConfig.WORLD_WIDTH - totalWidth) / 2;
        float buttonY = GameConfig.WORLD_HEIGHT / 2 - 100f;

        replayButton = new Rectangle(startX, buttonY, buttonWidth, buttonHeight);
        menuButton = new Rectangle(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
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
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tempVector);
        float mouseX = tempVector.x;
        float mouseY = tempVector.y;

        replayHovered = replayButton.contains(mouseX, mouseY);
        menuHovered = menuButton.contains(mouseX, mouseY);

        if (Gdx.input.justTouched()) {
            Gdx.app.log("GameOverScreen", "Click at: " + mouseX + ", " + mouseY);
            Gdx.app.log("GameOverScreen", "Replay Bounds: " + replayButton.toString());
            Gdx.app.log("GameOverScreen", "Menu Bounds: " + menuButton.toString());

            if (replayHovered) {
                Gdx.app.log("GameOverScreen", "Replay Clicked");
                try {
                    game.setScreen(new GameScreen(game));
                    dispose();
                } catch (Throwable e) {
                    Gdx.app.error("GameOverScreen", "Failed to switch to GameScreen", e);
                    e.printStackTrace();
                }
            } else if (menuHovered) {
                Gdx.app.log("GameOverScreen", "Menu Clicked");
                try {
                    game.setScreen(new MenuScreen(game));
                    dispose();
                } catch (Throwable e) {
                    Gdx.app.error("GameOverScreen", "Failed to switch to MenuScreen", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.05f, 0.05f, 1f);
        viewport.apply();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Replay button
        if (replayHovered) {
            shapeRenderer.setColor(0.2f, 0.7f, 0.3f, 1f);
        } else {
            shapeRenderer.setColor(0.15f, 0.5f, 0.2f, 1f);
        }
        shapeRenderer.rect(replayButton.x, replayButton.y, replayButton.width, replayButton.height);

        // Menu button
        if (menuHovered) {
            shapeRenderer.setColor(0.5f, 0.5f, 0.6f, 1f);
        } else {
            shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 1f);
        }
        shapeRenderer.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        shapeRenderer.end();

        // Draw button borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Draw actual hitbox in RED to debug
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(replayButton.x, replayButton.y, replayButton.width, replayButton.height);
        shapeRenderer.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        // Draw nicer border on top
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(replayButton.x, replayButton.y, replayButton.width, replayButton.height);
        shapeRenderer.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);
        shapeRenderer.end();

        // Draw text
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Game Over title
        glyphLayout.setText(titleFont, "GAME OVER");
        float titleX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float titleY = GameConfig.WORLD_HEIGHT - 100f;
        titleFont.draw(game.batch, "GAME OVER", titleX, titleY);

        // Final score
        String scoreText = "Score: " + finalScore;
        glyphLayout.setText(scoreFont, scoreText);
        float scoreX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float scoreY = GameConfig.WORLD_HEIGHT / 2 + 50f;
        scoreFont.draw(game.batch, scoreText, scoreX, scoreY);

        // Button texts
        glyphLayout.setText(buttonFont, "REJOUER");
        float replayTextX = replayButton.x + (replayButton.width - glyphLayout.width) / 2;
        float replayTextY = replayButton.y + (replayButton.height + glyphLayout.height) / 2;
        buttonFont.draw(game.batch, "REJOUER", replayTextX, replayTextY);

        glyphLayout.setText(buttonFont, "MENU");
        float menuTextX = menuButton.x + (menuButton.width - glyphLayout.width) / 2;
        float menuTextY = menuButton.y + (menuButton.height + glyphLayout.height) / 2;
        buttonFont.draw(game.batch, "MENU", menuTextX, menuTextY);

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
        scoreFont.dispose();
        buttonFont.dispose();
    }
}
