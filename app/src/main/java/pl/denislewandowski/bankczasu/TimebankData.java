package pl.denislewandowski.bankczasu;

import java.util.List;

public class TimebankData {
    private String id;
    private List<String> members;
    private List<Service> services;

    public TimebankData(String id, List<String> members, List<Service> services) {
        this.id = id;
        this.members = members;
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
