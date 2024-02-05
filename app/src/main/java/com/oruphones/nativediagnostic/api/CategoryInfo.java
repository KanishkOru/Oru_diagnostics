package com.oruphones.nativediagnostic.api;

/**
 * Created by Pervacio on 31-08-2017.
 */

public class CategoryInfo {
    private String name = "";
    private String displayname = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description = "";

    public CategoryInfo() {
    }

    public CategoryInfo(String name, String displayName, String description) {
        this.name = name;
        this.displayname = displayName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
}
