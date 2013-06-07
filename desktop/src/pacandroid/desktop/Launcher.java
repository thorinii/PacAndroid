/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.lachlanap.lct.gui.LCTFrame;
import pacandroid.AppLog;
import pacandroid.DesktopLog;
import pacandroid.PacAndroidGame;

/**
 *
 * @author lachlan
 */
public class Launcher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1)
            System.setProperty("level-file", args[0]);

        AppLog.init(new DesktopLog());

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "PacAndroid";
        config.width = 1280;
        config.height = 800;
        config.useGL20 = true;

        PacAndroidGame game = new PacAndroidGame();

        LCTFrame frame = new LCTFrame(game.getLCTManager());
        frame.setVisible(true);

        LwjglApplication app = new LwjglApplication(game, config);
    }
}
