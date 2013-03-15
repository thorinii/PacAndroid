package pacandroid.view.fonts;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontRenderer {

    private static final String FONT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";
    private Map<String, BitmapFont> fonts;
    private BitmapFont font;

    public FontRenderer() {
        fonts = new HashMap<String, BitmapFont>();
        loadFonts();
    }

    private void loadFonts() {
        FreeTypeFontGenerator benderGenerator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/bendersolid.ttf"));

        BitmapFont benderSolid = benderGenerator.generateFont(50);
        benderSolid.setColor(1f, 0f, 0f, 1f);
        fonts.put("BenderSolid", benderSolid);

        font = benderSolid;
    }

    public void setFont(String font) {
        this.font = fonts.get(font);
    }

    public BitmapFont getFont() {
        return font;
    }

    public void drawString(String string, SpriteBatch batch, int x, int y) {
        if (font == null)
            throw new IllegalStateException("Font must be set");

        font.draw(batch, string, x, y);
    }

    public void drawStringCentred(String string, SpriteBatch batch, int x, int y) {
    }
}
