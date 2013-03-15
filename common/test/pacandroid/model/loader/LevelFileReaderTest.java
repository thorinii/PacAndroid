/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.model.loader;

import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;
import pacandroid.model.Level;

/**
 *
 * @author lachlan
 */
public class LevelFileReaderTest {

    @Test
    public void test() {
        try {
            InputStream in = new FileInputStream(
                    "test-data/symmetrical-level1.palvl");

            try {
                LevelFileReader reader = new LevelFileReader();

                Level l = reader.readLevel(in);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            fail(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}