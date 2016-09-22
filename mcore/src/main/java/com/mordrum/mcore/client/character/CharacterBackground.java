package com.mordrum.mcore.client.character;

public class CharacterBackground {
    private final int id;
    private final String title;
    private final String description;
    private final String perkDescription;
    private final String heirloomDescription;

    public CharacterBackground(int id, String title, String description, String perkDescription, String heirloomDescription) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.perkDescription = perkDescription;
        this.heirloomDescription = heirloomDescription;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPerkDescription() {
        return perkDescription;
    }

    public String getHeirloomDescription() {
        return heirloomDescription;
    }
}
