package org.example.dialogs;

import org.example.MagicObjectManager;
import org.example.call.Call;
import org.example.magicobject.DescriptionGenerator;
import org.example.magicobject.MagicObject;
import org.example.magicobject.PropertiesGenerator;

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
    private JButton generatePropertiesWithChatButton;

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
                if(Call.KEY.isEmpty()) {
                    SetKey dialog = new SetKey();
                    dialog.setSize(300, 200);
                    dialog.setTitle("Set the Key");
                    dialog.setVisible(true);
                }
                descriptionText.setEditable(false);
                String oldText = descriptionText.getText();
                descriptionText.setText("Generating...");
                try {
                    DescriptionGenerator generator = new DescriptionGenerator(
                            nameField.getText(),
                            (String) rarityBox.getSelectedItem(),
                            (String) typeBox.getSelectedItem(),
                            subtypeField.getText(),
                            propertiesArea.getText()
                    );
                    descriptionText.setText(generator.getResult());
                } catch (Exception exp) {
                    descriptionText.setText(oldText);
                    JOptionPane.showMessageDialog(this, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                descriptionText.setEditable(true);
            });
            descriptionGeneration.start();
        });
        generatePropertiesWithChatButton.addActionListener((e) ->  {
                Thread descriptionGeneration = new Thread(() -> {
                    if(Call.KEY.isEmpty()) {
                        SetKey dialog = new SetKey();
                        dialog.setSize(300, 200);
                        dialog.setTitle("Set the Key");
                        dialog.setVisible(true);
                    }
                    propertiesArea.setEditable(false);
                    String oldText = propertiesArea.getText();
                    propertiesArea.setText("Generating...");
                    try {
                        PropertiesGenerator generator = new PropertiesGenerator(
                                nameField.getText(),
                                (String) rarityBox.getSelectedItem(),
                                requiresAttunementCheckBox.isSelected(),
                                (String) typeBox.getSelectedItem(),
                                subtypeField.getText(),
                                descriptionText.getText()
                        );
                        propertiesArea.setText(generator.getResult());
                    } catch (Exception exp) {
                        propertiesArea.setText(oldText);
                        JOptionPane.showMessageDialog(this, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    propertiesArea.setEditable(true);
                });
                descriptionGeneration.start();});
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
