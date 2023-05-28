package org.example.dialogs;

import org.example.MagicObjectManager;
import org.example.magicobject.DescriptionGenerator;
import org.example.magicobject.MagicObject;

import javax.swing.*;
import java.awt.event.*;

public class AddMagicObject extends JDialog {
    private final MagicObjectManager PARENT_FRAME;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox requiresAttunementCheckBox;
    private JTextField nameField;
    private JTextField subtypeField;
    private JTextArea descriptionText;
    private JTextArea propertiesArea;
    private JComboBox<String> rarityBox;
    private JComboBox<String> typeBox;
    private JButton generateDescriptionWithChatButton;

    public AddMagicObject(MagicObjectManager parentFrame) {
        this.PARENT_FRAME = parentFrame;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        generateDescriptionWithChatButton.addActionListener(e -> {
            Thread descriptionGeneration = new Thread(() -> {
                descriptionText.setEditable(false);
                descriptionText.setText("Generating...");
                DescriptionGenerator generator = new DescriptionGenerator(
                        nameField.getText(),
                        (String) rarityBox.getSelectedItem(),
                        (String) typeBox.getSelectedItem(),
                        subtypeField.getText(),
                        propertiesArea.getText()
                );
                descriptionText.setText(generator.getResult());
                descriptionText.setEditable(true);
            });
            descriptionGeneration.start();
        });
    }

    private void onOK() {
        if (!nameField.getText().isEmpty()) {
            MagicObject magicObject = new MagicObject(
                    nameField.getText(),
                    (String) rarityBox.getSelectedItem(),
                    requiresAttunementCheckBox.isSelected(),
                    (String) typeBox.getSelectedItem());
            magicObject.setSubtype(subtypeField.getText());
            magicObject.setDescription(descriptionText.getText());
            magicObject.setProperties(propertiesArea.getText());
            if(PARENT_FRAME.addItem(magicObject)) {
                dispose();
            }else {
                JOptionPane.showMessageDialog(this, "An error occurred while adding the object. " +
                "Please retry (note that can't exist two Objects with the same name).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onCancel() {
        dispose();
    }
}
