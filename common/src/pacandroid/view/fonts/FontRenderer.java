package pacandroid.view.fonts;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontRenderer {

    private Map<String, BitmapFont> fonts;
    private BitmapFont font;

    public FontRenderer() {
        fonts = new HashMap<String, BitmapFont>();
        loadFonts();
    }

    private void loadFonts() {
        FreeTypeFontGenerator benderGenerator = new FreeTypeFontGenerator(
                Gdx.files.classpath("fonts/bendersolid.ttf"));

        BitmapFont benderSolid = benderGenerator.generateFont(50);
        benderSolid.setColor(1f, 1f, 1f, 1f);
        fonts.put("BenderSolid", benderSolid);

        font = benderSolid;
    }

    public void setFont(String font) {
        this.font = fonts.get(font);
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setColor(Color c) {
        font.setColor(c);
    }

    public void setAlpha(float alpha) {
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b,
                      alpha);
    }

    public void drawString(String string, SpriteBatch batch, int x, int y) {
        if (font == null)
            throw new IllegalStateException("Font must be set");

        font.draw(batch, string, x, y);
    }

    public void drawStringCentred(String string, SpriteBatch batch, int x, int y) {
        TextBounds bounds = font.getBounds(string);
        x -= bounds.width / 2;
        y -= bounds.height / 2;

        drawString(string, batch, x, y);
    }
}
