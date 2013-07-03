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

    private LevelLoader loader;
    private FontRenderer fontRenderer;

    public PacAndroidGame() {
    }

    @Override
    public void create() {
        this.loader = new LevelLoader();
        
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
        Level level = loader.loadNextLevel();
        setScreen(new GameScreen(this, level, fontRenderer));
    }

    public void mainMenu() {
        setScreen(new MainMenuScreen(this));
    }
}
