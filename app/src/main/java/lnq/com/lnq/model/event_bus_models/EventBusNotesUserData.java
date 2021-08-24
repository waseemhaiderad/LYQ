package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.userprofile.UserNotes;
import lnq.com.lnq.model.userprofile.UserTasks;

public class EventBusNotesUserData {

    private String userId, connectionStatus, userFirstName;
    private List<UserTasks> tasks;
    private UserNotes notes;

    public EventBusNotesUserData(String userId, String connectionStatus, String userFirstName, UserNotes notes, List<UserTasks> tasks) {
        this.userId = userId;
        this.connectionStatus = connectionStatus;
        this.userFirstName = userFirstName;
        this.notes = notes;
        this.tasks = tasks;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public UserNotes getNotes() {
        return notes;
    }

    public void setNotes(UserNotes notes) {
        this.notes = notes;
    }

    public List<UserTasks> getTasks() {
        return tasks;
    }

    public void setTasks(List<UserTasks> tasks) {
        this.tasks = tasks;
    }
}
