package com.dimeh.cassebrique.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dimeh.cassebrique.CasseBriqueMain;
import com.dimeh.cassebrique.config.GameConfig;
import com.dimeh.cassebrique.model.Ball;
import com.dimeh.cassebrique.model.Brick;
import com.dimeh.cassebrique.model.GameState;
import com.dimeh.cassebrique.model.Paddle;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    private final CasseBriqueMain game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    private TiledMap tiledMap;
    private int currentLevel = 1;

    // Game state for lives and score
    private GameState gameState;
    private BitmapFont font;

    private float startTimer = 0f;

    // Level Transition
    private boolean isLevelStarting = false;
    private float levelStartTimer = 0f;
    private String levelText = "";

    public GameScreen(CasseBriqueMain game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "Showing GameScreen - Initializing...");
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();

        // Initialize game state
        gameState = new GameState();
        startTimer = 0f; // Reset timer

        // Initialize font for HUD
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        paddle = new Paddle();
        ball = new Ball();

        Gdx.app.log("GameScreen", "Loading Level...");
        loadCurrentLevel();
        Gdx.app.log("GameScreen", "Level Loaded.");
    }

    private void loadCurrentLevel() {
        if (currentLevel > 2) {
            currentLevel = 1; // Loop back to level 1 for now, or show Win Screen
        }
        loadLevel("maps/level" + currentLevel + ".tmx");

        // Trigger Level Announcement
        isLevelStarting = true;
        levelStartTimer = 0f;
        levelText = "LEVEL " + currentLevel;
    }

    private void loadLevel(String mapPath) {
        bricks = new ArrayList<>();

        // Load the map
        try {
            tiledMap = new TmxMapLoader().load(mapPath);

            // Parse bricks from "bricks" object layer
            MapLayer brickLayer = tiledMap.getLayers().get("bricks");
            if (brickLayer != null) {
                MapObjects objects = brickLayer.getObjects();
                for (MapObject object : objects) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        // Tiled coordinates need to be scaled if unit scale is different,
                        // but here we assume Tiled pixels match world units (1:1) or adaptation is
                        // needed.
                        // Also Tiled Y-axis is usually inverted relative to LibGDX if not careful,
                        // but MapObjects are usually placed in world coordinates.
                        // Let's create the Brick.
                        bricks.add(new Brick(rect.x, rect.y, rect.width, rect.height));
                    }
                }
            } else {
                Gdx.app.error("GameScreen", "No 'bricks' layer found in map: " + mapPath);
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Could not load map: " + mapPath, e);
            // Fallback to default bricks if map fails? Or just crash/empty.
            // For now, let's just properly log.
        }
    }

    // Reusable vector to avoid garbage collection
    private final com.badlogic.gdx.math.Vector3 tempVector = new com.badlogic.gdx.math.Vector3();

    @Override
    public void render(float delta) {
        try {
            update(delta);
            draw();
        } catch (Throwable e) {
            Gdx.app.error("GameScreen", "Crash avoided during render", e);
        }
    }

    private void update(float delta) {
        // Level Transition Logic
        if (isLevelStarting) {
            levelStartTimer += delta;
            if (levelStartTimer > 2f) {
                isLevelStarting = false;
            }
            return; // Pause game during announcement
        }

        // Input handling
        boolean inputHandled = false;

        // Keyboard control
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q)) {
            paddle.moveLeft(delta);
            inputHandled = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            paddle.moveRight(delta);
            inputHandled = true;
        }

        // Mouse control (only if no keyboard input to avoid conflict)
        if (!inputHandled) {
            // Convert screen coordinates to world coordinates
            tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0); // Corrected getY usage
            viewport.unproject(tempVector);
            paddle.update(delta, tempVector.x);
        }

        // Logic to prevent immediate launch on screen switch
        if (startTimer < 0.5f) {
            startTimer += delta;
            // Force ball position to paddle while waiting
            ball.setPosition(
                    paddle.getX() + (paddle.getWidth() - ball.getWidth()) / 2,
                    paddle.getY() + paddle.getHeight() + 2);
            return;
        }

        // Ball logic
        if (!ball.isActive()) {
            ball.setPosition(
                    paddle.getX() + (paddle.getWidth() - ball.getWidth()) / 2,
                    paddle.getY() + paddle.getHeight() + 2);

            if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)
                    || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                ball.launch();
            }
        } else {
            ball.update(delta);
            checkCollisions();
            checkGameOver();
        }
    }

    private void checkCollisions() {
        // Ball vs Paddle
        if (Intersector.overlaps(ball.getBounds(), paddle.getBounds())) {
            Rectangle ballRect = ball.getBounds();
            Rectangle paddleRect = paddle.getBounds();
            
            // Calculate overlap to determine collision side
            float ballCenterX = ballRect.x + ballRect.width / 2;
            float ballCenterY = ballRect.y + ballRect.height / 2;
            float paddleCenterX = paddleRect.x + paddleRect.width / 2;
            float paddleCenterY = paddleRect.y + paddleRect.height / 2;
            
            float overlapX = (ballRect.width / 2 + paddleRect.width / 2) - Math.abs(ballCenterX - paddleCenterX);
            float overlapY = (ballRect.height / 2 + paddleRect.height / 2) - Math.abs(ballCenterY - paddleCenterY);
            
            if (overlapY < overlapX) {
                // Top/bottom collision - reverse Y velocity
                if (ball.getVelocityY() < 0) {
                    ball.reverseY();
                    ball.setPosition(ball.getX(), paddle.getY() + paddle.getHeight() + 1);
                }
            } else {
                // Side collision - reverse X velocity
                ball.reverseX();
                if (ballCenterX < paddleCenterX) {
                    ball.setPosition(paddle.getX() - ball.getWidth() - 1, ball.getY());
                } else {
                    ball.setPosition(paddle.getX() + paddle.getWidth() + 1, ball.getY());
                }
            }
            
            // Add english based on hit position
            float hitFactor = (ballCenterX - paddleCenterX) / (paddle.getWidth() / 2);
            ball.setVelocityX(ball.getVelocityX() + hitFactor * 100);
        }

        // Ball vs Bricks
        Iterator<Brick> iter = bricks.iterator();
        while (iter.hasNext()) {
            Brick brick = iter.next();
            if (Intersector.overlaps(ball.getBounds(), brick.getBounds())) {
                ball.reverseY();
                iter.remove(); // Destroy brick
                gameState.addBrickScore(); // Add score!
                break; // Only hit one brick per frame to prevent weird physics for now
            }
        }
    }

    private void checkGameOver() {
        if (ball.getY() < 0) {
            Gdx.app.log("GameScreen", "Ball died at Y=" + ball.getY());
            gameState.loseLife(); // Lose a life

            if (gameState.isGameOver()) {
                // Transition to Game Over screen
                game.setScreen(new GameOverScreen(game, gameState.getScore()));
                return;
            }

            ball.reset(); // Reset ball position
        }

        if (bricks.isEmpty()) {
            // Level complete
            Gdx.app.log("GameScreen", "Level " + currentLevel + " Complete!");
            currentLevel++;
            loadCurrentLevel();
            ball.reset();
            // Reset timer to prevent instant launch
            startTimer = 0f;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Paddle
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());

        // Draw Ball
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

        // Draw Bricks
        shapeRenderer.setColor(Color.RED);
        for (Brick brick : bricks) {
            shapeRenderer.rect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        }

        shapeRenderer.end();

        // Draw HUD (score and lives)
        drawHUD();

        if (isLevelStarting) {
            drawLevelAnnouncement();
        }
    }

    private void drawLevelAnnouncement() {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Re-use font but scale it up temporarily
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        font.getData().setScale(3f);

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font,
                levelText);
        float x = (GameConfig.WORLD_WIDTH - layout.width) / 2;
        float y = (GameConfig.WORLD_HEIGHT + layout.height) / 2;

        font.draw(game.batch, levelText, x, y);

        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);

        game.batch.end();
    }

    private void drawHUD() {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Draw score on left
        font.draw(game.batch, "Score: " + gameState.getScore(), 20, GameConfig.WORLD_HEIGHT - 15);

        // Draw lives on right
        String livesText = "Vies: " + gameState.getLives();
        font.draw(game.batch, livesText, GameConfig.WORLD_WIDTH - 120, GameConfig.WORLD_HEIGHT - 15);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
