
package lnq.com.lnq.model.userprofile;


public class GetUserProfileMainObject {

    private Integer status;
    private String message;
    private GetUserProfileData getUserProfile;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GetUserProfileData getGetUserProfile() {
        return getUserProfile;
    }

    public void setGetUserProfile(GetUserProfileData getUserProfile) {
        this.getUserProfile = getUserProfile;
    }

}
