package me.superblaubeere27.jobf.ui;

import com.google.gson.JsonParser;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.superblaubeere27.jobf.Configuration;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.JObfFileFilter;
import me.superblaubeere27.jobf.util.JarFileFilter;
import me.superblaubeere27.jobf.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class GUI extends JFrame {
    public JTextArea logArea;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField inputTextField;
    private JButton inputBrowseButton;
    private JTextField outputTextField;
    private JButton outputBrowseButton;
    private JButton obfuscateButton;
    private JTabbedPane tabbedPane2;
    private JCheckBox flowObfuscatorEnabled;
    private JCheckBox informationRemoverEnabled;
    private JCheckBox numberObfuscatorEnabled;
    private JCheckBox hiderEnabled;
    private JCheckBox shuffleMembersEnabled;
    private JCheckBox staticinitializionProtectorEnabled;
    private JCheckBox stringEncryptionEnabled;
    private JButton lightButton;
    private JButton mediumButton;
    private JButton heavyButton;
    private JButton aggressiveButton;
    private JButton buildButton;
    private JButton loadButton;
    private JButton saveButton;
    private JCheckBox prettyPrintCheckBox;
    private JTextArea configPanel;
    private JCheckBox referenceProxyEnabled;

    public GUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel1);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.getWidth() / 2), (int) (screenSize.getHeight() / 2));
        setLocationRelativeTo(null);
        setVisible(true);

        inputBrowseButton.addActionListener(e -> {
            String file = Util.chooseFile(null, GUI.this, new JarFileFilter());
            if (file != null) {
                inputTextField.setText(file);
            }
        });
        outputBrowseButton.addActionListener(e -> {
            String file = Util.chooseFileToSave(null, GUI.this, new JarFileFilter());
            if (file != null) {
                outputTextField.setText(file);
            }
        });
        obfuscateButton.addActionListener(e -> startObfuscator());
        buildButton.addActionListener(e -> buildConfig());
        saveButton.addActionListener(e -> {
            String name = Util.chooseFileToSave(null, GUI.this, new JObfFileFilter());
            if (name != null) {
                buildConfig();
                try {
                    Files.write(new File(name).toPath(), configPanel.getText().getBytes(Charset.forName("UTF-8")));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(GUI.this, e1.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        loadButton.addActionListener(e -> {
            String name = Util.chooseFile(null, GUI.this, new JObfFileFilter());
            if (name != null) {
                buildConfig();
                try {
                    Configuration configuration = new Configuration().fromJson(new JsonParser().parse(new String(Files.readAllBytes(new File(name).toPath()))).getAsJsonObject());
                    applyConfig(configuration);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(GUI.this, e1.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void applyConfig(Configuration configuration) {
        Configuration config = new Configuration();

        flowObfuscatorEnabled.setSelected(configuration.isFlowObfuscatorEnabled);
        referenceProxyEnabled.setSelected(configuration.isReferenceProxyEnabled);
        hiderEnabled.setSelected(configuration.isHiderEnabled);
        informationRemoverEnabled.setSelected(config.isInformationRemoverEnabled);
        numberObfuscatorEnabled.setSelected(configuration.isNumberObfuscatorEnabled);
        shuffleMembersEnabled.setSelected(configuration.isShuffleMembersEnabled);
        staticinitializionProtectorEnabled.setSelected(configuration.isStaticInitializionProtectorEnabled);
        stringEncryptionEnabled.setSelected(configuration.isStringEncryptionEnabled);

        // TODO ADD SETTINGS
    }

    private void buildConfig() {
        configPanel.setText(prettyPrintCheckBox.isSelected() ? Util.prettyGson(createConfig().toJson()) : createConfig().toJson().toString());
    }

    private Configuration createConfig() {
        Configuration config = new Configuration();

        config.isFlowObfuscatorEnabled = flowObfuscatorEnabled.isSelected();
        config.isHiderEnabled = hiderEnabled.isSelected();
        config.isInformationRemoverEnabled = informationRemoverEnabled.isSelected();
        config.isNumberObfuscatorEnabled = numberObfuscatorEnabled.isSelected();
        config.isShuffleMembersEnabled = shuffleMembersEnabled.isSelected();
        config.isStaticInitializionProtectorEnabled = staticinitializionProtectorEnabled.isSelected();
        config.isStringEncryptionEnabled = stringEncryptionEnabled.isSelected();

        // TODO ADD SETTINGS

        return config;
    }

    private void startObfuscator() {
        Configuration config = createConfig();
        JObfImpl impl = new JObfImpl();
        impl.loadConfig(config);

        File in = new File(inputTextField.getText());

        if (!in.exists()) {
            JOptionPane.showMessageDialog(this, "Input file doesn't exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            impl.processJar(inputTextField.getText(), outputTextField.getText(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab(ResourceBundle.getBundle("strings").getString("input.output"), panel2);
        inputTextField = new JTextField();
        panel2.add(inputTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputBrowseButton = new JButton();
        this.$$$loadButtonText$$$(inputBrowseButton, ResourceBundle.getBundle("strings").getString("browse"));
        panel2.add(inputBrowseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputTextField = new JTextField();
        panel2.add(outputTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        outputBrowseButton = new JButton();
        this.$$$loadButtonText$$$(outputBrowseButton, ResourceBundle.getBundle("strings").getString("browse"));
        panel2.add(outputBrowseButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("strings").getString("input"));
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("strings").getString("output"));
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Processor", panel3);
        tabbedPane2 = new JTabbedPane();
        panel3.add(tabbedPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Preset", panel4);
        lightButton = new JButton();
        lightButton.setText("Light");
        panel4.add(lightButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mediumButton = new JButton();
        mediumButton.setText("Medium");
        panel4.add(mediumButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        heavyButton = new JButton();
        heavyButton.setText("Heavy");
        panel4.add(heavyButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aggressiveButton = new JButton();
        aggressiveButton.setText("Aggressive");
        panel4.add(aggressiveButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("FlowObfuscator", panel5);
        flowObfuscatorEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(flowObfuscatorEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel5.add(flowObfuscatorEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("InformationRemover", panel6);
        informationRemoverEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(informationRemoverEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel6.add(informationRemoverEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("NumberObfuscator", panel7);
        numberObfuscatorEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(numberObfuscatorEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel7.add(numberObfuscatorEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel7.add(spacer5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Hider", panel8);
        hiderEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(hiderEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel8.add(hiderEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel8.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("ShuffleMembers", panel9);
        shuffleMembersEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(shuffleMembersEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel9.add(shuffleMembersEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel9.add(spacer7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("StaticInitalitionProtector", panel10);
        staticinitializionProtectorEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(staticinitializionProtectorEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel10.add(staticinitializionProtectorEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel10.add(spacer8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("StringEncryption", panel11);
        stringEncryptionEnabled = new JCheckBox();
        this.$$$loadButtonText$$$(stringEncryptionEnabled, ResourceBundle.getBundle("strings").getString("enabled"));
        panel11.add(stringEncryptionEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel11.add(spacer9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("ReferenceProxy", panel12);
        referenceProxyEnabled = new JCheckBox();
        referenceProxyEnabled.setText("Enabled");
        panel12.add(referenceProxyEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel12.add(spacer10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Config", panel13);
        buildButton = new JButton();
        buildButton.setText("Build");
        panel13.add(buildButton, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadButton = new JButton();
        loadButton.setText("Load");
        panel13.add(loadButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel13.add(saveButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        prettyPrintCheckBox = new JCheckBox();
        prettyPrintCheckBox.setText("PrettyPrint");
        panel13.add(prettyPrintCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel13.add(scrollPane1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        configPanel = new JTextArea();
        scrollPane1.setViewportView(configPanel);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab(ResourceBundle.getBundle("strings").getString("class.options"), panel14);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab(ResourceBundle.getBundle("strings").getString("log"), panel15);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel15.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logArea = new JTextArea();
        logArea.setEditable(false);
        scrollPane2.setViewportView(logArea);
        obfuscateButton = new JButton();
        this.$$$loadButtonText$$$(obfuscateButton, ResourceBundle.getBundle("strings").getString("obfuscate"));
        panel1.add(obfuscateButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(inputTextField);
        label2.setLabelFor(outputTextField);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
