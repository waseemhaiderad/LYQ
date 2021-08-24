package lnq.com.lnq.model.event_bus_models;

public class EventBusBlurMainActivity {

    private boolean isBlurView;

    public EventBusBlurMainActivity(boolean isBlurView) {
        this.isBlurView = isBlurView;
    }

    public boolean isBlurView() {
        return isBlurView;
    }

    public void setBlurView(boolean blurView) {
        isBlurView = blurView;
    }
}
