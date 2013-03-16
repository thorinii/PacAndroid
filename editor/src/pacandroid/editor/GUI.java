package pacandroid.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import pacandroid.PacAndroidGame;
import pacandroid.model.Grid;
import pacandroid.model.Level;
import pacandroid.model.loader.LevelFileReader;
import pacandroid.model.loader.LevelFileWriter;

public class GUI extends JFrame {

    private JFileChooser fc;
    private LevelView levelView;
    private GridTool gridTool;

    public GUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("PacAndroid Level Editor");

        setLayout(new BorderLayout(5, 5));

        initComponents();
        setSize(800, 600);
    }

    private void initComponents() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(buttonsPanel, BorderLayout.NORTH);

        initButtons(buttonsPanel);

        levelView = new LevelView();
        levelView.setLevel(LevelFactory.makeBlankLevel());
        add(levelView, BorderLayout.CENTER);

        gridTool = new GridTool();
        gridTool.setLevelView(levelView);
        gridTool.setDesiredCell(Grid.GRID_WALL);
    }

    private void initButtons(JPanel buttonsPanel) {
        JButton newBtn = new JButton("New");
        newBtn.addActionListener(new NewLevelListener());
        buttonsPanel.add(newBtn);

        JButton openBtn = new JButton("Open");
        openBtn.addActionListener(new OpenLevelListener());
        buttonsPanel.add(openBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(new SaveLevelListener());
        buttonsPanel.add(saveBtn);

        JButton testBtn = new JButton("Test");
        testBtn.addActionListener(new TestLevelListener());
        buttonsPanel.add(testBtn);


        buttonsPanel.add(makeSpacer(5));


        ButtonGroup toolsGroup = new ButtonGroup();

        buttonsPanel.add(makeTool("Wall", toolsGroup,
                                  Grid.GRID_WALL));
        buttonsPanel.add(makeTool("Jellybean", toolsGroup,
                                  Grid.GRID_JELLYBEAN));
        buttonsPanel.add(makeTool("Android Spawner", toolsGroup,
                                  Grid.GRID_ANDROID_SPAWN));
        buttonsPanel.add(makeTool("Enemy Spawner", toolsGroup,
                                  Grid.GRID_ENEMY_SPAWN));
        buttonsPanel.add(makeTool("Powerup", toolsGroup,
                                  Grid.GRID_POWERUP));
    }

    private JComponent makeSpacer(int width) {
        JComponent comp = new JPanel();

        comp.setPreferredSize(new Dimension(width, 1));
        comp.setMinimumSize(new Dimension(width, 1));
        comp.setMaximumSize(new Dimension(width, 1));

        return comp;
    }

    private JToggleButton makeTool(String name, ButtonGroup toolsGroup,
            int cellType) {
        JToggleButton btn = new JToggleButton(name, true);

        btn.addActionListener(new ToolOptionListener(cellType));

        toolsGroup.add(btn);

        return btn;
    }

    private class ToolOptionListener implements ActionListener {

        private final int cellType;

        public ToolOptionListener(int cellType) {
            this.cellType = cellType;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            gridTool.setDesiredCell(cellType);
        }
    }

    private class NewLevelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            levelView.setLevel(LevelFactory.makeBlankLevel());
        }
    }

    private class OpenLevelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (fc == null) {
                fc = new JFileChooser(".");
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return ".palvl PacAndroid Levels";
                    }

                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".palvl")
                                || f.isDirectory();
                    }
                });
            }

            if (fc.showOpenDialog(GUI.this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                try {
                    LevelFileReader reader = new LevelFileReader();
                    InputStream in = new BufferedInputStream(
                            new FileInputStream(file));

                    Level level = reader.readLevelFlipped(in);

                    levelView.setLevel(level);

                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(GUI.this,
                                                  "Error Saving Level: "
                            + ioe.getMessage(),
                                                  "PacAndroid Level Editor",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SaveLevelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!levelView.validateLevel()) {
                JOptionPane.showMessageDialog(GUI.this,
                                              "Cannot save level: not valid");
                return;
            }

            if (fc == null) {
                fc = new JFileChooser(".");
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return ".palvl PacAndroid Levels";
                    }

                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".palvl");
                    }
                });
            }

            if (fc.showSaveDialog(GUI.this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (!file.getName().endsWith(".palvl"))
                    file = new File(file.getAbsolutePath() + ".palvl");

                try {
                    LevelFileWriter writer = new LevelFileWriter();
                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(file));

                    writer.writeLevelFlipped(levelView.getLevel(), out);

                    out.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(GUI.this,
                                                  "Error Saving Level: "
                            + ioe.getMessage(),
                                                  "PacAndroid Level Editor",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class TestLevelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!levelView.validateLevel()) {
                JOptionPane.showMessageDialog(GUI.this,
                                              "Cannot test level: not valid");
                return;
            }

            try {
                File file = new File(File.createTempFile("test-lvl", ".palvl")
                        .getAbsolutePath());
                file.deleteOnExit();

                LevelFileWriter writer = new LevelFileWriter();
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));

                writer.writeLevelFlipped(levelView.getLevel(), out);

                out.close();

                ((JButton) e.getSource()).setEnabled(false);
                startPADesktop(file);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(GUI.this,
                                              "Cannot test level: "
                        + ioe.getMessage(),
                                              "PacAndroid Level Editor",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }

        private void startPADesktop(File level) throws IOException {
            try {
                //System.setProperty("level-file", level.toString());

                FakeLevelLoader loader = new FakeLevelLoader(levelView.
                        getLevel());
                final LwjglFrame frame = new LwjglFrame(new PacAndroidGame(
                        loader),
                                                        "PacAndroid", 1280, 800,
                                                        true);
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.getLwjglCanvas().exit();
                                System.out.println("Closing");
                            }
                        });
                    }
                });
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }
}
