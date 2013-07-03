package pacandroid.model.loader;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Grid;
import pacandroid.model.Level;

public class LevelLoader {

    private static final String LEVELS_FILE = "builtin-levels.txt";
    private final LevelFileReader reader = new LevelFileReader();
    private final String[] files;
    private int currentLevel = 0;

    public LevelLoader() {
        files = loadBuiltinLevels();
    }

    private String[] loadBuiltinLevels() {
        try {
            FileHandle levelsFile = Gdx.files.classpath(LEVELS_FILE);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                    levelsFile.read()));
            try {
                List<String> lines = new ArrayList<String>();
                String line;

                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }

                System.out.println(lines);

                return lines.toArray(new String[lines.size()]);
            } finally {
                br.close();
            }
        } catch (IOException ioe) {
            throw new LevelLoaderException("Couldn't read levels file", ioe);
        }
    }

    public Level loadNextLevel() {
        Level l = loadLevel(files[currentLevel]);
        currentLevel++;
        return l;
    }

    private Level loadLevel(String levelFile) {
        FileHandle handle;

        if (System.getProperty("level-file") != null) {
            levelFile = System.getProperty("level-file");
            handle = Gdx.files.absolute(levelFile);
        } else {
            handle = Gdx.files.classpath(levelFile);
        }

        if (!handle.exists()) {
            handle = Gdx.files.absolute(levelFile);

            if (!handle.exists()) {
                handle = Gdx.files.absolute("test-data/" + levelFile);
            }
        }

        if (!handle.exists())
            throw new LevelLoaderException("Can not find level: " + levelFile);

        try {
            InputStream in = handle.read();
            try {
                Level level = reader.readLevel(in, levelFile);
                setupLevel(level);

                return level;
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new LevelLoaderException("Unable to read level file: " + levelFile, e);
        }
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
