package lnq.com.lnq.model.gson_converter_models.tasknote;

public class TaskNoteMainObject {

    private Integer status;
    private String message;
    private TaskData createTask;
    private NoteData createNote;

    public NoteData getCreateNote() {
        return createNote;
    }

    public void setCreateNote(NoteData createNote) {
        this.createNote = createNote;
    }

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

    public TaskData getCreateTask() {
        return createTask;
    }

    public void setCreateTask(TaskData createTask) {
        this.createTask = createTask;
    }
}
