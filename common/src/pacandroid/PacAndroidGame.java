/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid;

import com.badlogic.gdx.Game;
import pacandroid.model.Level;
import pacandroid.model.loader.LevelLoader;
import pacandroid.screens.GameScreen;
import pacandroid.screens.MainMenuScreen;
import pacandroid.view.fonts.FontRenderer;

/**
 *
 * @author lachlan
 */
public class PacAndroidGame extends Game {

    private final LevelLoader loader;
    private FontRenderer fontRenderer;

    public PacAndroidGame() {
        this.loader = new LevelLoader();
    }

    @Override
    public void create() {
        fontRenderer = new FontRenderer();
        fontRenderer.setFont("BenderSolid");

        //mainMenu();
        play();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void play() {
        Level level = loader.loadBuiltinLevel(0);
        setScreen(new GameScreen(this, level, fontRenderer));
    }

    public void mainMenu() {
        setScreen(new MainMenuScreen(this));
    }
}
