package pacandroid.model.loader;

import java.io.InputStream;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Grid;
import pacandroid.model.Level;
import pacandroid.model.LevelState;

public class LevelLoader {

    public Level loadBuiltinLevel(int levelIndex, LevelState levelState) {
        String levelFile = "symmetrical-level1.palvl";
        InputStream in;

        try {
            FileHandle handle;

            if (System.getProperty("level-file") != null) {
                levelFile = System.getProperty("level-file");
                handle = Gdx.files.absolute(levelFile);
            } else {
                handle = Gdx.files.classpath(levelFile);
            }

            if (!handle.exists()) {
                handle = Gdx.files.absolute(levelFile);

                if (!handle.exists())
                    handle = Gdx.files.absolute("test-data/" + levelFile);
            }

            if (!handle.exists())
                throw new LevelLoaderException(
                        "Can not find level: " + levelFile);

            in = handle.read();
            try {
                LevelFileReader reader = new LevelFileReader();

                Level level = reader.readLevel(in);
                spawnAndroid(level, levelState);

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

    private Level createLevel() {
        Level l = new Level();
        return l;
    }

    private void fillGrid(Level l) {
        Grid g = l.getGrid();

        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                if ((i ^ j + i + j * j) < 10) {
                    g.set(i, j, Grid.GRID_WALL);
                } else {
                    g.set(i, j, Grid.GRID_JELLYBEAN);
                }
            }
        }


        Random r = new Random();

        int x = r.nextInt(g.getWidth());
        int y = r.nextInt(g.getHeight());

        g.set(x, y, Grid.GRID_ANDROID_SPAWN);
    }

    private void spawnAndroid(Level l, LevelState levelState) {
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
            AndyAndroid entity = new AndyAndroid(g, levelState);

            entity.setPosition(new Vector2(x * Level.GRID_UNIT_SIZE, y
                    * Level.GRID_UNIT_SIZE));

            l.spawnEntity(entity);
        } else {
            throw new IllegalStateException("Could not find Android Spawner");
        }
    }
}
