package pacandroid.model.loader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pacandroid.model.Grid;
import pacandroid.model.Level;

public class LevelFileWriter {

    public void writeLevelFlipped(Level level, OutputStream outputStream)
            throws IOException {
        Grid oldG = level.getGrid();
        Level l = new Level();

        for (int i = 0; i < oldG.getWidth(); i++) {
            for (int j = 0; j < oldG.getHeight(); j++) {
                l.getGrid().set(i, j, oldG.get(i, j));
            }
        }

        writeLevel(l, outputStream);
    }

    public void writeLevel(Level level, OutputStream outputStream)
            throws IOException {
        DataOutputStream out = new DataOutputStream(outputStream);

        try {
            writeHeader(out);
            writeGrid(out, level);
        } finally {
            out.close();
        }
    }

    private void writeHeader(DataOutputStream out) throws IOException {
        out.write(LevelFileConstants.MAGIC);
        out.writeByte(LevelFileConstants.VERSION);
    }

    private void writeGrid(DataOutputStream out, Level l) throws IOException {
        Grid g = l.getGrid();

        for (int i = 0; i < Level.GRID_WIDTH; i++) {
            for (int j = 0; j < Level.GRID_HEIGHT; j++) {
                out.writeByte(g.get(i, j));
            }
        }
    }
}
