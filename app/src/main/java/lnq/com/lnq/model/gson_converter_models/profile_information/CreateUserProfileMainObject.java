
package lnq.com.lnq.model.gson_converter_models.profile_information;


public class CreateUserProfileMainObject {

    private Integer status;
    private String message;
    private CreateUserProfile createUserProfile;
    private UpdateUserProfile updateUserProfile;
    private UpdateStatusMessage updateStatusMsg;
    private UpdateProfileImage updateProfileImage;

    public UpdateProfileImage getUpdateProfileImage() {
        return updateProfileImage;
    }

    public void setUpdateProfileImage(UpdateProfileImage updateProfileImage) {
        this.updateProfileImage = updateProfileImage;
    }

    public UpdateStatusMessage getUpdateStatusMsg() {
        return updateStatusMsg;
    }

    public void setUpdateStatusMsg(UpdateStatusMessage updateStatusMsg) {
        this.updateStatusMsg = updateStatusMsg;
    }

    public UpdateUserProfile getUpdateUserProfile() {
        return updateUserProfile;
    }

    public void setUpdateUserProfile(UpdateUserProfile updateUserProfile) {
        this.updateUserProfile = updateUserProfile;
    }

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

    public CreateUserProfile getCreateUserProfile() {
        return createUserProfile;
    }

    public void setCreateUserProfile(CreateUserProfile createUserProfile) {
        this.createUserProfile = createUserProfile;
    }

}
