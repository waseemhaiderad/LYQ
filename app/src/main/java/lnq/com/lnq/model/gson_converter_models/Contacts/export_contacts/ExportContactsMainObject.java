
package lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts;

import java.util.List;

public class ExportContactsMainObject {

    private Integer status;
    private String message;
    private List<ExportContactsData> exportContacts = null;

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

    public List<ExportContactsData> getExportContacts() {
        return exportContacts;
    }

    public void setExportContacts(List<ExportContactsData> exportContacts) {
        this.exportContacts = exportContacts;
    }

}
