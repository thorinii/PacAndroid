package pacandroid.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pacandroid.controller.SteeringController;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Apple;
import pacandroid.model.Entity;
import pacandroid.model.Grid;
import pacandroid.model.Level;
import pacandroid.model.LevelState;
import pacandroid.model.LevelState.Powerup;
import pacandroid.util.Timer;
import pacandroid.util.Timers;
import pacandroid.view.fonts.FontRenderer;

public class DefaultLevelRenderer implements LevelRenderer {

    private static final double SCREEN_RATIO = 5.0 / 8.0;
    private static final float FADE_TIME = 1.5f;
    //
    private final OrthographicCamera cam;
    private final SpriteBatch spriteBatch;
    //
    private FontRenderer fontRenderer;
    private SteeringControllerView steeringControllerView;
    private final Map<Class<? extends Entity>, EntityRenderer> entityRenderers;
    //
    private final Level level;
    private final LevelState levelState;
    //
    private Texture wallTexture;
    private Texture scoreIconTexture;
    private Texture lifeIconTexture;
    private Texture[] jellybeanTextures;
    private Texture[] powerupTextures;
    //
    private final float width, height;
    private float offsetX, offsetY;
    private float ppuW, ppuH;
    //
    private Timer powerupShowTimer;
    private Powerup currentPowerup;

    public DefaultLevelRenderer(int width, int height, Level level,
            LevelState levelState) {
        if (width > (height * SCREEN_RATIO)) {
            this.width = width;
            this.height = (float) (width * SCREEN_RATIO);
        } else if (width < (height * SCREEN_RATIO)) {
            this.width = (float) (height / SCREEN_RATIO);
            this.height = height;
        } else {
            this.width = width;
            this.height = height;
        }

        this.offsetX = width / 2 - this.width / 2;
        this.offsetY = height / 2 - this.height / 2;


        this.ppuW = this.width / level.getWorldWidth();
        this.ppuH = this.height / level.getWorldHeight();
        this.level = level;

        this.levelState = levelState;

        this.cam = new OrthographicCamera(this.width, this.height);
        this.cam.position
                .set(this.width / 2 - level.getGridUnitSize() / 2 * ppuW,
                     this.height / 2 - level.getGridUnitSize() / 2 * ppuH, 0);
        this.cam.update();

        this.spriteBatch = new SpriteBatch();

        this.entityRenderers = new HashMap<Class<? extends Entity>, EntityRenderer>();

        loadColoursAndTextures();

        registerRenderers();
    }

    public void setSteeringController(SteeringController steeringController) {
        steeringControllerView = new SteeringControllerView(steeringController);
    }

    private void loadColoursAndTextures() {
        wallTexture = new Texture(Gdx.files.classpath("640x/wall-32x32.png"));
        scoreIconTexture = new Texture(Gdx.files.classpath("score.png"));
        lifeIconTexture = new Texture(Gdx.files.classpath("heart.png"));

        jellybeanTextures = new Texture[7];
        for (int i = 0; i < jellybeanTextures.length; i++) {
            jellybeanTextures[i] = new Texture(Gdx.files
                    .classpath("640x/jellybean-32x32-" + (i + 1) + ".png"));
        }

        powerupTextures = new Texture[5];
        for (int i = 0; i < powerupTextures.length; i++) {
            powerupTextures[i] = new Texture(Gdx.files
                    .classpath("640x/icecream-32x32-" + (i + 1) + ".png"));
        }

        fontRenderer = new FontRenderer();
        fontRenderer.setFont("BenderSolid");
    }

    private void registerRenderers() {
        entityRenderers.put(AndyAndroid.class, new AndyAndroidRenderer());
        entityRenderers.put(Apple.class, new AppleRenderer());
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }

    public float getPPUWidth() {
        return ppuW;
    }

    public float getPPUHeight() {
        return ppuH;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();

        drawGrid();
        drawEntities();

        if (!levelState.isGameOver()) {
            drawSteeringController();
            drawScores();
        }

        drawMessages();

        spriteBatch.end();
    }

