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
 * Écran de victoire affiché après avoir terminé tous les niveaux.
 */
public class WinScreen extends ScreenAdapter {

    private final CasseBriqueMain game;
    private final int finalScore;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont scoreFont;
    private BitmapFont buttonFont;
    private GlyphLayout glyphLayout;

    private Rectangle replayButton;
    private Rectangle menuButton;
    private boolean replayHovered;
    private boolean menuHovered;

    // Animation
    private float animationTimer = 0f;

    public WinScreen(CasseBriqueMain game, int finalScore) {
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
        titleFont.setColor(Color.GOLD);

        subtitleFont = new BitmapFont();
        subtitleFont.getData().setScale(2f);
        subtitleFont.setColor(Color.WHITE);

        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(2.5f);
        scoreFont.setColor(Color.YELLOW);

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.8f);
        buttonFont.setColor(Color.WHITE);

        glyphLayout = new GlyphLayout();

        // Boutons
        float buttonWidth = 180f;
        float buttonHeight = 50f;
        float spacing = 30f;
        float totalWidth = buttonWidth * 2 + spacing;
        float startX = (GameConfig.WORLD_WIDTH - totalWidth) / 2;
        float buttonY = GameConfig.WORLD_HEIGHT / 2 - 120f;

        replayButton = new Rectangle(startX, buttonY, buttonWidth, buttonHeight);
        menuButton = new Rectangle(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        animationTimer += delta;
        update();
        if (game.getScreen() != this)
            return;
        draw();
    }

    // Vecteur réutilisable
    private final com.badlogic.gdx.math.Vector3 tempVector = new com.badlogic.gdx.math.Vector3();

    private void update() {
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tempVector);
        float mouseX = tempVector.x;
        float mouseY = tempVector.y;

        replayHovered = replayButton.contains(mouseX, mouseY);
        menuHovered = menuButton.contains(mouseX, mouseY);

        if (Gdx.input.justTouched()) {
            if (replayHovered) {
                Gdx.app.log("WinScreen", "Replay Clicked");
                try {
                    game.setScreen(new GameScreen(game));
                    dispose();
                } catch (Throwable e) {
                    Gdx.app.error("WinScreen", "Failed to switch to GameScreen", e);
                    e.printStackTrace();
                }
            } else if (menuHovered) {
                Gdx.app.log("WinScreen", "Menu Clicked");
                try {
                    game.setScreen(new MenuScreen(game));
                    dispose();
                } catch (Throwable e) {
                    Gdx.app.error("WinScreen", "Failed to switch to MenuScreen", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        // Fond vert foncé pour la victoire
        ScreenUtils.clear(0.05f, 0.15f, 0.1f, 1f);
        viewport.apply();

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Dessiner des étoiles animées en arrière-plan
        drawStars();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Bouton rejouer
        if (replayHovered) {
            shapeRenderer.setColor(0.2f, 0.7f, 0.3f, 1f);
        } else {
            shapeRenderer.setColor(0.15f, 0.5f, 0.2f, 1f);
        }
        shapeRenderer.rect(replayButton.x, replayButton.y, replayButton.width, replayButton.height);

        // Bouton menu
        if (menuHovered) {
            shapeRenderer.setColor(0.5f, 0.5f, 0.6f, 1f);
        } else {
            shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 1f);
        }
        shapeRenderer.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);

        shapeRenderer.end();

        // Dessiner les bordures des boutons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(replayButton.x, replayButton.y, replayButton.width, replayButton.height);
        shapeRenderer.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);
        shapeRenderer.end();

        // Dessiner le texte
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Titre VICTOIRE avec effet de pulsation
        float pulse = 1f + 0.1f * (float) Math.sin(animationTimer * 3);
        titleFont.getData().setScale(4f * pulse);
        glyphLayout.setText(titleFont, "VICTOIRE !");
        float titleX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float titleY = GameConfig.WORLD_HEIGHT - 80f;
        titleFont.draw(game.batch, "VICTOIRE !", titleX, titleY);
        titleFont.getData().setScale(4f); // Restaurer

        // Sous-titre
        glyphLayout.setText(subtitleFont, "Félicitations ! Tous les niveaux sont terminés !");
        float subtitleX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float subtitleY = GameConfig.WORLD_HEIGHT - 150f;
        subtitleFont.draw(game.batch, "Félicitations ! Tous les niveaux sont terminés !", subtitleX, subtitleY);

        // Score final
        String scoreText = "Score Final: " + finalScore;
        glyphLayout.setText(scoreFont, scoreText);
        float scoreX = (GameConfig.WORLD_WIDTH - glyphLayout.width) / 2;
        float scoreY = GameConfig.WORLD_HEIGHT / 2 + 30f;
        scoreFont.draw(game.batch, scoreText, scoreX, scoreY);

        // Textes des boutons
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

    private void drawStars() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLD);

        // Dessiner quelques étoiles fixes avec effet de scintillement
        for (int i = 0; i < 20; i++) {
            float x = (i * 137) % GameConfig.WORLD_WIDTH;
            float y = (i * 89) % GameConfig.WORLD_HEIGHT;
            float size = 2f + (float) Math.sin(animationTimer * 2 + i) * 1f;
            shapeRenderer.circle(x, y, size);
        }

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
        scoreFont.dispose();
        buttonFont.dispose();
    }
}
