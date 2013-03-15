/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.model.loader;

import java.io.IOException;

/**
 *
 * @author lachlan
 */
public class LevelLoaderException extends IOException {

    public LevelLoaderException(String msg) {
        super(msg);
    }

    public LevelLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
