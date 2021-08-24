
package lnq.com.lnq.model.gson_converter_models.Contacts.connections;

import lnq.com.lnq.model.gson_converter_models.Contacts.NoteOnUser;
import lnq.com.lnq.model.gson_converter_models.Contacts.TaskOnUser;

public class UserConnections {

    private UserConnectionsData user_data;
    private TaskOnUser task_on_user;
    private NoteOnUser note_on_user;

    public UserConnectionsData getUser_data() {
        return user_data;
    }

    public void setUser_data(UserConnectionsData user_data) {
        this.user_data = user_data;
    }

    public TaskOnUser getTask_on_user() {
        return task_on_user;
    }

    public void setTask_on_user(TaskOnUser task_on_user) {
        this.task_on_user = task_on_user;
    }

    public NoteOnUser getNote_on_user() {
        return note_on_user;
    }

    public void setNote_on_user(NoteOnUser note_on_user) {
        this.note_on_user = note_on_user;
    }

}
