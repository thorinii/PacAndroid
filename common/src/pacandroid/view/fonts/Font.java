package pacandroid.view.fonts;

import com.badlogic.gdx.graphics.Texture;

public abstract class Font {

    private final char[][] map;
    private final Texture texture;

    public Font(Texture texture) {
        this.texture = texture;
        this.map = null;
    }

    public Font(Texture texture, char[][] map) {
        this.texture = texture;
        this.map = map;
    }

    public Texture getTexture() {
        return texture;
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract int getHMapSpacing();

    public abstract int getVMapSpacing();

    public int[] getCharacterPosition(char c) {
        if (map == null)
            throw new IllegalStateException(
                    "No character map specified, and not overriden.");

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == c)
                    return new int[]{x, y};
            }
        }

        return new int[]{-1, -1};
    }
}
