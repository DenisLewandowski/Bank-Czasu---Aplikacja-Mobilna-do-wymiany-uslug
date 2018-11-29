package pl.denislewandowski.bankczasu.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Service implements Serializable {
    private String id;
    private String name;
    private String description;
    private int category;
    private Date creationDate;
    private int timeCurrencyValue;
    private String serviceOwnerId;
    private String clientId;

    public Service(String name, String description, int category, Date creationDate, int timeCurrencyValue, String serviceOwnerId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.creationDate = creationDate;
        this.timeCurrencyValue = timeCurrencyValue;
        this.serviceOwnerId = serviceOwnerId;
    }

    public Service() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
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

    public String getServiceOwnerId() {
        return serviceOwnerId;
    }

    public void setServiceOwnerId(String serviceOwnerId) {
        this.serviceOwnerId = serviceOwnerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("category", category);
        result.put("creationDate", creationDate);
        result.put("description", description);
        result.put("serviceOwnerId", serviceOwnerId);
        result.put("timeCurrencyValue", timeCurrencyValue);
        result.put("clientId", clientId);

        return result;
    }




}