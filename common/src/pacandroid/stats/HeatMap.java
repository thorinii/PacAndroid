/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.stats;

import com.badlogic.gdx.math.Vector2;
import java.io.DataOutputStream;
import java.io.IOException;
import pacandroid.model.Grid;

/**
 * Stores a heatmap of positions in the grid.
 */
public class HeatMap {

    private final int[][] map;
    private final int width, height;
    private final float ppu;

    public HeatMap(int width, int height, float ppu) {
        this.width = (int) (width * ppu);
        this.height = (int) (height * ppu);
        map = new int[this.width][this.height];
        this.ppu = ppu;
    }

    public void addPoint(Vector2 point) {
        addPoint((int) (point.x * ppu), (int) (point.y * ppu));
    }

    public void addPoint(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            map[x][y]++;
        } else {
            System.out.println(x + " " + y);
            // silently absorb invalid point
        }
    }

    public void writeOut(DataOutputStream out) throws IOException {
        writeOut(out, null);
    }

    public void writeOut(DataOutputStream out, Grid grid) throws IOException {
        if (grid == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(grid.getWidth());
            out.writeInt(grid.getHeight());

            for (int i = 0; i < grid.getWidth(); i++) {
                for (int j = grid.getHeight() - 1; j >= 0; j--) {
                    out.writeInt(grid.get(i, j));
                }
            }
        }

        out.writeInt(width);
        out.writeInt(height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                out.writeInt(map[i][j]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[HeatMap ").append(width).append('x').append(height).append('\n');

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++)
                builder.append(map[i][j]).append(' ');

            builder.append('\n');
        }

        builder.append(']');

        return builder.toString();
    }
}
