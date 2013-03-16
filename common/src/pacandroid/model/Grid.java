package pacandroid.model;

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
    private int gridUnitSize;

    public Grid(int size, int gridUnitSize) {
        this(size, size, gridUnitSize);
    }

    public Grid(int width, int height, int gridUnitSize) {
        wallGrid = new int[width][height];
        this.gridUnitSize = gridUnitSize;
    }

    public int getUnitSize() {
        return gridUnitSize;
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
}