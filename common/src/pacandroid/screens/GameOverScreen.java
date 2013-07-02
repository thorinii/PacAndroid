/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pacandroid.PacAndroidGame;
import pacandroid.gui.Button;
import pacandroid.gui.GUI;
import pacandroid.gui.Text;
import pacandroid.model.Score;
import pacandroid.view.fonts.FontRenderer;

/**
 *
 * @author lachlan
 */
public class GameOverScreen extends AbstractScreen {

    private final Score score;
    private final float time;
    private final GUI gui;

    public GameOverScreen(PacAndroidGame game, FontRenderer renderer, float time, Score score) {
        super(game);

        this.score = score;
        this.time = time;

        gui = new GUI(800, 600);
        gui.setBackground(new Texture(Gdx.files.internal(
                "gui/gameoverbackground.png")));

        Texture playUp, playDown;
        playUp = new Texture(Gdx.files.internal("gui/play-up.png"));
        playDown = new Texture(Gdx.files.internal("gui/play-down.png"));

//        Button playButton = new Button(playUp, playDown);
//        playButton.setCentre(400, 150);
//        playButton.setActionListener(new ActionListener() {
//            @Override
//           public void actionPerformed(ActionEvent e) {
//                getGame().play();
//            }
//        });
//        gui.add(playButton);

        Text text = new Text("Time: " + time + " seconds");
        text.setCentre(400, 200);
        gui.add(text);

        text = new Text("Apples: " + score.getApples());
        text.setCentre(400, 250);
        gui.add(text);

        text = new Text("Powerups: " + score.getPowerups());
        text.setCentre(400, 300);
        gui.add(text);

        text = new Text("Jellybeans: " + score.getJellybeans());
        text.setCentre(400, 350);
        gui.add(text);

        text = new Text("Score: " + score.toString());
        text.setCentre(400, 400);
        gui.add(text);

        gui.setFontRenderer(renderer);
        gui.enable();
    }

    @Override
    public void resize(int width, int height) {
        gui.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        gui.draw();
    }

    @Override
    public void dispose() {
        gui.disable();
        gui.dispose();
    }
}
