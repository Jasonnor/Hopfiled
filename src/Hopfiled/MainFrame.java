package Hopfiled;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MainFrame {
    private static JMenuItem loadMenuItem;
    private static JMenuItem generateMenuItem;
    private static JFrame frame;
    private JPanel layoutPanel;
    private JPanel coordinatePanel;
    private JButton loadButton;
    private JLabel loadValue;
    private JButton generateButton;
    private JLabel timesValue;
    private JLabel correctValue;
    private ArrayList<ArrayList<double[]>> trainDataList = new ArrayList<>();

    private MainFrame() {
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
        loadMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
        generateButton.addActionListener(e -> runNetwork());
        generateMenuItem.addActionListener(e -> runNetwork());
    }

    private void loadFile(JFileChooser fileChooser) {
        File loadedFile = fileChooser.getSelectedFile();
        loadValue.setText(loadedFile.getPath());
        resetFrame();
        resetData();
        try (BufferedReader br = new BufferedReader(new FileReader(loadedFile))) {
            String line = br.readLine();
            ArrayList<double[]> trainData = new ArrayList<>();
            while (line != null) {
                if (line.equals("")) {
                    if (trainData.size() > 0) {
                        trainDataList.add(new ArrayList<>(trainData));
                        trainData.clear();
                    }
                    line = br.readLine();
                    continue;
                }
                char[] charArray = line.toCharArray();
                double[] numbers = new double[charArray.length];
                for (int i = 0; i < charArray.length; i++)
                    numbers[i] = (charArray[i] == '1') ? 1.0 : -1.0;
                trainData.add(numbers);
                line = br.readLine();
            }
            if (trainData.size() > 0)
                trainDataList.add(new ArrayList<>(trainData));
            generateButton.setEnabled(true);
            runNetwork();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void runNetwork() {
        for (ArrayList<double[]> trainData : trainDataList) {
            Network network = new Network(trainData);
            network.train();
            ArrayList<double[]> result = network.recall(trainData);
            for (double[] data : result) {
                for (double s : data) {
                    if (s > 0.0)
                        System.out.print("1");
                    else
                        System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private void resetData() {
        trainDataList.clear();
    }

    private static void resetFrame() {
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private static void changeLAF(String name) {
        try {
            if (name.equals("Nimbus")) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
            } else {
                UIManager.setLookAndFeel(name);
            }
            resetFrame();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            System.out.println("Failed to load the skin!");
        }
    }

    private void createUIComponents() {
        coordinatePanel = new GPanel();
    }

    public static void main(String[] args) {
        JMenuBar menuBar = new JMenuBar();
        JMenu filesMenu = new JMenu("Files");
        JMenu skinsMenu = new JMenu("Skins");
        // Files menu
        loadMenuItem = new JMenuItem("Load", KeyEvent.VK_L);
        generateMenuItem = new JMenuItem("Generate", KeyEvent.VK_G);
        filesMenu.setMnemonic(KeyEvent.VK_F);
        filesMenu.add(loadMenuItem);
        filesMenu.add(generateMenuItem);
        menuBar.add(filesMenu);
        // Skins menu
        skinsMenu.setMnemonic(KeyEvent.VK_S);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem skinsMetalMenuItem = new JRadioButtonMenuItem("Metal");
        skinsMetalMenuItem.setMnemonic(KeyEvent.VK_M);
        skinsMenu.add(skinsMetalMenuItem);
        group.add(skinsMetalMenuItem);
        skinsMetalMenuItem.addActionListener(e -> changeLAF(UIManager.getCrossPlatformLookAndFeelClassName()));
        JRadioButtonMenuItem skinsDefaultMenuItem = new JRadioButtonMenuItem("Default");
        skinsDefaultMenuItem.setMnemonic(KeyEvent.VK_D);
        skinsMenu.add(skinsDefaultMenuItem);
        group.add(skinsDefaultMenuItem);
        skinsDefaultMenuItem.addActionListener(e -> changeLAF(UIManager.getSystemLookAndFeelClassName()));
        JRadioButtonMenuItem skinsMotifMenuItem = new JRadioButtonMenuItem("Motif");
        skinsMotifMenuItem.setMnemonic(KeyEvent.VK_M);
        skinsMenu.add(skinsMotifMenuItem);
        group.add(skinsMotifMenuItem);
        skinsMotifMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
        JRadioButtonMenuItem skinsGTKMenuItem = new JRadioButtonMenuItem("GTK");
        skinsGTKMenuItem.setMnemonic(KeyEvent.VK_G);
        skinsMenu.add(skinsGTKMenuItem);
        group.add(skinsGTKMenuItem);
        skinsGTKMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
        JRadioButtonMenuItem skinsWindowsMenuItem = new JRadioButtonMenuItem("Windows");
        skinsWindowsMenuItem.setMnemonic(KeyEvent.VK_G);
        skinsMenu.add(skinsWindowsMenuItem);
        group.add(skinsWindowsMenuItem);
        skinsWindowsMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
        JRadioButtonMenuItem skinsNimbusMenuItem = new JRadioButtonMenuItem("Nimbus");
        skinsNimbusMenuItem.setMnemonic(KeyEvent.VK_N);
        skinsNimbusMenuItem.setSelected(true);
        skinsMenu.add(skinsNimbusMenuItem);
        group.add(skinsNimbusMenuItem);
        skinsNimbusMenuItem.addActionListener(e -> changeLAF("Nimbus"));
        menuBar.add(skinsMenu);
        // Main frame
        frame = new JFrame("Hopfiled");
        frame.setContentPane(new MainFrame().layoutPanel);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        changeLAF("Nimbus");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private class GPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(Color.black);
            g.drawLine(250, 0, 250, 500);
            g.drawLine(0, 250, 500, 250);
            g2.setColor(Color.black);
            //g2.draw(new Line2D.double(point[0], point[1], point[0], point[1]));
        }
    }
}
