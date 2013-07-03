/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author lachlan
 */
public enum Powerup {

    Null(-1, null), KillAll(2000, "Kill All"), Edible(7000, "Edible"), DoubleScore(0, "Double Score"), NewLife(0,
                                                                                                               "New Life"),
    LevelStartFreeze(3000, null, true);

    Powerup(int buffMillis, String name) {
        this.buffMillis = buffMillis;
        this.freeze = false;
        this.name = name;
    }

    Powerup(int buffMillis, String name, boolean freeze) {
        this.buffMillis = buffMillis;
        this.freeze = freeze;
        this.name = name;
    }

    public boolean isHuman() {
        return name != null;
    }
    public final int buffMillis;
    public final String name;
    public final boolean freeze;
    private static final List<Powerup> VALUES = Collections.unmodifiableList(Arrays.asList(Powerup.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Powerup randomChoice() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
