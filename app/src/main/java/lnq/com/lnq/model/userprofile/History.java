package lnq.com.lnq.model.userprofile;

public class History {

    private String history_type;
    private String history_date;
    private String history_time;
    private String description;
    private String history_taskCompleted;

    public String getHistory_taskCompleted() {
        return history_taskCompleted;
    }

    public void setHistory_taskCompleted(String history_taskCompleted) {
        this.history_taskCompleted = history_taskCompleted;
    }

    public String getHistory_type() {
        return history_type;
    }

    public void setHistory_type(String history_type) {
        this.history_type = history_type;
    }

    public String getHistory_date() {
        return history_date;
    }

    public void setHistory_date(String history_date) {
        this.history_date = history_date;
    }

    public String getHistory_time() {
        return history_time;
    }

    public void setHistory_time(String history_time) {
        this.history_time = history_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
