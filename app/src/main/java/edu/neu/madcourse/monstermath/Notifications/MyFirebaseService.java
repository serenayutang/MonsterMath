package edu.neu.madcourse.monstermath.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import edu.neu.madcourse.monstermath.MainActivity;
import edu.neu.madcourse.monstermath.R;


public class MyFirebaseService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";

    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage);

        }
        extractPayloadDataForegroundCase(remoteMessage);
    }

    private void updateToken(String newToken) {
        // firebaseUser no use
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // to be discuss if the token need to be updated
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", newToken);
        databaseReference.updateChildren(hashMap);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        // set notification's tap response
        // reference: https://developer.android.com/training/notify-user/build-notification
        Intent intent = new Intent(this, MainActivity.class);
        // addFlags() helps preserve the user's expected navigation experience after they open the app via notification
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(this);
        }

        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                // automatically removes the notification when the user taps it
                .setAutoCancel(true)
                // set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .build();
        // make the notification appear
        notificationManager.notify(0, notification);
    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractPayloadDataForegroundCase(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            postToastMessage(remoteMessage.getData().get("title"));
        }
    }
}



