
package lnq.com.lnq.model.gson_converter_models.qr_code;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUserProfile implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("verification_status")
    @Expose
    private String verificationStatus;
    @SerializedName("user_fname")
    @Expose
    private String userFname;
    @SerializedName("user_lname")
    @Expose
    private String userLname;
    @SerializedName("user_avatar")
    @Expose
    private String userAvatar;
    @SerializedName("avatar_from")
    @Expose
    private String avatarFrom;
    @SerializedName("user_cnic")
    @Expose
    private String userCnic;
    @SerializedName("user_address")
    @Expose
    private String userAddress;
    @SerializedName("user_phone")
    @Expose
    private String userPhone;
    @SerializedName("user_current_position")
    @Expose
    private String userCurrentPosition;
    @SerializedName("user_company")
    @Expose
    private String userCompany;
    @SerializedName("user_birthday")
    @Expose
    private String userBirthday;
    @SerializedName("user_bio")
    @Expose
    private String userBio;
    @SerializedName("user_status_msg")
    @Expose
    private String userStatusMsg;
    @SerializedName("user_interests")
    @Expose
    private String user_interests;
    @SerializedName("task_on_user")
    @Expose
    private TaskOnUser taskOnUser;
    @SerializedName("note_on_user")
    @Expose
    private NoteOnUser noteOnUser;
    @SerializedName("user_history")
    @Expose
    private List<UserHistory> userHistory = null;
    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("is_connection")
    @Expose
    private String isConnection;
    @SerializedName("is_favorite")
    @Expose
    private String isFavorite;
    @SerializedName("is_blocked")
    @Expose
    private String isBlocked;
    @SerializedName("connection_date")
    @Expose
    private String connectionDate;
    @SerializedName("sender_location")
    @Expose
    private String senderLocation;
    @SerializedName("receiver_location")
    @Expose
    private String receiverLocation;
    @SerializedName("visible_to")
    @Expose
    private String visibleTo;
    @SerializedName("visible_at")
    @Expose
    private String visibleAt;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("user_lat")
    @Expose
    private String userLat;
    @SerializedName("user_long")
    @Expose
    private String userLong;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("location")
    @Expose
    private String location;

    protected GetUserProfile(Parcel in) {
        id = in.readString();
        userEmail = in.readString();
        verificationStatus = in.readString();
        userFname = in.readString();
        userLname = in.readString();
        userAvatar = in.readString();
        avatarFrom = in.readString();
        userCnic = in.readString();
        userAddress = in.readString();
        userPhone = in.readString();
        userCurrentPosition = in.readString();
        userCompany = in.readString();
        userBirthday = in.readString();
        userBio = in.readString();
        userStatusMsg = in.readString();
        user_interests = in.readString();
        senderId = in.readString();
        receiverId = in.readString();
        isConnection = in.readString();
        isFavorite = in.readString();
        isBlocked = in.readString();
        connectionDate = in.readString();
        senderLocation = in.readString();
        receiverLocation = in.readString();
        visibleTo = in.readString();
        visibleAt = in.readString();
        locationName = in.readString();
        userLat = in.readString();
        userLong = in.readString();
        distance = in.readString();
        location = in.readString();
    }

    public static final Creator<GetUserProfile> CREATOR = new Creator<GetUserProfile>() {
        @Override
        public GetUserProfile createFromParcel(Parcel in) {
            return new GetUserProfile(in);
        }

        @Override
        public GetUserProfile[] newArray(int size) {
            return new GetUserProfile[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getAvatarFrom() {
        return avatarFrom;
    }

    public void setAvatarFrom(String avatarFrom) {
        this.avatarFrom = avatarFrom;
    }

    public String getUserCnic() {
        return userCnic;
    }

    public void setUserCnic(String userCnic) {
        this.userCnic = userCnic;
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

    public String getUserStatusMsg() {
        return userStatusMsg;
    }

    public void setUserStatusMsg(String userStatusMsg) {
        this.userStatusMsg = userStatusMsg;
    }

    public String getUser_interests() {
        return user_interests;
    }

    public void setUser_interests(String user_interests) {
        this.user_interests = user_interests;
    }

    public TaskOnUser getTaskOnUser() {
        return taskOnUser;
    }

    public void setTaskOnUser(TaskOnUser taskOnUser) {
        this.taskOnUser = taskOnUser;
    }

    public NoteOnUser getNoteOnUser() {
        return noteOnUser;
    }

    public void setNoteOnUser(NoteOnUser noteOnUser) {
        this.noteOnUser = noteOnUser;
    }

    public List<UserHistory> getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(List<UserHistory> userHistory) {
        this.userHistory = userHistory;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getIsConnection() {
        return isConnection;
    }

    public void setIsConnection(String isConnection) {
        this.isConnection = isConnection;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(String connectionDate) {
        this.connectionDate = connectionDate;
    }

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    public String getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(String receiverLocation) {
        this.receiverLocation = receiverLocation;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getUserLat() {
        return userLat;
    }

    public void setUserLat(String userLat) {
        this.userLat = userLat;
    }

    public String getUserLong() {
        return userLong;
    }

    public void setUserLong(String userLong) {
        this.userLong = userLong;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(userEmail);
        dest.writeString(verificationStatus);
        dest.writeString(userFname);
        dest.writeString(userLname);
        dest.writeString(userAvatar);
        dest.writeString(avatarFrom);
        dest.writeString(userCnic);
        dest.writeString(userAddress);
        dest.writeString(userPhone);
        dest.writeString(userCurrentPosition);
        dest.writeString(userCompany);
        dest.writeString(userBirthday);
        dest.writeString(userBio);
        dest.writeString(userStatusMsg);
        dest.writeString(user_interests);
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(isConnection);
        dest.writeString(isFavorite);
        dest.writeString(isBlocked);
        dest.writeString(connectionDate);
        dest.writeString(senderLocation);
        dest.writeString(receiverLocation);
        dest.writeString(visibleTo);
        dest.writeString(visibleAt);
        dest.writeString(locationName);
        dest.writeString(userLat);
        dest.writeString(userLong);
        dest.writeString(distance);
        dest.writeString(location);
    }
}
