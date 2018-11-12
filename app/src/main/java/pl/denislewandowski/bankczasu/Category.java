package pl.denislewandowski.bankczasu;

import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private int iconResourceId;


    public Category(UUID id, String name, int iconResourceId) {
        this.id = id;
        this.name = name;
        this.iconResourceId = iconResourceId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }
}
