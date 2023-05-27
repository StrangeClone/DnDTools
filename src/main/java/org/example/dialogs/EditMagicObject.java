package org.example.dialogs;

import org.example.MagicObjectManager;
import org.example.magicobject.MagicObject;

import javax.swing.*;
import java.awt.event.*;

public class EditMagicObject extends JDialog {
    private final MagicObjectManager PARENT;
    private final String ORIGINAL_NAME;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JComboBox<String> rarityBox;
    private JCheckBox requiresAttunementCheckBox;
    private JComboBox<String> typeBox;
    private JTextField subtypeField;
    private JTextArea descrpitionArea;
    private JTextArea propertiesArea;

    public EditMagicObject(MagicObjectManager parent, MagicObject magicObject) {
        this.PARENT = parent;

        ORIGINAL_NAME = magicObject.getName();
        nameField.setText(magicObject.getName());
        rarityBox.setSelectedItem(magicObject.getRarity());
        requiresAttunementCheckBox.setSelected(magicObject.requiresAttunement());
        typeBox.setSelectedItem(magicObject.getType());
        subtypeField.setText(magicObject.getSubtype());
        descrpitionArea.setText(magicObject.getDescription());
        propertiesArea.setText(magicObject.getProperties());

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if(!nameField.getText().isEmpty()) {
            MagicObject item = new MagicObject(
                    nameField.getText(),
                    (String) rarityBox.getSelectedItem(),
                    requiresAttunementCheckBox.isSelected(),
                    (String) typeBox.getSelectedItem()
            );
            item.setSubtype(subtypeField.getText());
            item.setDescription(descrpitionArea.getText());
            item.setProperties(propertiesArea.getText());
            if(PARENT.modifyItem(ORIGINAL_NAME, item)) {
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
