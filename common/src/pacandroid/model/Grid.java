package pacandroid.model;

import com.badlogic.gdx.math.Vector2;

public class Grid {

    public static final int GRID_EMPTY = 0;
    public static final int GRID_WALL = 1;
    public static final int GRID_JELLYBEAN = 2;
    public static final int GRID_ANDROID_SPAWN = 3;
    public static final int GRID_ENEMY_SPAWN = 4;
    public static final int GRID_POWERUP = 5;

    public static boolean isWall(int gridSpace) {
        return gridSpace == GRID_WALL || gridSpace == GRID_ENEMY_SPAWN;
    }
    private int[][] wallGrid;
    private int unitSize;

    public Grid(int size, int gridUnitSize) {
        this(size, size, gridUnitSize);
    }

    public Grid(int width, int height, int gridUnitSize) {
        wallGrid = new int[width][height];
        this.unitSize = gridUnitSize;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public int getWidth() {
        return wallGrid.length;
    }

    public int getHeight() {
        return wallGrid[0].length;
    }

    public int[][] getWallGrid() {
        return wallGrid;
    }

    public void set(int x, int y, int value) {
        wallGrid[x][y] = value;
    }

    public int get(int x, int y) {
        return wallGrid[x][y];
    }

    /**
     * Same as #get(int,int) - just casts the floats to ints.
     */
    public int get(float x, float y) {
        return wallGrid[(int) x][(int) y];
    }

    /**
     * Safe get. If (x,y) are in bounds, then returns the value, else returns
     * <code>def</code>.
     * <p/>
     */
    public int get(int x, int y, int def) {
        try {
            return get(x, y);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            return def;
        }
    }

    public Vector2 pointToGrid(Vector2 point) {
        return new Vector2((int) (point.x / unitSize + .5f),
                           (int) (point.y / unitSize + .5f));
    }

    public int getAt(float x, float y) {
        return get((int) (x / unitSize + .5f),
                   (int) (y / unitSize + .5f),
                   Grid.GRID_EMPTY);
    }
}
