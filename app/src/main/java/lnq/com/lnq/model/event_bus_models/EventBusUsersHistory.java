package lnq.com.lnq.model.event_bus_models;

import java.util.List;

import lnq.com.lnq.model.userprofile.History;

public class EventBusUsersHistory {
    private String connectionStatus;
    private String isFavorite;
    private String userId;

    private String fName;
    private List<History> historyList;



    public EventBusUsersHistory(String connectionStatus, String isFavorite, String userId, List<History> historyList, String fName) {
        this.connectionStatus = connectionStatus;
        this.isFavorite = isFavorite;
        this.userId = userId;
        this.historyList = historyList;
        this.fName = fName;

    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }
    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }
}
