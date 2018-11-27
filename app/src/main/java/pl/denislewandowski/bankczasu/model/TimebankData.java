package pl.denislewandowski.bankczasu.model;

import java.util.List;

public class TimebankData {
    private String id;
    private List<String> members;
    private List<Service> services;
    private List<Chat> chatMessages;

    public TimebankData(String id, List<String> members, List<Service> services, List<Chat> chatMessages) {
        this.id = id;
        this.members = members;
        this.services = services;
        this.chatMessages = chatMessages;
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

    public List<Chat> getMessages() {
        return chatMessages;
    }

    public void setMessages(List<Chat> chatMessages) {
        this.chatMessages = chatMessages;
    }
}
