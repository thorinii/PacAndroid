/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 *
 * @author lachlan
 */
public class GUI {

    private final Stage stage;
    private Texture background;
    private final Vector2 size;

    public GUI(int width, int height) {
        size = new Vector2(width, height);
        stage = new Stage(width, height, false);
    }

    public void resize(int width, int height) {
        stage.setViewport(size.x, size.y, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.
                getGutterHeight(), 0);
    }

    public void enable() {
        Gdx.input.setInputProcessor(stage);
    }

    public void disable() {
        Gdx.input.setInputProcessor(null);
    }

    public void dispose() {
        stage.dispose();
    }

    public void setBackground(Texture background) {
        this.background = background;
    }

    public void draw() {
        if (background != null) {
            SpriteBatch batch = stage.getSpriteBatch();
            batch.begin();
            batch.draw(background, 0, 0);
            batch.end();
        }

        stage.draw();
    }

    public void add(Button button) {
        stage.addActor(button);
    }
}
