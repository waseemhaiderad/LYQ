package lnq.com.lnq.model.gson_converter_models.registerandlogin;

import com.google.gson.annotations.SerializedName;

import lnq.com.lnq.endpoints.EndpointKeys;

public class LogInData {

    @SerializedName(EndpointKeys.ID)
    private String userId;
    @SerializedName(EndpointKeys.USER_Gender)
    private String usergender;
    @SerializedName(EndpointKeys.USER_EMAIL)
    private String userEmail;
    @SerializedName(EndpointKeys.USER_PASS)
    private String userPassword;
    @SerializedName(EndpointKeys.USER_TYPE)
    private String userType;
    @SerializedName(EndpointKeys.USER_STATUS)
    private String userStatus;
    @SerializedName(EndpointKeys.VERIFICATION_STATUS)
    private String verificationStatus;
    @SerializedName(EndpointKeys.DEVICE_TYPE)
    private String deviceType;
    @SerializedName(EndpointKeys.DEVICE_TOCKEN)
    private String deviceToken;
    @SerializedName(EndpointKeys.USER_LOCATION)
    private String userLocation;
    @SerializedName(EndpointKeys.UPDATED_AT)
    private String updatedAt;
    @SerializedName(EndpointKeys.CREATED_AT)
    private String createdAt;
    @SerializedName(EndpointKeys.USER_FNAME)
    private String userFirstName;
    @SerializedName(EndpointKeys.USER_LNAME)
    private String userLastName;
    @SerializedName(EndpointKeys.USER_AVATAR)
    private String userAvatar;
    @SerializedName(EndpointKeys.USER_CNIC)
    private String userCnic;
    @SerializedName(EndpointKeys.USER_ADDRESS)
    private String userAddress;
    @SerializedName(EndpointKeys.USER_PHONE)
    private String userPhone;
    @SerializedName(EndpointKeys.USER_CURRENT_POSITION)
    private String userCurrentPosition;
    @SerializedName(EndpointKeys.USER_COMPANY)
    private String userCompany;
    @SerializedName(EndpointKeys.USER_BIRTHDAY)
    private String userBirthday;
    @SerializedName(EndpointKeys.USER_BIO)
    private String userBio;
    @SerializedName(EndpointKeys.USER_STATUS_MESSAGE)
    private String userStatusMessage;
    @SerializedName(EndpointKeys.USER_INTRESTS)
    private String user_interests;
    @SerializedName(EndpointKeys.VISIBLE_TO)
    private String visibleTo;
    @SerializedName(EndpointKeys.VISIBLE_AT)
    private String visibleAt;
    @SerializedName(EndpointKeys.IS_LOGGEN_IN)
    private String isLoggedIn;
    @SerializedName(EndpointKeys.IS_FROZEN)
    private String is_frozen;
    @SerializedName(EndpointKeys.STATUS_DATE)
    private String status_date;
    @SerializedName("last_login")
    private String lastLogin;
    @SerializedName("secondary_emails")
    private String secondaryEmail;
    @SerializedName("secondary_phones")
    private String secondaryPhones;
    @SerializedName("social_links")
    private String social_links;
    @SerializedName("home_default_view")
    private String home_default_view;
    @SerializedName("contact_default_view")
    private String contact_default_view;
    @SerializedName("profile_status")
    private String profile_status;

    public String getHome_default_view() {
        return home_default_view;
    }

    public void setHome_default_view(String home_default_view) {
        this.home_default_view = home_default_view;
    }

    public String getContact_default_view() {
        return contact_default_view;
    }

    public void setContact_default_view(String contact_default_view) {
        this.contact_default_view = contact_default_view;
    }

    public String getProfile_status() {
        return profile_status;
    }

    public void setProfile_status(String profile_status) {
        this.profile_status = profile_status;
    }

    public String getSocial_links() {
        return social_links;
    }

    public void setSocial_links(String social_links) {
        this.social_links = social_links;
    }

    public String getSecondaryPhones() {
        return secondaryPhones;
    }

    public void setSecondaryPhones(String secondaryPhones) {
        this.secondaryPhones = secondaryPhones;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUsergender() {
        return usergender;
    }

    public void setUsergender(String usergender) {
        this.usergender = usergender;
    }
    public String getIs_frozen() {
        return is_frozen;
    }

    public void setIs_frozen(String is_frozen) {
        this.is_frozen = is_frozen;
    }

    public String getStatus_date() {
        return status_date;
    }

    public void setStatus_date(String status_date) {
        this.status_date = status_date;
    }



    public String getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(String isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getVisibleTo() {
        return visibleTo;
    }

    public void setVisibleTo(String visibleTo) {
        this.visibleTo = visibleTo;
    }

    public String getVisibleAt() {
        return visibleAt;
    }

    public void setVisibleAt(String visibleAt) {
        this.visibleAt = visibleAt;
    }

    public String getUser_interests() {
        return user_interests;
    }

    public void setUser_interests(String user_interests) {
        this.user_interests = user_interests;
    }

    public String getUserStatusMessage() {
        return userStatusMessage;
    }

    public void setUserStatusMessage(String userStatusMessage) {
        this.userStatusMessage = userStatusMessage;
    }

    public String getUserCnic() {
        return userCnic;
    }

    public void setUserCnic(String userCnic) {
        this.userCnic = userCnic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserCurrentPosition() {
        return userCurrentPosition;
    }

    public void setUserCurrentPosition(String userCurrentPosition) {
        this.userCurrentPosition = userCurrentPosition;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

}
