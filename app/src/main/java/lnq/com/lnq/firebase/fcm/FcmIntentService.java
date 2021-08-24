package lnq.com.lnq.firebase.fcm;

import android.app.IntentService;
import android.content.Intent;


public class FcmIntentService extends IntentService {
    // Must create a default constructor
    public FcmIntentService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if (GlobalClass.getInstance().mFappOpen){
//            EventBus.getDefault().post(new EventFcm(intent.getExtras().getString("data")));
//        }else{
//            Intent i  = new Intent(getApplicationContext(), NotificationActivity.class);
//            i.putExtra("data", intent.getExtras().getString("data"));
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            getApplicationContext().startActivity(i);
//        }
    }
}
