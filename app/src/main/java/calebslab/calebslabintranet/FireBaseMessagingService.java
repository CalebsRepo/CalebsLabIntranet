/************************************************************************
 *  업무구분명: push메시지 설정
 *  세부업무구분명: 서버에서 전송된 메세지를 세팅하고 push 이벤트를 설정하여 사용자에게 보여준다.
 *  작성자: 남기완
 *  설명: 1) push메세지 세팅 및 이벤트 설정
 *  ------------------------------------------------
 *  변경이력
 *  ------------------------------------------------
 *  NO   날짜          		       작성자       내용
 *  1   2019-03-04                 남기완      신규생성
 **************************************************************************/
package calebslab.calebslabintranet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.media.RingtoneManager;

import android.net.Uri;

import android.os.Build;

import android.support.v4.app.NotificationCompat;

import android.util.Log;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URL;
import java.util.Map;


public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Bitmap bigPicture;


    /**
     * 내용 : 서버에서 전송된 메세지를 받는다
     * param remoteMessage ; 전송된 메시지 값
     **/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /*background에서도 sendNotification()함수를 타기 값 설정*/
        Map<String, String> data = remoteMessage.getData();

        //you can get your text message here.
        String title    = data.get("title");
        String body     = data.get("body");
        String type     = data.get("type");
        String picture  = data.get("picture");

        sendNotification(title, body, type, picture);
    }

    /**
     * 내용 : 서버에서 전송된 메세지를 세팅하고 push 이벤트를 설정하여 사용자에게 보여준다.
     * param messageTitle : push 메시지 제목
     * param messageBody : push 메시지 내용
     * param messageType : push 전송처리 구분
     * param messagePicture : 이미지
     **/
    private void sendNotification(String messageTitle, String messageBody, String messageType, String messagePicture) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // (int)(System.currentTimeMillis()/1000) 현재시간을 id값으로 줘서 전송되는 push메세지를 쌓이도록 한다.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.CalebNoti);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),android.R.drawable.zoom_plate);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);

        notificationBuilder.setSmallIcon(R.mipmap.logo); // 작은 아이콘

        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo)); // 큰 아이콘

        notificationBuilder.setContentTitle(messageTitle); // push 제목

        switch (messageType) {
            case "noti" : case "project":
                Log.d("das", "게시판 , 공지사항~~~~~~~~~~~~");
                notificationBuilder.setContentText(messageBody); // push 내용
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(messageTitle) // 아래로 내렸을 시 제목
                        .bigText(messageBody)); // 아래로 내렸을 시 내용
                break;
            case "holiday" :
                Log.d("das", "휴가~~~~~~~~~~~~~~");
                notificationBuilder.setContentText("아래로 당겨주세요."); // push 내용
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(messageTitle) // 아래로 내렸을 시 제목
                        .bigText(messageBody)); // 아래로 내렸을 시 내용
                break;

            case "picture" :
                Log.d("das", "그림~~~~~~~~~~~~~~");
                try {
                    URL url = new URL(messagePicture);
                    bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                notificationBuilder.setContentText(messageBody); // push 내용
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle(messageTitle) // push알림 아래로 내렸을 시 제목
                        .setSummaryText(messageBody) // push알림 아래로 내렸을 시 내용
                        .bigPicture(bigPicture));    // 표시할 사진
                break;
        }

        notificationBuilder.setAutoCancel(true); // 선택시 자동으로 push메세지 삭제

        notificationBuilder.setWhen(System.currentTimeMillis()); // push메세지 전송 시간

        notificationBuilder.setSound(defaultSoundUri); // 사운드 설정

        notificationBuilder.setDefaults(Notification.FLAG_SHOW_LIGHTS); // 기본 설정

        notificationBuilder.setLights(Color.rgb(165,102,255), 2000,2000); // LED 설정

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX); // 우선순위

        notificationBuilder.setContentIntent(pendingIntent); // 실행할 작업이 담긴 PendingIntent

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 해당 기기의 OS버전이 오레오이상일때

            String channelName = getString(R.string.CalebNoti);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.rgb(165,102,255));
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);

        }

        // (int)(System.currentTimeMillis()/1000) 현재시간을 id값으로 줘서 전송되는 push메세지를 쌓이도록 한다.
        notificationManager.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());

    }

}