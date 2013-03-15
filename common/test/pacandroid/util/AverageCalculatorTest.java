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
public class AverageCalculatorTest {

    @Test(expected = IllegalStateException.class)
    public void testZeroSamples() {
        SampleCollector calc = new SampleCollector(3);
        calc.getAverage();
    }

    @Test
    public void testOneSample() {
        SampleCollector calc = new SampleCollector(3);
        calc.addSample(5);
        assertEquals(5, calc.getAverage(), 0);
    }

    @Test
    public void testTwoSamples() {
        SampleCollector calc = new SampleCollector(3);
        calc.addSample(5);
        calc.addSample(7);

        assertEquals(6, calc.getAverage(), 0);
    }

    @Test
    public void testThreeSamples() {
        SampleCollector calc = new SampleCollector(3);
        calc.addSample(5);
        calc.addSample(7);
        calc.addSample(9);

        assertEquals(7, calc.getAverage(), 0);
    }

    @Test
    public void testWrappedSamples() {
        SampleCollector calc = new SampleCollector(3);
        calc.addSample(5);
        calc.addSample(7);
        calc.addSample(9);
        calc.addSample(11); // This one should wrap around

        assertEquals(9, calc.getAverage(), 0);
    }

    @Test
    public void testWrappedTwice() {
        SampleCollector calc = new SampleCollector(3);
        calc.addSample(5);
        calc.addSample(7);
        calc.addSample(9);
        
        calc.addSample(11);
        calc.addSample(13);
        calc.addSample(15);
        
        calc.addSample(17);

        assertEquals(15, calc.getAverage(), 0);
    }
}