/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import pacandroid.controller.LevelController;
import pacandroid.controller.SteeringController;
import pacandroid.model.LevelState;
import pacandroid.model.loader.LevelLoader;
import pacandroid.view.DefaultLevelRenderer;
import pacandroid.view.LevelRenderer;
import pacandroid.model.Level;
import pacandroid.PacAndroidGame;

/**
 *
 * @author lachlan
 */
public class GameScreen extends AbstractScreen {

    /**
     * The size of 1 grid square (or 2 units): 32px
     */
    public static final int GRID_UNIT = 55;
    public static final float MIN_DELTA = 0.015f;
    public static final float REGULAR_DELTA = 0.02f;
    private final LevelLoader loader;
    private Level level;
    private LevelController controller;
    private LevelRenderer[] renderers;
    private SteeringController steeringController;
    private LevelState levelState;
    //
    private float lastSmallDelta;

    public GameScreen(PacAndroidGame game) {
        this(game, new LevelLoader());
    }

    public GameScreen(PacAndroidGame game, LevelLoader loader) {
        super(game);
        this.loader = loader;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (delta >= MIN_DELTA || lastSmallDelta >= REGULAR_DELTA) {
            updateLevel(REGULAR_DELTA);
            lastSmallDelta = 0;
        } else {
            updateLevel(MIN_DELTA);
            lastSmallDelta += delta;
        }

        renderLevel(REGULAR_DELTA);
    }

    private void updateLevel(float delta) {
        try {
            if (!levelState.getCurrentPowerup().freeze
                    && !levelState.isGameOver()) {
                level.update(delta);
            }
            level.removeDead();

            levelState.updatePowerups(delta);
            controller.update(delta);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void renderLevel(float delta) {
        for (LevelRenderer renderer : renderers)
            renderer.render(delta);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputHandler());

        levelState = new LevelState();
        level = loader.loadBuiltinLevel(0, levelState);

        controller = new LevelController(level, levelState);


        steeringController = new SteeringController(level, controller,
                                                    getScreenSize());
        steeringController.setRoot(new Vector2(150, 150));


        DefaultLevelRenderer renderer = new DefaultLevelRenderer(
                (int) getScreenSize().x, (int) getScreenSize().y,
                level, levelState);
        renderer.setSteeringController(steeringController);

        renderers = new LevelRenderer[]{
            renderer
        };
    }

    class InputHandler implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Keys.LEFT:
                    controller.leftPressed();
                    break;
                case Keys.RIGHT:
                    controller.rightPressed();
                    break;
                case Keys.UP:
                    controller.upPressed();
                    break;
                case Keys.DOWN:
                    controller.downPressed();
                    break;
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Keys.LEFT:
                    controller.leftReleased();
                    break;
                case Keys.RIGHT:
                    controller.rightReleased();
                    break;
                case Keys.UP:
                    controller.upReleased();
                    break;
                case Keys.DOWN:
                    controller.downReleased();
                    break;
            }
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer,
                int button) {
            steeringController.touchDown(screenX, screenY);
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            steeringController.touchUp(screenX, screenY);

            if (levelState.isGameOver()) {
                gotoScreen(new GameOverScreen(getGame()));
            }

            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            steeringController.touchDragged(screenX, screenY);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return true;
        }

        @Override
        public boolean scrolled(int amount) {
            return true;
        }
    }
}
