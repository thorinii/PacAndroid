/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.util;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author lachlan
 */
public final class MathUtil {

    private MathUtil() {
    }

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

    public static float absMax(float a, float b) {
        return (Math.abs(a) > Math.abs(b)) ? a : b;
    }

    /**
     * Calculates the penetration. What this actually does, is calculate the shortest way out of the
     * collision, negated.
     */
    public static Vector2 collideAABB(Vector2 m1, Vector2 m2, Vector2 g1, Vector2 g2) {
        Vector2 mc = new Vector2((m1.x + m2.x) / 2, (m1.y + m2.y) / 2);
        Vector2 gc = new Vector2((g1.x + g2.x) / 2, (g1.y + g2.y) / 2);
        Vector2 t = gc.cpy().sub(mc);
        float aXExtent = (m2.x - m1.x) / 2;
        float bXExtent = (g2.x - g1.x) / 2;
        float xOverlap = aXExtent + bXExtent - Math.abs(t.x);
        float aYExtent = (m2.y - m1.y) / 2;
        float bYExtent = (g2.y - g1.y) / 2;
        float yOverlap = aYExtent + bYExtent - Math.abs(t.y);
        if (xOverlap < yOverlap) {
            return new Vector2((t.x < 0) ? -Math.max(0, xOverlap) : Math.max(0, xOverlap), 0);
        } else {
            return new Vector2(0, (t.y < 0) ? -Math.max(0, yOverlap) : Math.max(0, yOverlap));
        }
    }
}
