package pacandroid.model.loader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import pacandroid.model.Grid;
import pacandroid.model.Level;

public class LevelFileReader {

    public Level readLevelFlipped(InputStream inputStream) throws IOException {
        Level level = readLevel(inputStream);

        Grid oldG = level.getGrid();
        Level l = new Level();

        for (int i = 0; i < oldG.getWidth(); i++) {
            for (int j = 0; j < oldG.getHeight(); j++) {
                l.getGrid().set(i, j, oldG.get(i, j));
            }
        }

        return l;
    }

    public Level readLevel(InputStream inputStream) throws IOException {
        return readLevel(inputStream, "test-level");
    }

    public Level readLevel(InputStream inputStream, String name) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);
        Level l = new Level(name);

        try {
            compareHeader(in);
            readGrid(in, l);
        } finally {
            in.close();
        }

        return l;
    }

    private void compareHeader(DataInputStream in) throws IOException {
        byte[] magic = new byte[4];
        int length = in.read(magic, 0, 4);

        if (length != 4)
            throw new LevelLoaderException("File not fully containing header");

        if (!Arrays.equals(LevelFileConstants.MAGIC, magic))
            throw new LevelLoaderException("File magic number not matching");

        if (in.readByte() != LevelFileConstants.VERSION)
            throw new LevelLoaderException("File version number not matching");
    }

    private void readGrid(DataInputStream in, Level l) throws IOException {
        Grid g = l.getGrid();

        for (int i = 0; i < Level.GRID_WIDTH; i++) {
            for (int j = 0; j < Level.GRID_HEIGHT; j++) {
                g.set(i, j, in.readByte());
            }
        }
    }
}
