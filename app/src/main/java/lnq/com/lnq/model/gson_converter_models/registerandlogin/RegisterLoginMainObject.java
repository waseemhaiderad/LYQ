package lnq.com.lnq.model.gson_converter_models.registerandlogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserSecondaryProfile;

public class RegisterLoginMainObject {

    private int status;
    private String message;
    private SignUpData signup;
    private LogInData login;
    private LogInData secondaryEmails;
    private List<CreateUserSecondaryProfile> user_profiles;
    @Expose
    @SerializedName("frozen_date")
    private String frozenDate;
    private UploadProfileImageData uploadProfileImage;

    public List<CreateUserSecondaryProfile> getUser_profiles() {
        return user_profiles;
    }

    public void setUser_profiles(List<CreateUserSecondaryProfile> user_profiles) {
        this.user_profiles = user_profiles;
    }

    public LogInData getSecondaryEmails() {
        return secondaryEmails;
    }

    public void setSecondaryEmails(LogInData secondaryEmails) {
        this.secondaryEmails = secondaryEmails;
    }

    public String getFrozenDate() {
        return frozenDate;
    }

    public void setFrozenDate(String frozenDate) {
        this.frozenDate = frozenDate;
    }

    public UploadProfileImageData getUploadProfileImage() {
        return uploadProfileImage;
    }

    public void setUploadProfileImage(UploadProfileImageData uploadProfileImage) {
        this.uploadProfileImage = uploadProfileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SignUpData getSignup() {
        return signup;
    }

    public void setSignup(SignUpData signup) {
        this.signup = signup;
    }

    public LogInData getLogin() {
        return login;
    }

    public void setLogin(LogInData login) {
        this.login = login;
    }

}