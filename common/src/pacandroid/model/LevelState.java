package pacandroid.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class LevelState {

    public enum Powerup {

        Null(-1),
        KillAll(2000),
        Edible(4000),
        HighScore(0),
        Jellybean2x(0),
        NewLife(0),
        LevelStartFreeze(3000, true);

        Powerup(int buffMillis) {
            this.buffSeconds = buffMillis;
            this.freeze = false;
        }

        Powerup(int buffMillis, boolean freeze) {
            this.buffSeconds = buffMillis;
            this.freeze = freeze;
        }
        public final int buffSeconds;
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
        System.out.println("Setting Powerup: " + currentPowerup.name());

        powerupTimeLeft = currentPowerup.buffSeconds;

        if (powerupTimeLeft < 0) {
            powerupTimeLeft = Integer.MIN_VALUE;
        }
    }

    public void choosePowerup(int x, int y) {
        Powerup[] valid = new Powerup[]{
            Powerup.KillAll,
            Powerup.Edible,
            Powerup.HighScore,
            Powerup.Jellybean2x,
            Powerup.NewLife};

        int hash = 41;
        hash = hash * 53 + x;
        hash = hash * 53 + y;
        hash %= valid.length;

        setCurrentPowerup(valid[hash]);
    }

    public void updatePowerups(float tpf) {
        if (powerupTimeLeft != Integer.MIN_VALUE) {
            powerupTimeLeft -= (int) (tpf * 1000);

            if (powerupTimeLeft < 0) {
                setCurrentPowerup(Powerup.Null);
            }
        }
    }
}
