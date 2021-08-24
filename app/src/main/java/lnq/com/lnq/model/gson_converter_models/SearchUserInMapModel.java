package lnq.com.lnq.model.gson_converter_models;

public class SearchUserInMapModel {
    private String userId;
    private String userName;
    private String userImage;
    private String userJobTitle;
    private String userCompany;
    private String userDistance;
    private String matchType;
    private String userTags;
    private String userAddress;
    private String userStatus;
    private String userBio;
    private String userTasks;
    private String userNotes;
    private String userInterests;

    public SearchUserInMapModel(String userId, String userName, String userImage, String userJobTitle, String userCompany, String userDistance, String matchType, String userTags, String userAddress, String userStatus, String userBio, String userTasks, String userNotes, String userInterests) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.userJobTitle = userJobTitle;
        this.userCompany = userCompany;
        this.userDistance = userDistance;
        this.matchType = matchType;
        this.userTags = userTags;
        this.userAddress = userAddress;
        this.userStatus = userStatus;
        this.userBio = userBio;
        this.userTasks = userTasks;
        this.userNotes = userNotes;
        this.userInterests = userInterests;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUserDistance() {
        return userDistance;
    }

    public void setUserDistance(String userDistance) {
        this.userDistance = userDistance;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getUserTags() {
        return userTags;
    }

    public void setUserTags(String userTags) {
        this.userTags = userTags;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(String userTasks) {
        this.userTasks = userTasks;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    public String getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(String userInterests) {
        this.userInterests = userInterests;
    }
}
