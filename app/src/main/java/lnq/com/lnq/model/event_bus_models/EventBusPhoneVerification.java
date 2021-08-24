package lnq.com.lnq.model.event_bus_models;

public class EventBusPhoneVerification {
    private String VerificationId ;
    private String Code ;
    private String mFlag ;

    public EventBusPhoneVerification(String verificationId, String code, String mFlag) {
        VerificationId = verificationId;
        Code = code;
        this.mFlag = mFlag;
    }

    public String getVerificationId() {
        return VerificationId;
    }

    public void setVerificationId(String verificationId) {
        VerificationId = verificationId;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getmFlag() {
        return mFlag;
    }

    public void setmFlag(String mFlag) {
        this.mFlag = mFlag;
    }
}
