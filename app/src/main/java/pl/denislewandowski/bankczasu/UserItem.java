package pl.denislewandowski.bankczasu;

public class UserItem {
    private String id;
    private String name;
    private int timeCurrency;

    public UserItem(String id, String name, int timeCurrency) {
        this.id = id;
        this.name = name;
        this.timeCurrency = timeCurrency;
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
}
