/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pacandroid.view.fonts.FontRenderer;

/**
 *
 * @author lachlan
 */
public class Text extends GUIComponent implements UsesFonts {

    private FontRenderer renderer;
    private String text;

    public Text() {
        text = "";
    }

    public Text(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void setFont(FontRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if (!text.isEmpty())
            renderer.drawStringCentred(text, batch, (int) getX(), (int) getY());
    }
}
