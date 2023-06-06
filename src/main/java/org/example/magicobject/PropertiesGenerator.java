package org.example.magicobject;

import org.example.MagicObjectManager;
import org.example.call.Call;

import java.sql.*;
import java.util.List;

public class PropertiesGenerator {

    String result;

    public PropertiesGenerator(String name,
                               String rarity,
                               boolean attunement,
                               String type,
                               String subtype,
                               String description) throws Exception {
        StringBuilder prompt = new StringBuilder("Generate the properties of a D&D 5 Magic Object. ");
        String examples = generateExamples(rarity, attunement, type);
        if(!examples.isEmpty()) {
            prompt.append("Here there are some examples:\n").append(examples);
        }
        prompt.append("These are the information of the Object to describe:\n");
        prompt.append("Name: \"").append(name).append("\".\n");
        prompt.append("Rarity: \"").append(rarity).append("\".\n");
        prompt.append("Requires attunement: ").append(attunement?"Yes\n":"No\n");
        prompt.append("Type: \"").append(type).append("\".\n");
        prompt.append("Subtype: \"").append(subtype).append("\".\n");
        prompt.append("Description: \"").append(description).append("\".\n");
        try {
            Call call = new Call(prompt.toString());
            result = call.getResult();
        }catch (Exception e) {
            result = "Error: " + e.getMessage() + "\nPlease retry.";
            throw new Exception(result);
        }
    }

    public String getResult() {
        return result;
    }

    static String generateExamples(String rarity, boolean attunement, String type) {
        StringBuilder builder = new StringBuilder();
        try(Connection connection = DriverManager.getConnection(MagicObjectManager.JDBC_URL, MagicObjectManager.USERNAME, MagicObjectManager.PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM magicobject " +
                    "WHERE rarity = ? AND type = ? AND attunement = ?");
            statement.setString(1, rarity);
            statement.setString(2, type);
            statement.setBoolean(3, attunement);
            List<MagicObject> selectItems = DescriptionGenerator.selectSome(statement.executeQuery());
            for(MagicObject m : selectItems) {
                builder.append("If the user enters these information:\n");
                builder.append("Name: ").append(m.getName()).append("\n");
                builder.append("Rarity: ").append(m.getRarity()).append("\n");
                builder.append("Requires attunement: ").append(m.requiresAttunement()?"Yes\n":"No\n");
                builder.append("Type: ").append(m.getType()).append("\n");
                builder.append("Subtype: ").append(m.getSubtype()).append("\n");
                builder.append("Properties: ").append(m.getDescription()).append("\n");
                builder.append("A valid answer would be: \"").append(m.getProperties()).append("\n");
            }
            return builder.toString();
        }catch (SQLException e) {
            return builder.toString();
        }
    }
}
