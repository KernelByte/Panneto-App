package co.panneto.pannetousuario.Service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import co.panneto.pannetousuario.Activities.MenuActivity;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Utils.NotificationUtils;
import co.panneto.pannetousuario.Vo.NotificationVO;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgingService";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String IMAGE_URL = "image";
    private static final String ACTION = "action";
    private static final String ACTION_DESTINATION = "action_destination";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String TOPIC_USUARIO = getString(R.string.topicNotifications);
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_USUARIO);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();
            String url = getApplicationContext().getString(R.string.urlApiRoute) + getApplicationContext().getString(R.string.urlImg);
            Bitmap bitmap = getBitmapFromURL(url + remoteMessage.getData().get(IMAGE_URL));

            handleData(data, bitmap);

        } else if (remoteMessage.getNotification() != null) {

            Bitmap bitmap = null;
            String ruta = null;

            if (remoteMessage.getData() != null) {
                String url = getApplicationContext().getString(R.string.urlApiRoute) + getApplicationContext().getString(R.string.urlImg);
                ruta = url + remoteMessage.getData().get(IMAGE_URL);
                bitmap = getBitmapFromURL(ruta);
                handleNotification(remoteMessage.getNotification(), bitmap);
            } else {
                handleNotification(remoteMessage.getNotification(), null);
            }

        }
    }

    private void handleNotification(final RemoteMessage.Notification RemoteMsgNotification, Bitmap bitmap) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), MenuActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent, bitmap);
        notificationUtils.playNotificationSound();
    }

    private void handleData(Map<String, String> data, Bitmap bitmap) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), MenuActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent, bitmap);
        notificationUtils.playNotificationSound();
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            java.net.URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
