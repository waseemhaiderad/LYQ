package lnq.com.lnq.model.event_bus_models;

import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;

public class EventBusChatGestures {
    private SwipeDetector.SwipeTypeEnum swipeTypeEnum;

    public EventBusChatGestures(SwipeDetector.SwipeTypeEnum swipeTypeEnum) {
        this.swipeTypeEnum = swipeTypeEnum;
    }

    public SwipeDetector.SwipeTypeEnum getSwipeTypeEnum() {
        return swipeTypeEnum;
    }

    public void setSwipeTypeEnum(SwipeDetector.SwipeTypeEnum swipeTypeEnum) {
        this.swipeTypeEnum = swipeTypeEnum;
    }
}
