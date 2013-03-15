/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.util;

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
    public void testInRange() {
        assertEquals(4, MathUtil.clamp(4, 2, 6));
    }

    @Test
    public void testAboveRange() {
        assertEquals(6, MathUtil.clamp(8, 2, 6));
    }

    @Test
    public void testBelowRange() {
        assertEquals(2, MathUtil.clamp(0, 2, 6));
    }
}