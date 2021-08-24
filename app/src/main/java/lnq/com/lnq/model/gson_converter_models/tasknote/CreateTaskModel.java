package lnq.com.lnq.model.gson_converter_models.tasknote;

import retrofit2.http.Field;

public class CreateTaskModel {

    private String user_id_task_by;
    private String user_id_task_for;
    private String task_description;
    private String task_duedate;
    private String task_status;
    String profile_id_note_by;
    String profile_id_note_for;
    private int Status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getUser_id_task_by() {
        return user_id_task_by;
    }

    public void setUser_id_task_by(String user_id_task_by) {
        this.user_id_task_by = user_id_task_by;
    }

    public String getUser_id_task_for() {
        return user_id_task_for;
    }

    public void setUser_id_task_for(String user_id_task_for) {
        this.user_id_task_for = user_id_task_for;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getTask_duedate() {
        return task_duedate;
    }

    public void setTask_duedate(String task_duedate) {
        this.task_duedate = task_duedate;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getProfile_id_note_by() {
        return profile_id_note_by;
    }

    public void setProfile_id_note_by(String profile_id_note_by) {
        this.profile_id_note_by = profile_id_note_by;
    }

    public String getProfile_id_note_for() {
        return profile_id_note_for;
    }

    public void setProfile_id_note_for(String profile_id_note_for) {
        this.profile_id_note_for = profile_id_note_for;
    }
}
