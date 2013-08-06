package me.lachlanap.heatmaprenderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class App {

    public static void main(String[] args) throws IOException {
        Path maps = Paths.get("./maps/");
        Path output = Paths.get("./out/");

        for (Path sub : Files.newDirectoryStream(maps)) {
            if (Files.isDirectory(sub, LinkOption.NOFOLLOW_LINKS)) {
                processMapSet(sub, "desktop", output);
                processMapSet(sub, "android", output);
            }
        }
    }

    private static void processMapSet(Path setBase, String filter, Path output) throws IOException {
        String mapName = setBase.getName(setBase.getNameCount() - 1).toString();

        List<Path> heatmaps = new ArrayList<Path>();
        List<Path> deathmaps = new ArrayList<Path>();
        List<Path> stats = new ArrayList<Path>();

        for (Path sub : Files.newDirectoryStream(setBase)) {
            String filename = sub.getName(sub.getNameCount() - 1).toString();

            if (!filename.endsWith(".dat") && !filename.endsWith(".txt"))
                continue;
            if (!filename.contains(filter))
                continue;

            if (filename.startsWith("heatmap")) {
                heatmaps.add(sub);
            } else if (filename.startsWith("deathmap")) {
                deathmaps.add(sub);
            } else if (filename.startsWith("stat")) {
                stats.add(sub);
            }
        }

        if (!stats.isEmpty())
            doStats(stats, output.resolve(mapName + "-stats-" + filter + ".txt"));
        if (!heatmaps.isEmpty())
            doMaps(heatmaps, output.resolve(mapName + "-heatmap-" + filter + ".png"));
        if (!deathmaps.isEmpty())
            doMaps(deathmaps, output.resolve(mapName + "-deathmap-" + filter + ".png"));
    }

    private static void doStats(List<Path> maps, Path outputPath) throws IOException {
        StatCalculator calculator = new StatCalculator();
        calculator.loadAll(maps);
        calculator.write(outputPath);
    }

    private static void doMaps(List<Path> maps, Path outputPath) throws IOException {
        File out = outputPath.toFile();

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
