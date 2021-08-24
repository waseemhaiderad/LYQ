package lnq.com.lnq.model.salesforce;

import java.util.List;

public class SalesforceContactModel {
    private List<SalesforceModel> records;

    public SalesforceContactModel(List<SalesforceModel> records) {
        this.records = records;
    }

    public List<SalesforceModel> getRecords() {
        return records;
    }

    public void setRecords(List<SalesforceModel> records) {
        this.records = records;
    }
}
