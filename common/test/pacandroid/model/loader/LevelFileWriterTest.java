package pacandroid.model.loader;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import pacandroid.model.Level;
import pacandroid.model.loader.LevelFileReader;
import pacandroid.model.loader.LevelFileWriter;

public class LevelFileWriterTest {

    @Test
    public void testCanWriteAndIsValid() {
        try {
            OutputStream out = new FileOutputStream(
                    "test-data/testlevelwrite.palvl");

            try {
                LevelFileWriter writer = new LevelFileWriter();
                Level l = new Level();

                writer.writeLevel(l, out);
            } finally {
                out.close();
            }

            InputStream in = new FileInputStream(
                    "test-data/testlevelwrite.palvl");
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
