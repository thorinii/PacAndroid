/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.util;

/**
 * Basic average sample calculator. Collects samples until a limit is reached,
 * then replaces from the beginning.
 * <p/>
 * @author lachlan
 */
public class SampleCollector {

    private final int total;
    private final int[] samples;
    private int head;
    private int count;

    public SampleCollector(int total) {
        this.total = total;

        samples = new int[total];
        head = 0;
        count = 0;
    }

    public void addSample(int sample) {
        if (count < total) {
            samples[head] = sample;
            head++;
            count++;
        } else {
            samples[head] = sample;
            head++;
        }

        if (head >= total)
            head = 0;
    }

    public float getAverage() {
        if (count == 0)
            throw new IllegalStateException("Must have at least one sample");

        float sum = 0;
        for (int i = 0; i < count; i++)
            sum += samples[i];
        return sum / count;
    }

    public int getMax() {
        if (count == 0)
            throw new IllegalStateException("Must have at least one sample");

        int max = samples[0];
        for (int i = 1; i < count; i++)
            if (samples[i] > max)
                max = samples[i];
        return max;
    }

    public int getMin() {
        if (count == 0)
            throw new IllegalStateException("Must have at least one sample");

        int min = samples[0];
        for (int i = 1; i < count; i++)
            if (samples[i] < min)
                min = samples[i];
        return min;
    }

    public int getHead() {
        return head;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return getMin() + " < " + getAverage() + " < " + getMax();
    }
}
