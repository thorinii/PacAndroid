package pacandroid.model;

public class Score {

    private int score;

    public Score() {
        score = 0;
    }

    public Score(int score) {
        this.score = score;
    }

    public void eatJellyBean() {
        score++;
    }

    public void eatPowerup() {
        score += 8;
    }

    public void eatApple() {
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

    @Override
    public String toString() {
        return String.valueOf(score);
    }
}
