/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.util;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lachlan
 */
public class MathUtilTest {

    public MathUtilTest() {
    }

    @Test
    public void testClampInRange() {
        assertEquals(4, MathUtil.clamp(4, 2, 6));
    }

    @Test
    public void testClampAboveRange() {
        assertEquals(6, MathUtil.clamp(8, 2, 6));
    }

    @Test
    public void testClampBelowRange() {
        assertEquals(2, MathUtil.clamp(0, 2, 6));
    }

    @Test
    public void testAbsMax() {
        assertEquals(2, MathUtil.absMax(2, 1), 0);
        assertEquals(2, MathUtil.absMax(2, -1), 0);
        assertEquals(-2, MathUtil.absMax(-2, 1), 0);
        assertEquals(3, MathUtil.absMax(-2, 3), 0);
    }

    @Test
    public void testAABB() {
        Vector2 a1, a2, b1, b2;

        a1 = new Vector2(0, 0);
        a2 = new Vector2(2, 2);
        b1 = new Vector2(3, 3);
        b2 = new Vector2(5, 5);
        assertEquals("AABB no collision", Vector2.Zero, MathUtil.collideAABB(a1, a2, b1, b2));

        a1 = new Vector2(0, 0);
        a2 = new Vector2(2, 2);
        b1 = new Vector2(1, 0);
        b2 = new Vector2(3, 2);
        assertEquals("AABB left x", new Vector2(1, 0), MathUtil.collideAABB(a1, a2, b1, b2));
        assertEquals("AABB right x", new Vector2(-1, 0), MathUtil.collideAABB(b1, b2, a1, a2));

        a1 = new Vector2(0, 0);
        a2 = new Vector2(2, 2);
        b1 = new Vector2(0, 1);
        b2 = new Vector2(2, 3);
        assertEquals("AABB below y", new Vector2(0, 1), MathUtil.collideAABB(a1, a2, b1, b2));
        assertEquals("AABB above y", new Vector2(0, -1), MathUtil.collideAABB(b1, b2, a1, a2));

        a1 = new Vector2(0, 0);
        a2 = new Vector2(2, 2);
        b1 = new Vector2(1.5f, 1f);
        b2 = new Vector2(3.5f, 3f);
        assertEquals("AABB bottom left", new Vector2(0.5f, 0), MathUtil.collideAABB(a1, a2, b1, b2));
        assertEquals("AABB top right", new Vector2(-0.5f, 0), MathUtil.collideAABB(b1, b2, a1, a2));

        a1 = new Vector2(0, 0);
        a2 = new Vector2(2, 2);
        b1 = new Vector2(-1.5f, 1f);
        b2 = new Vector2(0.5f, 3);
        assertEquals("AABB bottom right", new Vector2(-0.5f, 0), MathUtil.collideAABB(a1, a2, b1, b2));
        assertEquals("AABB top left", new Vector2(0.5f, 0), MathUtil.collideAABB(b1, b2, a1, a2));
    }
}