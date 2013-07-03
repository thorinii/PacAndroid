package pacandroid.model.loader;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Grid;
import pacandroid.model.Level;

public class LevelLoader {

    public Level loadBuiltinLevel(int levelIndex) {
        String levelFile = "tims-level.palvl";
        InputStream in;

        try {
            FileHandle handle;

            if (System.getProperty("level-file") != null) {
                levelFile = System.getProperty("level-file");
                handle = Gdx.files.absolute(levelFile);
            } else {
                handle = Gdx.files.classpath(levelFile);
            }

            System.out.println(handle.name() + " " + handle.exists());

            if (!handle.exists()) {
                handle = Gdx.files.absolute(levelFile);
                System.out.println(handle.name() + " " + handle.exists());

                if (!handle.exists()) {
                    handle = Gdx.files.absolute("test-data/" + levelFile);

                    System.out.println(handle.name() + " " + handle.exists());
                }
            }

            if (!handle.exists())
                throw new LevelLoaderException(
                        "Can not find level: " + levelFile);

            in = handle.read();
            try {
                LevelFileReader reader = new LevelFileReader();

                Level level = reader.readLevel(in);
                setupLevel(level);

                return level;
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to read level file: " + levelFile,
                                       e);
        }
    }

    public Level loadCustomLevel(String name) {
        throw new UnsupportedOperationException();
    }

    protected void setupLevel(Level level) {
        spawnAndroid(level);
    }

    private void spawnAndroid(Level l) {
        Grid g = l.getGrid();
        int x = -1;
        int y = -1;

        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                if (g.get(i, j) == Grid.GRID_ANDROID_SPAWN) {
                    x = i;
                    y = j;

                    break;
                }
            }
        }

        if (x != -1 && y != -1) {
            AndyAndroid entity = new AndyAndroid(g, l);

            entity.setPosition(new Vector2(x * Level.GRID_UNIT_SIZE, y
                    * Level.GRID_UNIT_SIZE));

            l.spawnEntity(entity);
        } else {
            throw new IllegalStateException("Could not find Android Spawner");
        }
    }
}
