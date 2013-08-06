package me.lachlanap.heatmaprenderer;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author lachlan
 */
public class StatCalculator {

    private final TIntList apples;
    private final TIntList jellybeans;
    private final TIntList powerups;
    private final TFloatList time;

    public StatCalculator() {
        apples = new TIntArrayList();
        jellybeans = new TIntArrayList();
        powerups = new TIntArrayList();
        time = new TFloatArrayList();
    }

    public void loadAll(List<Path> maps) throws IOException {
        Properties props = new Properties();

        for (Path path : maps) {
            BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));

            try {
                props.load(reader);
            } finally {
                reader.close();
            }

            try {
                apples.add(Integer.parseInt(props.getProperty("apples")));
                jellybeans.add(Integer.parseInt(props.getProperty("jellybeans")));
                powerups.add(Integer.parseInt(props.getProperty("powerups")));
                time.add(Float.parseFloat(props.getProperty("time")));
            } catch (NumberFormatException nfe) {
                System.err.println("Malformed stats file (" + path + "): " + nfe.toString());
            }

            props.clear();
        }
    }

    public void write(Path outputPath) throws IOException {
        apples.sort();
        jellybeans.sort();
        powerups.sort();
        time.sort();

        IntStatistics appleStats = new IntStatistics(apples);
        IntStatistics jellybeanStats = new IntStatistics(jellybeans);
        IntStatistics powerupsStats = new IntStatistics(powerups);
        FloatStatistics timeStats = new FloatStatistics(time);

        System.out.println("Apples: " + appleStats);
        System.out.println("Time: " + timeStats);

        Properties dataOut = new Properties();
        appleStats.put(dataOut, "apples");
        jellybeanStats.put(dataOut, "jellybeans");
        powerupsStats.put(dataOut, "powerups");
        timeStats.put(dataOut, "time");

        BufferedWriter writer = Files.newBufferedWriter(outputPath, Charset.forName("UTF-8"),
                                                        StandardOpenOption.CREATE);
        try {
            dataOut.store(writer, "PacAndroid Level Statistics");
        } finally {
            writer.close();
        }
    }

    private abstract class Statistics {

        private float min;
        private float max;
        private float avg;
        private float stddev;
        private float percentile10;
        private float percentile90;

        void init(int count, float min, float max, float total, float totalSq, float percentile10, float percentile90) {
            this.min = min;
            this.max = max;
            this.percentile10 = percentile10;
            this.percentile90 = percentile90;

            if (count > 1) {
                avg = total / count;
                stddev = (float) Math.sqrt(
                        (count * totalSq - total * total) / (count * (count - 1)));
            } else {
                avg = total;
                stddev = 0;
            }
        }

        @Override
        public String toString() {
            return "[" + min + " < " + avg + " < " + max
                    + " (+/-" + stddev + ": {" + (avg - stddev) + ", " + (avg + stddev) + "}"
                    + ", 10%: " + percentile10
                    + ", 90%: " + percentile90 + ")]";
        }

        public void put(Properties props, String base) {
            props.setProperty(base + ".min", String.valueOf(min));
            props.setProperty(base + ".max", String.valueOf(max));
            props.setProperty(base + ".avg", String.valueOf(avg));
            props.setProperty(base + ".stddev", String.valueOf(stddev));
            props.setProperty(base + ".per10", String.valueOf(percentile10));
            props.setProperty(base + ".per90", String.valueOf(percentile90));
        }
    }

    private class FloatStatistics extends Statistics {

        public FloatStatistics(TFloatList list) {
            int n = list.size();

            float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
            float total = 0;
            float totalSq = 0;

            TFloatIterator itr = list.iterator();
            for (; itr.hasNext();) {
                float f = itr.next();

                min = Math.min(min, f);
                max = Math.max(max, f);

                total += f;
                totalSq += f * f;
            }


            float rank10 = 10f / 100f * (n - 1);
            int rI10 = (int) rank10;
            float rF10 = rank10 - rI10;
            float percentile10;

            float rank90 = 90f / 100f * (n - 1);
            int rI90 = (int) rank90;
            float rF90 = rank90 - rI90;
            float percentile90;

            if (n > 1) {
                percentile10 =
                        (1 - rF10) * list.get(rI10) + rF10 * list.get(rI10 + 1);
                percentile90 =
                        (1 - rF90) * list.get(rI90) + rF90 * list.get(rI90 + 1);
            } else {
                percentile10 = total;
                percentile90 = total;
            }

            init(n,
                 min, max,
                 total, totalSq,
                 percentile10,
                 percentile90);
        }
    }

    private class IntStatistics extends Statistics {

        public IntStatistics(TIntList list) {
            int n = list.size();

            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
            float total = 0;
            float totalSq = 0;

            TIntIterator itr = list.iterator();
            for (; itr.hasNext();) {
                int i = itr.next();

                min = Math.min(min, i);
                max = Math.max(max, i);

                total += i;
                totalSq += i * i;
            }


            float rank10 = 10f / 100f * (n - 1);
            int rI10 = (int) rank10;
            float rF10 = rank10 - rI10;
            float percentile10;

            float rank90 = 90f / 100f * (n - 1);
            int rI90 = (int) rank90;
            float rF90 = rank90 - rI90;
            float percentile90;

            if (n > 1) {
                percentile10 =
                        (1 - rF10) * list.get(rI10) + rF10 * list.get(rI10 + 1);
                percentile90 =
                        (1 - rF90) * list.get(rI90) + rF90 * list.get(rI90 + 1);
            } else {
                percentile10 = total;
                percentile90 = total;
            }

            init(n,
                 min, max,
                 total, totalSq,
                 percentile10,
                 percentile90);
        }
    }
}