    private void drawGrid() {
        Grid grid = level.getGrid();
        int gUSize = grid.getUnitSize();
        int gHUSize = gUSize / 2;

        for (int i = 0; i < level.getGridWidth(); i++) {
            for (int j = 0; j < level.getGridHeight(); j++) {
                float xLoc = i * gUSize * ppuW + offsetX;
                float yLoc = j * gUSize * ppuH + offsetY;

                int gridSpace = grid.get(i, j);

                if (Grid.isWall(gridSpace)) {
                    spriteBatch.draw(wallTexture, xLoc, yLoc, ppuW * gUSize,
                                     ppuH * gUSize);
                } else if (gridSpace == Grid.GRID_JELLYBEAN) {
                    spriteBatch.draw(jellybeanTextures[
                                     hash(i, j,
                                                            jellybeanTextures.length)],
                                     xLoc, yLoc,
                                     ppuW * gUSize, ppuH * gUSize);
                } else if (gridSpace == Grid.GRID_POWERUP) {
                    spriteBatch.draw(powerupTextures[
                                     hash(i, j,
                                                          powerupTextures.length)],
                                     xLoc, yLoc,
                                     ppuW * gUSize, ppuH * gUSize);
                }
            }
        }
    }

    private void drawEntities() {
        for (Entity entity : level.getEntities()) {
            entityRenderers.get(entity.getClass())
                    .renderEntity(entity, spriteBatch, this);
        }
    }

    private void drawSteeringController() {
        if (steeringControllerView != null)
            steeringControllerView.draw(spriteBatch);
    }

    private void drawScores() {
        int graphicY = 710;

        spriteBatch.draw(scoreIconTexture, offsetX + 10, offsetY + graphicY);

        fontRenderer.setColor(Color.RED);
        fontRenderer.drawString(levelState.getScore().toString(), spriteBatch,
                                (int) offsetX + 60, (int) offsetY + graphicY);

        for (int i = 0; i < levelState.getLives(); i++)
            spriteBatch.draw(lifeIconTexture, offsetX + 100 + i * 42, offsetY
                    + graphicY + 40);
    }

    private void drawMessages() {
        if (levelState.isGameOver()) {
            String message;

            if (levelState.didPlayerWin()) {
                message = "You Made It!";
            } else {
                message = "Game Over";
            }

            fontRenderer.drawStringCentred(message,
                                           spriteBatch,
                                           (int) width / 2,
                                           (int) height / 2);
        } else {
            Powerup powerup = levelState.getCurrentPowerup();
            float showTime;

            // Check for a new powerup... just make sure its not Null
            if (powerup != currentPowerup && powerup != Powerup.Null) {
                currentPowerup = powerup;
                showTime = Math.max(0.2f, powerup.buffMillis / 1000f);

                powerupShowTimer = Timers.getSingleTimer(showTime);
            } else {
                showTime = Math.max(0.2f, currentPowerup.buffMillis / 1000f);
            }

            if (powerupShowTimer != null) {
                // We don't want to tell everyone the level is frozen at the start
                if (!currentPowerup.isHuman())
                    return;

                if (powerupShowTimer.beforeTick()) {
                    fontRenderer.setColor(Color.WHITE);
                    fontRenderer.drawStringCentred(currentPowerup.name,
                                                   spriteBatch,
                                                   (int) width / 2,
                                                   (int) height / 2);
                } else {
                    float fadeTime = powerupShowTimer.getTime() - showTime;
                    float fade = Math.max(0, (FADE_TIME - fadeTime) / FADE_TIME);

                    fontRenderer.setColor(new Color(1, 1, 1, fade));
                    fontRenderer.drawStringCentred(currentPowerup.name,
                                                   spriteBatch,
                                                   (int) width / 2,
                                                   (int) height / 2);
                    fontRenderer.setColor(Color.WHITE);

                    if (fade == 0) {
                        currentPowerup = powerup;
                        powerupShowTimer = null;
                    }
                }
            }
        }
    }

    private int hash(int x, int y, int length) {
        int hash = 23;

        hash = hash * 31 + x;
        hash = hash * 31 + y;

        return hash % length;
    }
}
