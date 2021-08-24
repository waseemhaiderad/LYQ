package lnq.com.lnq.model.event_bus_models;

public class EventBusAboutUserData {

    private String userBio;
    private String userTags;
    private String fName;

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public EventBusAboutUserData(String userBio, String userTags, String fName) {
        this.userTags = userTags;
        this.userBio = userBio;
        this.fName = fName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserTags() {
        return userTags;
    }

    public void setUserTags(String userTags) {
        this.userTags = userTags;
    }
}
