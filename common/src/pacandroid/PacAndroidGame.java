/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid;

import com.badlogic.gdx.Game;
import me.lachlanap.lct.LCTManager;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Apple;
import pacandroid.model.Level;
import pacandroid.model.loader.LevelLoader;
import pacandroid.screens.GameScreen;
import pacandroid.screens.MainMenuScreen;
import pacandroid.view.AndyAndroidRenderer;
import pacandroid.view.fonts.FontRenderer;

/**
 *
 * @author lachlan
 */
public class PacAndroidGame extends Game {

    private final LevelLoader loader;
    private final LCTManager manager;
    private FontRenderer fontRenderer;

    public PacAndroidGame() {
        this(null);
    }

    public PacAndroidGame(LevelLoader loader) {
        this.loader = loader;

        manager = new LCTManager();
        manager.register(AndyAndroid.class);
        manager.register(Apple.class);
        manager.register(Level.class);
        manager.register(AndyAndroidRenderer.class);
    }

    @Override
    public void create() {
        fontRenderer = new FontRenderer();
        fontRenderer.setFont("BenderSolid");

        //mainMenu();
        play();
    }

    public LCTManager getLCTManager() {
        return manager;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void play() {
        if (loader != null)
            setScreen(new GameScreen(this, loader, fontRenderer));
        else
            setScreen(new GameScreen(this, fontRenderer));
    }

    public void mainMenu() {
        setScreen(new MainMenuScreen(this));
    }
}
