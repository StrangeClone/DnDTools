package org.example;

import org.example.dialogs.AddMagicObject;
import org.example.dialogs.EditMagicObject;
import org.example.magicobject.MagicObject;

import javax.swing.*;
import java.sql.*;
/**
 * Application that will show and manage many D&D 5.0 magic objects
 * For my personal use.
 *
 * @author StrangeClone
 */
public class MagicObjectManager extends JFrame {
    /**
     * Database url
     */
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dnddata";
    /**
     * Database Username
     */
    private static final String USERNAME = "root";
    /**
     * Database password
     */
    private static final String PASSWORD = "";
    /**
     * Main panel
     */
    private JPanel panel;
    /**
     * List of the magic objects currently shown by the itemsList
     */
    private final DefaultListModel<MagicObject> listModel;
    /**
     * List of Magic Objects, that con be selected
     */
    private JList<MagicObject> objectJList;
    /**
     * Area where the properties of the selected Magic Object are shown
     */
    private JTextArea propertiesArea;
    /**
     * Area where the description of the selected Magic Object is shown
     */
    private JTextArea descriptionArea;
    /**
     * Label where the name of the selected Magic Object is shown
     */
    private JLabel nameLabel;
    /**
     * Label where the rarity of the selected Magic Object is shown
     */
    private JLabel rarityLabel;
    /**
     * Label where the type of the selected Magic Object is shown
     */
    private JLabel typeLabel;
    /**
     * Label where the subtype of the selected Magic Object is shown
     */
    private JLabel subtypeLabel;
    /**
     * Button to add a new Magic Object (opens a dialog)
     */
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    /**
     * Creates the application
     */
    MagicObjectManager() {
        setContentPane(panel);
        setVisible(true);
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Dungeons and Dragons Tools");

        listModel = new DefaultListModel<>();

        objectJList.clearSelection();
        objectJList.addListSelectionListener(e -> showItem(objectJList.getSelectedValue()));

        getItems();
        objectJList.setModel(listModel);
        objectJList.setSelectedIndex(0);

        addButton.addActionListener(e -> {
                AddMagicObject dialog = new AddMagicObject(this);
                dialog.setSize(500, 500);
                dialog.setTitle("Add a new Magic Object");
                dialog.setVisible(true);
        });
        editButton.addActionListener(e -> {
            EditMagicObject dialog = new EditMagicObject(this, objectJList.getSelectedValue());
            dialog.setSize(500, 500);
            dialog.setTitle("Edit Magic Object");
            dialog.setVisible(true);
        });
        deleteButton.addActionListener(e -> deleteItem(objectJList.getSelectedValue()));
    }

    private void showItem(MagicObject item) {
        if(item != null) {
            nameLabel.setText(item.getName());
            if (item.requiresAttunement()) {
                rarityLabel.setText(item.getRarity() + " (Requires attunement)");
            } else {
                rarityLabel.setText(item.getRarity());
            }
            typeLabel.setText(item.getType());
            subtypeLabel.setText(item.getSubtype());
            descriptionArea.setText(item.getDescription());
            propertiesArea.setText(item.getProperties());
        }
    }

    public boolean addItem(MagicObject newItem) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            insertItem(newItem, connection);
            listModel.add(0, newItem);
            objectJList.setSelectedIndex(0);
            return true;
        }catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private void deleteItem(MagicObject item) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            PreparedStatement deletion = connection.prepareStatement("DELETE FROM magicobject WHERE name = ?");
            deletion.setString(1, item.getName());
            deletion.execute();
            objectJList.clearSelection();
            listModel.remove(listModel.indexOf(item));
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public boolean modifyItem(String previousName, MagicObject item) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            if(!previousName.equals(item.getName())) {
                insertItem(item, connection);
                PreparedStatement deletion = connection.prepareStatement("DELETE FROM magicobject WHERE name = ?");
                deletion.setString(1, previousName);
                deletion.execute();
                int index = listModel.indexOf(objectJList.getSelectedValue());
                listModel.get(index).setName(item.getName());
                listModel.get(index).setRarity(item.getRarity());
                listModel.get(index).setAttunement(item.requiresAttunement());
                listModel.get(index).setType(item.getType());
                listModel.get(index).setSubtype(item.getSubtype());
                listModel.get(index).setDescription(item.getDescription());
                listModel.get(index).setProperties(item.getProperties());
                objectJList.setSelectedIndex(index);
                showItem(objectJList.getSelectedValue());
            }else {
                PreparedStatement update = connection.prepareStatement("UPDATE magicobject SET " +
                        "rarity = ?, attunement = ?, type = ?, subtype = ?, description = ?, properties = ?" +
                        "WHERE name = ?");
                update.setString(1, item.getRarity());
                update.setBoolean(2, item.requiresAttunement());
                update.setString(3, item.getType());
                update.setString(4, item.getSubtype());
                update.setString(5, item.getDescription());
                update.setString(6, item.getProperties());
                update.setString(7, item.getName());
                update.execute();
                for(int i = 0; i < listModel.size(); i++) {
                    if (listModel.get(i).getName().equals(item.getName())) {
                        listModel.set(i, item);
                        showItem(item);
                        break;
                    }
                }
            }
            return true;
        }catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private void insertItem(MagicObject item, Connection connection) throws SQLException {
        PreparedStatement query = connection.prepareStatement(
                "INSERT INTO magicobject (name, rarity, attunement, type, subtype, description, properties)" +
                        "VALUES (?,?,?,?,?,?,?)");
        query.setString(1, item.getName());
        query.setString(2, item.getRarity());
        query.setBoolean(3, item.requiresAttunement());
        query.setString(4, item.getType());
        query.setString(5, item.getSubtype());
        query.setString(6, item.getDescription());
        query.setString(7, item.getProperties());
        query.execute();
    }

    /**
     * Function to get all the Magic Items in the database
     */
    private void getItems() {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MagicObject");
            if(statement.execute()) {
                ResultSet result = statement.getResultSet();
                while (result.next()) {
                    listModel.add(listModel.size(), new MagicObject(result.getString("name"),
                            result.getString("rarity"),
                            result.getBoolean("attunement"),
                            result.getString("type")));
                    if(result.getString("subtype") != null) {
                        listModel.get(listModel.size() - 1).setSubtype(result.getString("subtype"));
                    }
                    if(result.getString("description") != null) {
                        listModel.get(listModel.size() - 1).setDescription(result.getString("description"));
                    }
                    if(result.getString("properties") != null) {
                        listModel.get(listModel.size() - 1).setProperties(result.getString("properties"));
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Launch the application
     *
     * @param args params
     */
    public static void main(String[] args) {
        new MagicObjectManager();
    }
}
