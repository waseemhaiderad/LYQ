
package lnq.com.lnq.model.gson_converter_models.profile_information;


public class CreateMultipleProfileMainObject {

    private Integer status;
    private String message;
    private CreateUserSecondaryProfile createUserSecondaryProfile;

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

    public CreateUserSecondaryProfile getCreateUserSecondaryProfile() {
        return createUserSecondaryProfile;
    }

    public void setCreateUserSecondaryProfile(CreateUserSecondaryProfile createUserSecondaryProfile) {
        this.createUserSecondaryProfile = createUserSecondaryProfile;
    }
}
