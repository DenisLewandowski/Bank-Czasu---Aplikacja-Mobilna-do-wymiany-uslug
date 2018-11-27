package pl.denislewandowski.bankczasu.model;

import java.io.Serializable;
import java.util.List;

public class UserItem implements Serializable {
    private String id;
    private String name;
    private int timeCurrency;
    private List<String> serviceIds;

    public UserItem(String id, String name, int timeCurrency, List<String> serviceIds) {
        this.id = id;
        this.name = name;
        this.timeCurrency = timeCurrency;
        this.serviceIds = serviceIds;
    }

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

    public int getTimeCurrency() {
        return timeCurrency;
    }

    public void setTimeCurrency(int timeCurrency) {
        this.timeCurrency = timeCurrency;
    }

    public List<String> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<String> serviceIds) {
        this.serviceIds = serviceIds;
    }
}
