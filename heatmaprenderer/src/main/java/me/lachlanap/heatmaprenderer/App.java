package me.lachlanap.heatmaprenderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class App {

    public static void main(String[] args) throws IOException {
        doMap("./heatmap.dat");
        doMap("./deathmap.dat");
    }

    private static void doMap(String filename) throws IOException {
        File file = new File(filename);

        String outFilename = file.getCanonicalPath();
        outFilename = outFilename.substring(0, outFilename.lastIndexOf('.'));
        File out = new File(outFilename + ".png");

        HeatRenderer heatRenderer = new HeatRenderer();
        int[][] map = heatRenderer.load(filename);
        BufferedImage heatmap = heatRenderer.process();

        if (map == null) {
            System.out.println("No map data.");
            ImageIO.write(heatmap, "png", out);
        } else {
            System.out.println("Compositing onto map...");

            BufferedImage composite = new BufferedImage(heatmap.getWidth(), heatmap.getHeight(), heatmap.getType());
            Graphics2D g = (Graphics2D) composite.createGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, composite.getWidth(), composite.getHeight());

            int x = map.length;
            int y = map[0].length;
            float w = (float) composite.getWidth() / map.length;
            float h = (float) composite.getHeight() / map[0].length;

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    if (map[i][j] != 1 && map[i][j] != 4) {
                        g.setColor(Color.WHITE);
                        g.fillRect((int) (i * w), (int) (j * h), (int) w, (int) h);
                    }
                }
            }

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g.drawImage(heatmap, (int) w / 2, (int) -h / 2, null);
            g.dispose();

            ImageIO.write(composite, "png", out);
        }
    }
}
