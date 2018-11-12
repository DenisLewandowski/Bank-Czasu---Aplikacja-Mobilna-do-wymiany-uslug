package pl.denislewandowski.bankczasu;

import java.util.Date;
import java.util.UUID;

public class Service {
    private UUID id;
    private String name;
    private String description;
    private Category category;
    private Date creationDate;
    private int timeCurrencyValue;
    private int serviceOwnerId;
    private boolean isFavorite;


    public Service(String name, String description, Category category, Date creationDate, int timeCurrencyValue, int serviceOwnerId, boolean isFavorite) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.creationDate = creationDate;
        this.timeCurrencyValue = timeCurrencyValue;
        this.serviceOwnerId = serviceOwnerId;
        this.isFavorite = isFavorite;
    }

    public Service() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getTimeCurrencyValue() {
        return timeCurrencyValue;
    }

    public void setTimeCurrencyValue(int timeCurrencyValue) {
        this.timeCurrencyValue = timeCurrencyValue;
    }

    public int getServiceOwnerId() {
        return serviceOwnerId;
    }

    public void setServiceOwnerId(int serviceOwnerId) {
        this.serviceOwnerId = serviceOwnerId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
