
package lnq.com.lnq.model.gson_converter_models.qr_code;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserHistory {

    @SerializedName("history_type")
    @Expose
    private String historyType;
    @SerializedName("history_date")
    @Expose
    private String historyDate;
    @SerializedName("history_time")
    @Expose
    private String historyTime;
    @SerializedName("description")
    @Expose
    private String description;

    public String getHistoryType() {
        return historyType;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public String getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(String historyTime) {
        this.historyTime = historyTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
