package me.lachlanap.heatmaprenderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HeatRenderer {

    private static final int SCALE = 5;
    private static final float DIST = 5f * SCALE;
    private static final float DIST2 = DIST * DIST;
    private static final int TRANSPARENT = new Color(0, true).getRGB();
    private int width = 24, height = 15;
    private int[][] data;

    public int[][] load(String file) throws IOException {
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                new FileInputStream(file)));
        int[][] worldMap = null;

        try {
            if (dis.readBoolean()) {
                int worldWidth = dis.readInt();
                int worldHeight = dis.readInt();

                worldMap = new int[worldWidth][worldHeight];
                for (int i = 0; i < worldWidth; i++) {
                    for (int j = 0; j < worldHeight; j++) {
                        worldMap[i][j] = dis.readInt();
                    }
                }
            }

            width = dis.readInt();
            height = dis.readInt();
            data = new int[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    data[i][j] = dis.readInt();
                }
            }
        } finally {
            dis.close();
        }

        System.out.println("Loaded Heatmap: " + width + "x" + height + " spaces.");

        return worldMap;
    }

    public BufferedImage process() {
        float[][] intensityMap = new float[width * SCALE][height * SCALE];
        float intensityMax;

        ForkJoinPool pool = new ForkJoinPool();
        IntensityMapGenerator intensityMapGenerator = new IntensityMapGenerator(data, intensityMap);
        intensityMax = pool.invoke(intensityMapGenerator);


        BufferedImage img = new BufferedImage(width * SCALE, height * SCALE,
                                              BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width * SCALE; i++) {
            for (int j = 0; j < height * SCALE; j++) {
                img.setRGB(i, j, map(intensityMap[i][j] / intensityMax));
            }
        }

        return img;
    }

    private float getValue(int x, int y) {
        float value = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float px = i * SCALE;
                float py = j * SCALE;

                float dist2 = (x - px) * (x - px) + (y - py) * (y - py);
                if (dist2 > DIST2)
                    continue;
                float dist = (float) Math.sqrt(dist2);

                int hits = data[i][j];
                value += hits * Math.max(0.001f, -dist + DIST) / DIST;
            }
        }
        return value;
    }

    private int map(float heat) {
        if (heat == 0)
            return TRANSPARENT;

        float hue = Math.min(1, 0.7f - (heat * 0.7f));
        float bright = (float) Math.min(1, Math.pow(heat, 1 / 2f) * 0.6 + 0.4);
        return Color.HSBtoRGB(hue, 1f, bright);
    }

    class IntensityMapGenerator extends RecursiveTask<Float> {

        private static final int THRESHOLD = 150;
        final int[][] data;
        final float[][] map;
        final int x, y, width, height;

        public IntensityMapGenerator(int[][] data, float[][] map) {
            this.data = data;
            this.map = map;
            x = 0;
            y = 0;
            width = map.length;
            height = map[0].length;
        }

        public IntensityMapGenerator(int[][] data, float[][] map, int x, int y, int width, int height) {
            this.data = data;
            this.map = map;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Float compute() {
            if (width <= THRESHOLD) {
                if (height <= THRESHOLD) {
                    return computeDirectly();
                } else {
                    int pieceHeight = height / 2;

                    IntensityMapGenerator img1 = new IntensityMapGenerator(data, map,
                                                                           x, y,
                                                                           width, pieceHeight);
                    IntensityMapGenerator img2 = new IntensityMapGenerator(data, map,
                                                                           x, y + pieceHeight,
                                                                           width, height - pieceHeight);
                    img2.fork();
                    float c1 = img1.compute();
                    float c2 = img2.join();
                    return Math.max(c1, c2);
                }
            } else {
                int pieceWidth = width / 2;

                IntensityMapGenerator img1 = new IntensityMapGenerator(data, map,
                                                                       x, y,
                                                                       pieceWidth, height);
                IntensityMapGenerator img2 = new IntensityMapGenerator(data, map,
                                                                       x + pieceWidth, y,
                                                                       width - pieceWidth, height);
                img2.fork();
                float c1 = img1.compute();
                float c2 = img2.join();
                return Math.max(c1, c2);
            }
        }

        private float computeDirectly() {
            float intensityMax = 0;
            for (int i = x; i < (x + width); i++) {
                for (int j = y; j < (y + height); j++) {
                    float value = getValue(i, j);
                    map[i][j] = value;

                    intensityMax = Math.max(intensityMax, value);
                }
            }

            return intensityMax;
        }
    }
}
