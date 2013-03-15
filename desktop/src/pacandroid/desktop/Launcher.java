/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
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
        new LwjglApplication(new PacAndroidGame(), "PacAndroid", 1280, 800, true);
    }
}
