package calebslab.calebslabintranet;

import android.app.Notification;
import android.app.NotificationChannel;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Intent;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import android.net.Uri;

import android.os.Build;

import android.os.Vibrator;

import android.support.v4.app.NotificationCompat;

import android.util.Log;

import android.content.Context;



import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";




    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {

        /*background에서도 sendNotification()함수를 타기 값 설정*/
        Map<String, String> data = remoteMessage.getData();

        //you can get your text message here.
        String title = data.get("title");
        String body  = data.get("body");
        int notiId  =  Integer.parseInt(data.get("notiId"));
        sendNotification(title, body, notiId);


        /*
        Log.d(TAG, "From: " + remoteMessage.getFrom());




        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());




            if (true) {

            } else {

                handleNow();

            }

        }

        if (remoteMessage.getNotification() != null) {

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

        }
        */

    }
    /*
    private void handleNow() {

        Log.d(TAG, "Short lived task is done.");

    }
    */




    private void sendNotification(String messageTitle, String messageBody, int messageNotiId) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,

                PendingIntent.FLAG_ONE_SHOT);




        String channelId = getString(R.string.CalebNoti);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =

                new NotificationCompat.Builder(this, channelId)

                        //.setSmallIcon(R.mipmap.ic_launcher)

                        //.setSmallIcon(android.R.drawable.ic_popup_reminder)   // required

                        .setSmallIcon(R.mipmap.logo)

                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo))

                        .setContentTitle(messageTitle)

                        .setContentText("아래로 당겨주세요.")

                        .setAutoCancel(true)

                        .setWhen(System.currentTimeMillis())

                        .setSound(defaultSoundUri)

                        .setDefaults(Notification.FLAG_SHOW_LIGHTS)

                        .setLights(Color.rgb(165,102,255), 2000,2000)

                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(messageTitle)
                                .bigText(messageBody))

                        .setContentIntent(pendingIntent);




        NotificationManager notificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelName = getString(R.string.CalebNoti);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.rgb(165,102,255));

            notificationManager.createNotificationChannel(channel);

        }

        notificationManager.notify(messageNotiId, notificationBuilder.build());

    }

}
