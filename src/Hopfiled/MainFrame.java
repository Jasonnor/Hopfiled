package Hopfiled;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MainFrame {
    private static JMenuItem loadMenuItem;
    private static JFrame frame;
    private JPanel layoutPanel;
    private JButton loadButton;
    private JLabel loadTrainValue;
    private JLabel loadTestValue;
    private JLabel timesValue;
    private JLabel correctValue;
    private JTextArea trainText;
    private JTextArea testText;
    private JTextArea resultText;
    private JRadioButton threshold0Button;
    private JRadioButton thresholdWeightsButton;
    private JButton generateNoiseButton;
    private JTextField noiseText;
    private ArrayList<ArrayList<double[]>> trainDataList = new ArrayList<>();
    private ArrayList<ArrayList<double[]>> testDataList = new ArrayList<>();
    private boolean thresholdZero = true;

    private MainFrame() {
        loadButton.addActionListener(e -> {
            resetFrame();
            resetData();
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser, trainDataList, loadTrainValue);
            }
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser, testDataList, loadTestValue);
            }
            generateNoiseButton.setEnabled(true);
            runNetwork();
        });
        loadMenuItem.addActionListener(e -> {
            resetFrame();
            resetData();
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser, trainDataList, loadTrainValue);
            }
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser, testDataList, loadTestValue);
            }
            generateNoiseButton.setEnabled(true);
            runNetwork();
        });
        generateNoiseButton.addActionListener(e -> {
            for (int n = 0; n < trainDataList.size(); n++)
                for (int i = 0; i < trainDataList.get(n).size(); i++)
                    for (int j = 0; j < trainDataList.get(n).get(i).length; j++)
                        if (Math.random() < Double.parseDouble(noiseText.getText()))
                            testDataList.get(n).get(i)[j] = trainDataList.get(n).get(i)[j] * -1;
                        else
                            testDataList.get(n).get(i)[j] = trainDataList.get(n).get(i)[j];
            runNetwork();
        });
        threshold0Button.addActionListener(e -> {
            thresholdZero = true;
            runNetwork();
        });
        thresholdWeightsButton.addActionListener(e -> {
            thresholdZero = false;
            runNetwork();
        });
    }

    private void loadFile(JFileChooser fileChooser, ArrayList<ArrayList<double[]>> dataList, JLabel loadValue) {
        File loadedFile = fileChooser.getSelectedFile();
        loadValue.setText(loadedFile.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(loadedFile))) {
            String line = br.readLine();
            ArrayList<double[]> data = new ArrayList<>();
            while (line != null) {
                if (line.equals("")) {
                    if (data.size() > 0) {
                        dataList.add(new ArrayList<>(data));
                        data.clear();
                    }
                    line = br.readLine();
                    continue;
                }
                char[] charArray = line.toCharArray();
                double[] numbers = new double[charArray.length];
                for (int i = 0; i < charArray.length; i++)
                    numbers[i] = (charArray[i] == '1') ? 1.0 : -1.0;
                data.add(numbers);
                line = br.readLine();
            }
            if (data.size() > 0)
                dataList.add(new ArrayList<>(data));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void runNetwork() {
        trainText.setText(null);
        testText.setText(null);
        resultText.setText(null);
        for (int i = 0; i < trainDataList.size(); i++) {
            ArrayList<double[]> trainData = trainDataList.get(i);
            for (double[] data : trainData) {
                for (double d : data)
                    trainText.append((d > 0.0) ? "1" : " ");
                trainText.append("\n");
            }
            ArrayList<double[]> testData = testDataList.get(i);
            for (double[] data : testData) {
                for (double d : data)
                    testText.append((d > 0.0) ? "1" : " ");
                testText.append("\n");
            }
            Network network = new Network(trainData, thresholdZero);
            network.train();
            ArrayList<double[]> result = network.recall(testData);
            for (double[] data : result) {
                for (double d : data)
                    resultText.append((d > 0.0) ? "1" : " ");
                resultText.append("\n");
            }
            trainText.append("\n");
            testText.append("\n");
            resultText.append("\n");
        }
        trainText.setCaretPosition(0);
        testText.setCaretPosition(0);
        resultText.setCaretPosition(0);
    }

    private void resetData() {
        trainDataList.clear();
        testDataList.clear();
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

    public static void main(String[] args) {
        JMenuBar menuBar = new JMenuBar();
        JMenu filesMenu = new JMenu("Files");
        JMenu skinsMenu = new JMenu("Skins");
        // Files menu
        loadMenuItem = new JMenuItem("Load", KeyEvent.VK_L);
        filesMenu.setMnemonic(KeyEvent.VK_F);
        filesMenu.add(loadMenuItem);
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
}
