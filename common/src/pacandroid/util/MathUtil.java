/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.util;

/**
 *
 * @author lachlan
 */
public final class MathUtil {

    public static int clamp(int in, int min, int max) {
        return Math.min(max, Math.max(min, in));
    }

    public static float clamp(float in, float min, float max) {
        return Math.min(max, Math.max(min, in));
    }

    public static long clamp(long in, long min, long max) {
        return Math.min(max, Math.max(min, in));
    }

    public static double clamp(double in, double min, double max) {
        return Math.min(max, Math.max(min, in));
    }

    private MathUtil() {
    }
}
