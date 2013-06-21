package pacandroid.model;

public class Score {

    private int score;
    private int jellybeans;
    private int powerups;
    private int apples;

    public Score() {
        score = 0;
        jellybeans = 0;
        powerups = 0;
        apples = 0;
    }

    public void eatJellyBean() {
        jellybeans++;
        score++;
    }

    public void eatPowerup() {
        powerups++;
        score += 8;
    }

    public void eatApple() {
        apples++;
        score += 5;
    }

    public void doubleScore() {
        score *= 2;
    }

    public void resetScore() {
        score = 0;
    }

    public int getScore() {
        return score;
    }

    public int getJellybeans() {
        return jellybeans;
    }

    public int getPowerups() {
        return powerups;
    }

    public int getApples() {
        return apples;
    }

    @Override
    public String toString() {
        return String.valueOf(score);
    }
}
