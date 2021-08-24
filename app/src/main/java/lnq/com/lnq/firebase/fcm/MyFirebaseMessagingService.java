package lnq.com.lnq.firebase.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.model.event_bus_models.EventBusActivityCount;
import lnq.com.lnq.model.event_bus_models.EventBusLogOut;
import lnq.com.lnq.model.event_bus_models.EventBusTotalCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChat;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateChatCount;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateMessages;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            JSONObject mJson = new JSONObject(params);
            String notificationType = "";
            try {
                notificationType = mJson.getString("notification_type");

                if (notificationType.equals("mute-chat")) {
                    EventBus.getDefault().post(new EventBusUserSession("msg_recieved"));
                    EventBus.getDefault().post(new EventBusUpdateChat());
                    EventBus.getDefault().post(new EventBusUpdateMessages());
                } else if (notificationType.equals("chat")) {
                    EventBus.getDefault().post(new EventBusUserSession("msg_recieved"));
//                    if (remoteMessage.getNotification() != null) {
//                        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//                    }
                    Gson gson = new Gson();
                    EventBus.getDefault().post(gson.fromJson(mJson.toString(), GetChatData.class));

                    String currentFragment = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CURRENT_FRAGMENT, "");
                    String senderID = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SENDER_ID, "");
                    String receiverId = mJson.getString("sender_id").equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? mJson.getString("receiver_id") : mJson.getString("sender_id");

                    if (currentFragment.equalsIgnoreCase(Constants.CHAT) &&
                            senderID.equalsIgnoreCase(receiverId)) {
                        EventBus.getDefault().post(new EventBusUpdateChat());
                    } else if(currentFragment.equalsIgnoreCase(Constants.GROUP_CHAT_FRAGMENT)){
                        EventBus.getDefault().post(new EventBusUpdateChat());
                    }else {
//                        int count = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.CHAT_COUNT, "0"));
//                        count++;
//                        LnqApplication.getInstance().editor.putString(Constants.CHAT_COUNT, String.valueOf(count)).apply();
//                        int total = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.TOTAL_COUNT, "0"));
//                        total++;
//                        LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(total)).apply();
//                        EventBus.getDefault().post(new EventBusTotalCount());
                        EventBus.getDefault().post(new EventBusUpdateChatCount(0));
                    }
                } else if (notificationType.equals("logout")) {
                    EventBus.getDefault().post(new EventBusLogOut());
                } else if (notificationType.equals("lnq_request") || notificationType.equals("lnq_request_accepted")) {

                    if (remoteMessage.getNotification() != null) {
                        String sender_id = mJson.getString("sender_id");
                        if (notificationType.equals("lnq_request")) {
                            EventBus.getDefault().post(new EventBusUpdateUserStatus(sender_id, Constants.CONTACTED, true));
                            EventBus.getDefault().post(new EventBusUserSession("lnq_requested_recieved"));
                        } else if (notificationType.equals("lnq_request_accepted")) {

                            EventBus.getDefault().post(new EventBusUpdateUserStatus(sender_id, Constants.CONNECTED, false));
                        }
                        int count = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.ACTIVITY_COUNT, "0"));
                        count++;
                        LnqApplication.getInstance().editor.putString(Constants.ACTIVITY_COUNT, String.valueOf(count)).apply();
                        int total = Integer.parseInt(LnqApplication.getInstance().sharedPreferences.getString(Constants.TOTAL_COUNT, "0"));
                        total++;
                        LnqApplication.getInstance().editor.putString(Constants.TOTAL_COUNT, String.valueOf(total)).apply();
                        EventBus.getDefault().post(new EventBusTotalCount());

//                        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.CURRENT_FRAGMENT, "").equals("alerts")) {
                        EventBus.getDefault().post(new EventBusActivityCount());
//                        }
                        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    }
                } else {
                    showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String title, String message) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, FcmIntentService.class);

        PendingIntent action1PendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Lnq_CH_01";
        String channelName = "LnqChannel";

        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(defaultSoundUri, attributes);

            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(action1PendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, builder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

}