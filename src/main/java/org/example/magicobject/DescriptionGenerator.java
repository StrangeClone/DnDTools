package org.example.magicobject;

import org.example.MagicObjectManager;
import org.example.call.Call;

import java.sql.*;
import java.util.*;

public class DescriptionGenerator {

    String result;

    public DescriptionGenerator(String name,
                                String rarity,
                                String type,
                                String subtype,
                                String properties) throws Exception {
        StringBuilder prompt = new StringBuilder("Describe the appearance of D&D 5-th edition Magic Object. ");
        prompt.append("The description must be 4-5 sentences long. It is extremely important that you don't give" +
                      " information about the game properties of the Magic Object, but you have to describe the " +
                      "appearance in a way that may give hints about its game properties. For example: if the object" +
                      " can deal fire damages, the object could be decorated with engravings of flames, dragons or other" +
                      " figures connected with the fire; if the object can heal wounds, it could have a heart or a nature" +
                      " goddess painted on it; if it is a dark object, able to destroy or curse, it could be decorated " +
                      "with skulls, demons or other evil symbols." +
                      "If in the Magic Object properties is specified the material it is made of, it " +
                      "is very important that you include this material in the description.");
        String examples = generateExamples(rarity, type);
        if(!examples.isEmpty()) {
            prompt.append("Here there are some examples:\n").append(examples);
        }
        prompt.append("These are the information of the Object to describe:\n");
        prompt.append("Name: \"").append(name).append("\".\n");
        prompt.append("Rarity: \"").append(rarity).append("\".\n");
        prompt.append("Type: \"").append(type).append("\".\n");
        prompt.append("Subtype: \"").append(subtype).append("\".\n");
        prompt.append("Properties: \"").append(properties).append("\".\n");
        try {
            Call call = new Call(prompt.toString());
            result = call.getResult();
        }catch (Exception exception) {
            result = "Error: " + exception.getMessage() + "\nPlease retry.";
            throw new Exception(result);
        }
    }

    public String getResult() {
        return result;
    }

    static String generateExamples(String rarity, String type) {
        StringBuilder builder = new StringBuilder();
        try(Connection connection = DriverManager.getConnection(MagicObjectManager.JDBC_URL, MagicObjectManager.USERNAME, MagicObjectManager.PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM magicobject WHERE rarity = ? AND type = ?");
            statement.setString(1, rarity);
            statement.setString(2, type);
            List<MagicObject> selectItems = selectSome(statement.executeQuery());
            for(MagicObject m : selectItems) {
                builder.append("If the user enters these information:\n");
                builder.append("Name: ").append(m.getName()).append("\n");
                builder.append("Rarity: ").append(m.getRarity()).append("\n");
                builder.append("Type: ").append(m.getType()).append("\n");
                builder.append("Subtype: ").append(m.getSubtype()).append("\n");
                builder.append("Properties: ").append(m.getProperties()).append("\n");
                builder.append("A valid answer would be: \"").append(m.getDescription()).append("\n");
            }
            return builder.toString();
        }catch (SQLException e) {
            return builder.toString();
        }
    }

    static List<MagicObject> selectSome(ResultSet set) throws SQLException {
        List<MagicObject> result = new ArrayList<>();
        while (set.next()) {
            MagicObject newObject = new MagicObject(set.getString("name"),
                    set.getString("rarity"),
                    set.getBoolean("attunmenet"),
                    set.getString("type"));
            newObject.setSubtype(set.getString("subtype"));
            newObject.setDescription(set.getString("description"));
            newObject.setProperties(set.getString("properties"));
            result.add(newObject);
        }
        Random random = new Random();
        while (result.size() > 7) {
            result.remove(random.nextInt(result.size()));
        }
        return result;
    }

}
