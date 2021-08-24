package lnq.com.lnq.model.gson_converter_models.registerandlogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgetPasswordNewModel {
    @Expose
    @SerializedName("status")
    private int status;
    @Expose
    @SerializedName("pass_token")
    private String pass_token;
    @Expose
    @SerializedName("user_pass")
    private String user_pass;
    @Expose
    @SerializedName("conf_user_pass")
    private String conf_user_pass;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPass_token() {
        return pass_token;
    }

    public void setPass_token(String pass_token) {
        this.pass_token = pass_token;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public String getConf_user_pass() {
        return conf_user_pass;
    }

    public void setConf_user_pass(String conf_user_pass) {
        this.conf_user_pass = conf_user_pass;
    }
}
