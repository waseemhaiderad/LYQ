package lnq.com.lnq.fragments.qrcode;

import lnq.com.lnq.listeners.activit_listeners.SwipeDetector;

public class EventBusActivityGestures {
    private SwipeDetector.SwipeTypeEnum swipeTypeEnum;

    public EventBusActivityGestures(SwipeDetector.SwipeTypeEnum swipeTypeEnum) {
        this.swipeTypeEnum = swipeTypeEnum;
    }

    public SwipeDetector.SwipeTypeEnum getSwipeTypeEnum() {
        return swipeTypeEnum;
    }

    public void setSwipeTypeEnum(SwipeDetector.SwipeTypeEnum swipeTypeEnum) {
        this.swipeTypeEnum = swipeTypeEnum;
    }
}
