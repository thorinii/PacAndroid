package pacandroid.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class LevelState {

    public enum Powerup {

        Null(-1, null),
        KillAll(2000, "Kill All"),
        Edible(7000, "Edible"),
        DoubleScore(0, "Double Score"),
        NewLife(0, "New Life"),
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
        private static final List<Powerup> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Powerup randomChoice() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }
    private Powerup currentPowerup;
    private int powerupTimeLeft;
    private Score score;
    private int lives;
    private boolean gameOver;
    private boolean playerWon;

    public LevelState() {
        setDefaults();
    }

    public void setDefaults() {
        setCurrentPowerup(Powerup.LevelStartFreeze);

        score = new Score();
        lives = 3;
    }

    public Score getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean didPlayerWin() {
        return playerWon;
    }

    public void takeLife() {
        lives--;

        if (lives <= 0)
            setGameOver(false);
    }

    public void addLife() {
        lives++;
    }

    public void setGameOver(boolean playerWon) {
        this.gameOver = true;
        this.playerWon = playerWon;
    }

    public Powerup getCurrentPowerup() {
        return currentPowerup;
    }

    public void setCurrentPowerup(Powerup currentPowerup) {
        this.currentPowerup = currentPowerup;

        powerupTimeLeft = currentPowerup.buffMillis;
        if (powerupTimeLeft < 0) {
            powerupTimeLeft = Integer.MIN_VALUE;
        }
    }

    public void choosePowerup(int x, int y) {
        Powerup[] valid = new Powerup[]{
            Powerup.KillAll,
            Powerup.Edible,
            Powerup.DoubleScore,
            Powerup.NewLife};

        int hash = 23;
        hash = hash * 53 + x;
        hash = hash * 53 + y;
        hash %= valid.length;

        setCurrentPowerup(valid[hash]);
    }

    public void updatePowerups(float tpf) {
        if (powerupTimeLeft != Integer.MIN_VALUE) {
            if (powerupTimeLeft == 0) {
                powerupTimeLeft = -1;
            } else {
                powerupTimeLeft -= (int) (tpf * 1000);
                if (powerupTimeLeft < 0)
                    setCurrentPowerup(Powerup.Null);
            }
        }
    }
}
