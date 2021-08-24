package lnq.com.lnq.model.event_bus_models;

public class EventBusProfilesClick {
    int position;
    String profileId;
    String userImage;
    String userJobTitle;
    String userCompany;

    public EventBusProfilesClick(int position, String profileId, String userImage, String userJobTitle, String userCompany) {
        this.position = position;
        this.profileId = profileId;
        this.userImage = userImage;
        this.userJobTitle = userJobTitle;
        this.userCompany = userCompany;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserJobTitle() {
        return userJobTitle;
    }

    public void setUserJobTitle(String userJobTitle) {
        this.userJobTitle = userJobTitle;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }
}
