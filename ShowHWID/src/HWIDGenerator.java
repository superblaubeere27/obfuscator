import me.superblaubeere27.hwid.HWID;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class HWIDGenerator {

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(1, 2));

        JTextField hwidfield;

        jp.add(hwidfield = new JTextField(HWID.bytesToHex(HWID.generateHWID())));
        hwidfield.setEditable(false);
        JButton button;
        jp.add(button = new JButton("Copy"));
        button.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwidfield.getText()), null));

        JOptionPane.showMessageDialog(null, jp);
    }

}
