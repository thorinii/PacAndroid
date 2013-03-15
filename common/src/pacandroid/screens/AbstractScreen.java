/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import pacandroid.PacAndroidGame;

/**
 *
 * @author lachlan
 */
public abstract class AbstractScreen implements Screen {

    private final PacAndroidGame game;
    private Vector2 screenSize;

    public AbstractScreen(PacAndroidGame game) {
        this.game = game;
    }

    public PacAndroidGame getGame() {
        return game;
    }

    public Vector2 getScreenSize() {
        return screenSize;
    }

    protected void gotoScreen(AbstractScreen screen) {
        game.setScreen(screen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
