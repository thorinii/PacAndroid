/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid;

import com.badlogic.gdx.Game;
import pacandroid.model.loader.LevelLoader;
import pacandroid.screens.GameScreen;
import pacandroid.screens.MainMenuScreen;

/**
 *
 * @author lachlan
 */
public class PacAndroidGame extends Game {

    private final LevelLoader loader;

    public PacAndroidGame() {
        this(null);
    }

    public PacAndroidGame(LevelLoader loader) {
        this.loader = loader;
    }

    @Override
    public void create() {
        mainMenu();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void play() {
        if (loader != null)
            setScreen(new GameScreen(this, loader));
        else
            setScreen(new GameScreen(this));
    }

    public void mainMenu() {
        setScreen(new MainMenuScreen(this));
    }
}
