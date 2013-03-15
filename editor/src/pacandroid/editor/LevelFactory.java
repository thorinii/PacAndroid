package pacandroid.editor;

import pacandroid.model.Grid;
import pacandroid.model.Level;

public final class LevelFactory {

    public static Level makeBlankLevel() {
        Level l = new Level();

        fillGrid(l.getGrid(), Grid.GRID_WALL);

        return l;
    }

    private static void fillGrid(Grid g, int type) {
        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                g.set(i, j, type);
            }
        }
    }
}
