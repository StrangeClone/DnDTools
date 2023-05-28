package org.example.magicobject;

import org.example.call.Call;

public class DescriptionGenerator {

    String result;

    public DescriptionGenerator(String name,
                                String rarity,
                                String type,
                                String subtype,
                                String properties) {
        StringBuilder prompt = new StringBuilder("Describe the appearance of D&D 5-th edition Magic Object");
        if(name.isEmpty()) {
            prompt.append(". The Magic Object name is: \"").append(name).append("\"");
        }
        prompt.append(". The Magic Object rarity is :\"").append(rarity).append("\", but you shouldn't explicitly" +
                                                                                "specify it in the description");
        prompt.append(". The Magic Object type is :\"").append(type).append("\"");
        if(!subtype.isEmpty()) {
            prompt.append(". The Magic Object is a ").append(subtype);
        }
        if(!properties.isEmpty()) {
            prompt.append(". The Magic Object properties are: \"").append(properties).append("\"");
        }
        prompt.append(". The description must be 4-5 sentences long. It is extremely important that you don't give" +
                      " information about the game properties of the Magic Object, but you have to describe the " +
                      "appearance in a way that may give hints about its game properties. For example: if the object" +
                      " can deal fire damages, the object could be decorated with engravings of flames, dragons or other" +
                      " figures connected with the fire; if the object can heal wounds, it could have a heart or a nature" +
                      " goddess painted on it; if it is a dark object, able to destroy or curse, it could be decorated " +
                      "with skulls, demons or other evil symbols." +
                      "If in the Magic Object properties is specified the material it is made of, it " +
                      "is very important that you include this material in the description.");
        try {
            Call call = new Call(prompt.toString());
            result = call.getResult();
        }catch (Exception exception) {
            result = "A network error occurred: " + exception.getMessage() + "\nPlease retry.";
        }
    }

    public String getResult() {
        return result;
    }
}
