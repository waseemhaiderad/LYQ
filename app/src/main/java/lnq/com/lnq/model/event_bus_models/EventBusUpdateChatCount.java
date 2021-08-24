package lnq.com.lnq.model.event_bus_models;

public class EventBusUpdateChatCount {

    private int count;

   public EventBusUpdateChatCount(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}