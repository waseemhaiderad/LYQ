package lnq.com.lnq.model.salesforce;

public class SalesforceAttributes {

    private String type;
    private String referenceId;

    public SalesforceAttributes(String type, String referenceId) {
        this.type = type;
        this.referenceId = referenceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
