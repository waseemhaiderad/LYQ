package lnq.com.lnq.model.event_bus_models;

import com.linkedin.android.spyglass.mentions.Mentionable;

public class EventBusMentionUsersClick {
    int position;
    Mentionable userName;
    String userId;
    String profileId;

    public EventBusMentionUsersClick(int position, Mentionable userName, String userId, String profileId) {
        this.position = position;
        this.userName = userName;
        this.userId = userId;
        this.profileId = profileId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Mentionable getUserName() {
        return userName;
    }

    public void setUserName(Mentionable userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
