package pacandroid.view.fonts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class DroidSansMonoFont extends Font {

    private static final char[][] MAP = {
        {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z'},
        {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'},
        {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'},
        {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-',
            '=', '\\', '|', '~', '`', ',', '.', '/', '<', '>', '?',
            ';', '\'', ':', '"'}
    };

    public DroidSansMonoFont() {
        super(new Texture(Gdx.files
                .internal("fonts/droid-sans-mono-18-white.png")), MAP);
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 11;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public int getHMapSpacing() {
        return 11;
    }

    @Override
    public int getVMapSpacing() {
        // TODO Auto-generated method stub
        return 22;
    }
}