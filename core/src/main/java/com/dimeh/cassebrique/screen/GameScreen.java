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

    // État du jeu pour les vies et le score
    private GameState gameState;
    private BitmapFont font;

    private float startTimer = 0f;

    // Input mode tracking - pour éviter le conflit clavier/souris
    private boolean usingKeyboard = false;
    private float lastMouseX = -1f;

    // Transition de niveau
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

        // Initialiser l'état du jeu
        gameState = new GameState();
        startTimer = 0f; // Réinitialiser le minuteur

        // Initialiser la police pour l'interface
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        paddle = new Paddle();
        ball = new Ball();

        Gdx.app.log("GameScreen", "Loading Level...");
        loadCurrentLevel();
        Gdx.app.log("GameScreen", "Level Loaded.");
    }

    // pour changer le nombre de niveau on change ici c'est > 3 si on met 4 par
    // exemple il faudrait un niveau 4
    private void loadCurrentLevel() {
        if (currentLevel > 3) {
            game.setScreen(new WinScreen(game, gameState.getScore()));
            return;
        }
        loadLevel("maps/level" + currentLevel + ".tmx");

        // Déclencher l'annonce du niveau
        isLevelStarting = true;
        levelStartTimer = 0f;
        levelText = "LEVEL " + currentLevel;
    }

    private void loadLevel(String mapPath) {
        bricks = new ArrayList<>();

        // Charger la carte
        try {
            tiledMap = new TmxMapLoader().load(mapPath);
            // Parcourir TOUS les calques pour trouver des briques
            for (MapLayer layer : tiledMap.getLayers()) {
                MapObjects objects = layer.getObjects();
                if (objects.getCount() > 0) {
                    for (MapObject object : objects) {
                        if (object instanceof RectangleMapObject) {
                            Rectangle rect = ((RectangleMapObject) object).getRectangle();
                            int type = 1;

                            // 1. Chercher "type" sur l'objet
                            if (object.getProperties().containsKey("type")) {
                                Object val = object.getProperties().get("type");
                                type = Integer.parseInt(val.toString());
                            }
                            // 2. Sinon, chercher "type" sur le calque (fallback)
                            else if (layer.getProperties().containsKey("type")) {
                                Object val = layer.getProperties().get("type");
                                type = Integer.parseInt(val.toString());
                            }

                            bricks.add(new Brick(rect.x, rect.y, rect.width, rect.height, type));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Could not load map: " + mapPath, e);
            // Utiliser des briques par défaut si la carte échoue ? Ou planter/vide.
            // Pour l'instant, on se contente de bien logger.
        }
    }

    // Vecteur réutilisable pour éviter le ramasse-miettes
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
        // Logique de transition de niveau
        if (isLevelStarting) {
            levelStartTimer += delta;
            if (levelStartTimer > 2f) {
                isLevelStarting = false;
            }
            return; // Mettre le jeu en pause pendant l'annonce
        }

        // Input handling - Gestion améliorée clavier/souris

        // Contrôle clavier
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q)) {
            paddle.moveLeft(delta);
            usingKeyboard = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            paddle.moveRight(delta);
            usingKeyboard = true;
        }

        // Mouse control - seulement si la souris a réellement bougé
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tempVector);

        // Détecter si la souris a bougé (avec une petite tolérance)
        if (lastMouseX >= 0 && Math.abs(tempVector.x - lastMouseX) > 2f) {
            usingKeyboard = false; // Revenir au mode souris seulement si la souris bouge
        }
        lastMouseX = tempVector.x;

        // Appliquer le contrôle souris seulement si on n'utilise pas le clavier
        if (!usingKeyboard) {
            paddle.update(delta, tempVector.x);
        }

        // Logique pour empêcher le lancement immédiat lors du changement d'écran
        if (startTimer < 0.5f) {
            startTimer += delta;
            // Forcer la position de la balle sur la raquette pendant l'attente
            ball.setPosition(
                    paddle.getX() + (paddle.getWidth() - ball.getWidth()) / 2,
                    paddle.getY() + paddle.getHeight() + 2);
            return;
        }

        // Logique de la balle
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
        // Balle contre Raquette
        if (Intersector.overlaps(ball.getBounds(), paddle.getBounds())) {
            Rectangle ballRect = ball.getBounds();
            Rectangle paddleRect = paddle.getBounds();

            // Calculer le chevauchement pour déterminer le côté de la collision
            float ballCenterX = ballRect.x + ballRect.width / 2;
            float ballCenterY = ballRect.y + ballRect.height / 2;
            float paddleCenterX = paddleRect.x + paddleRect.width / 2;
            float paddleCenterY = paddleRect.y + paddleRect.height / 2;

            float overlapX = (ballRect.width / 2 + paddleRect.width / 2) - Math.abs(ballCenterX - paddleCenterX);
            float overlapY = (ballRect.height / 2 + paddleRect.height / 2) - Math.abs(ballCenterY - paddleCenterY);

            if (overlapY < overlapX) {
                // Collision haut/bas - inverser la vélocité Y
                if (ball.getVelocityY() < 0) {
                    ball.reverseY();
                    ball.setPosition(ball.getX(), paddle.getY() + paddle.getHeight() + 1);
                }
            } else {
                // Collision latérale - inverser la vélocité X
                ball.reverseX();
                if (ballCenterX < paddleCenterX) {
                    ball.setPosition(paddle.getX() - ball.getWidth() - 1, ball.getY());
                } else {
                    ball.setPosition(paddle.getX() + paddle.getWidth() + 1, ball.getY());
                }
            }

            // Ajouter de l'effet basé sur la position de frappe
            float hitFactor = (ballCenterX - paddleCenterX) / (paddle.getWidth() / 2);
            ball.setVelocityX(ball.getVelocityX() + hitFactor * 100);
        }

        // Balle contre Briques
        Iterator<Brick> iter = bricks.iterator();
        while (iter.hasNext()) {
            Brick brick = iter.next();
            if (Intersector.overlaps(ball.getBounds(), brick.getBounds())) {
                ball.reverseY();
                brick.toucher(); // On enlève une vie

                if (brick.isDestroyed()) {
                    iter.remove(); // Détruire brique si 0 vies
                    gameState.addBrickScore();
                }
                break; // Ne toucher qu'une brique par frame pour éviter une physique bizarre
            }
        }
    }

    private void checkGameOver() {
        if (ball.getY() < 0) {
            Gdx.app.log("GameScreen", "Ball died at Y=" + ball.getY());
            gameState.loseLife(); // Perdre une vie

            if (gameState.isGameOver()) {
                // Transition vers l'écran Game Over
                game.setScreen(new GameOverScreen(game, gameState.getScore()));
                return;
            }

            ball.reset(); // Réinitialiser la position de la balle
        }

        if (bricks.isEmpty()) {
            // Niveau terminé
            Gdx.app.log("GameScreen", "Level " + currentLevel + " Complete!");
            currentLevel++;
            loadCurrentLevel();
            ball.reset();
            // Réinitialiser le minuteur pour empêcher le lancement instantané
            startTimer = 0f;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dessiner la Raquette
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());

        // Dessiner la Balle
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

        // Dessiner les Briques
        for (Brick brick : bricks) {
            // Couleur basée sur les VIES restantes (Visual feedback !)
            if (brick.getVies() >= 3) {
                shapeRenderer.setColor(Color.GREEN);
            } else if (brick.getVies() == 2) {
                shapeRenderer.setColor(Color.ORANGE);
            } else {
                shapeRenderer.setColor(Color.RED);
            }
            shapeRenderer.rect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        }

        shapeRenderer.end();

        // Dessiner l'interface (score et vies)
        drawHUD();

        if (isLevelStarting) {
            drawLevelAnnouncement();
        }
    }

    private void drawLevelAnnouncement() {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Réutiliser la police mais l'agrandir temporairement
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        font.getData().setScale(3f);

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font,
                levelText);
        float x = (GameConfig.WORLD_WIDTH - layout.width) / 2;
        float y = (GameConfig.WORLD_HEIGHT + layout.height) / 2;

        font.draw(game.batch, levelText, x, y);

        // Restaurer l'échelle
        font.getData().setScale(oldScaleX, oldScaleY);

        game.batch.end();
    }

    private void drawHUD() {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Dessiner le score à gauche
        font.draw(game.batch, "Score: " + gameState.getScore(), 20, GameConfig.WORLD_HEIGHT - 15);

        // Dessiner les vies à droite
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
