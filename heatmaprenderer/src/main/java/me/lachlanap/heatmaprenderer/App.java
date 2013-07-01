package me.lachlanap.heatmaprenderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class App {

    public static void main(String[] args) throws IOException {
        Path cwd = Paths.get("./maps/");
        List<Path> heatmaps = new ArrayList<Path>();
        List<Path> deathmaps = new ArrayList<Path>();

        for (Path sub : Files.newDirectoryStream(cwd)) {
            String filename = sub.getName(sub.getNameCount() - 1).toString();

            if (!filename.endsWith(".dat"))
                continue;

            if (filename.startsWith("heatmap")) {
                heatmaps.add(sub);
            } else if (filename.startsWith("deathmap")) {
                deathmaps.add(sub);
            }
        }

        doMaps(heatmaps, "heatmap");
        doMaps(deathmaps, "deathmap");
    }

    private static void doMaps(List<Path> maps, String name) throws IOException {
        File out = new File(name + ".png");

        HeatRenderer heatRenderer = new HeatRenderer();
        int[][] map;
        Iterator<Path> it = maps.iterator();

        if (maps.isEmpty())
            return;
        else {
            Path path = it.next();
            System.out.println("Loading: " + path);
            map = heatRenderer.load(path.toString());
        }

        for (; it.hasNext();) {
            Path path = it.next();
            System.out.println("Loading: " + path);
            heatRenderer.loadAppend(path.toString());
        }

        BufferedImage heatmap = heatRenderer.process();
        compositeOnMap(map, heatmap, out);
    }

    private static void doMap(String filename) throws IOException {
        File file = new File(filename);

        String outFilename = file.getCanonicalPath();
        outFilename = outFilename.substring(0, outFilename.lastIndexOf('.'));
        File out = new File(outFilename + ".png");

        HeatRenderer heatRenderer = new HeatRenderer();
        int[][] map = heatRenderer.load(filename);
        BufferedImage heatmap = heatRenderer.process();
        compositeOnMap(map, heatmap, out);
    }

    private static void compositeOnMap(int[][] map, BufferedImage heatmap, File out) throws IOException {
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
