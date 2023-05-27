package org.example.magicobject;

public class MagicObject implements Comparable<MagicObject> {

    String name;
    String rarity;
    boolean attunement;
    String type;
    String subtype;
    String description;
    String properties;

    public MagicObject(String name, String rarity, boolean attunement, String type) {
        this.name = name;
        this.rarity = rarity;
        this.attunement = attunement;
        this.type = type;
        subtype = "";
        description = "";
        properties = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public boolean requiresAttunement() {
        return attunement;
    }

    public void setAttunement(boolean attunement) {
        this.attunement = attunement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return properties;
    }

    @Override
    public int compareTo(MagicObject o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
