package org.example;

import com.theokanning.openai.OpenAiHttpException;
import org.example.call.Call;

import javax.swing.*;

public class DnDToolController extends JFrame {
    private JPanel panel;
    private JTextField nameText;
    private JComboBox<String> rarityBox;
    private JComboBox<String> typeBox;
    private JCheckBox attunementCheckBox;
    private JButton generateButton;
    private JTextArea resultArea;

    public DnDToolController() {
        setContentPane(panel);
        setTitle("Dungeons and Dragons Tools");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        generateButton.addActionListener(e -> {
            resultArea.setText("Connecting to ChatGPT...");
            Thread call = new Thread(() -> {
                String prompt = "Generate a D&D 5.0 edition magic object. ";
                if(!nameText.getText().isEmpty()) {
                    prompt += "It is very important that the magic object has name: \"" + nameText.getText() + "\". ";
                }
                if(rarityBox.getSelectedItem() instanceof String rarity && !rarity.isEmpty()) {
                    prompt += "It is very important that the magic object has " + rarity + "rarity. ";
                }
                if(typeBox.getSelectedItem() instanceof String type && !type.isEmpty()) {
                    prompt += "It is very important that the magic object is a " + type + ". ";
                }
                if(attunementCheckBox.isSelected()) {
                    prompt += "It is very important that the magic object requires attunement.";
                }
                try {
                    Call generatingMagicItem = new Call(prompt);
                    resultArea.setText(generatingMagicItem.getResult());
                } catch (OpenAiHttpException exception) {
                    resultArea.setText("There has been a network error: " + exception.getMessage() + "\nPlease retry");
                }
            });
            call.start();
        });
    }

    public static void main(String[] args) {
        new DnDToolController();
    }
}
