package com.ibrhmdurna.chatapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.Person;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.main.ChatsFragment;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.models.MessageNotification;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {

    private static NotificationService instance;

    public static synchronized NotificationService getInstance(){
        if(instance == null){
            synchronized (NotificationService.class){
                instance = new NotificationService();
            }
        }
        return instance;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_type = remoteMessage.getData().get("notification_type");
        String name_surname = remoteMessage.getData().get("name_surname");
        String profile_image = remoteMessage.getData().get("profile_image");
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String email = remoteMessage.getData().get("email");
        String click_action = remoteMessage.getData().get("click_action");

        assert notification_type != null;
        switch (notification_type) {
            case "request":
                showRequestNotification(name_surname, profile_image, from_user_id, email, click_action);
                break;
            case "confirm":
                showConfirmNotification(name_surname, profile_image, from_user_id, email, click_action);
                break;
            case "message":
                String message = remoteMessage.getData().get("message");
                showMessageNotification(name_surname, profile_image, from_user_id, email, click_action, message);
                break;
            case "image":
                String message1 = remoteMessage.getData().get("message");
                String image = remoteMessage.getData().get("image");
                showImageMessageNotification(name_surname, profile_image, from_user_id, email, click_action, message1, image);
                break;
            case "deleted":
                showDeletedNotification(name_surname, profile_image, from_user_id, email, click_action);
                break;
        }
    }

    private void showDeletedNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.message";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.messages),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                generateRandom(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel(getString(R.string.reply))
                .build();

        Intent actionIntent = new Intent(this, NotificationReceiver.class);
        actionIntent.putExtra("user_id", fromUid);
        actionIntent.putExtra("action", "reply");
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this,
                generateRandom(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_white_icon,
                getString(R.string.reply),
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorNotification))
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle(nameSurname)
                        .addLine(getString(R.string.this_message_was_deleted)))
                .setContentTitle(nameSurname)
                .setContentText(getString(R.string.this_message_was_deleted))
                .setSubText(email)
                .addAction(replyAction)
                .addAction(R.color.colorNotification, getString(R.string.mark_as_read), PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "read").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setLargeIcon(setProfileImage(profileImage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if(!ChatsFragment.isChat){
            if(ChatActivity.NOTIF_ID != null){
                if(!ChatActivity.NOTIF_ID.equals(fromUid)){
                    notificationManager.notify(fromUid, 2, notificationBuilder.build());
                }
            }
            else{
                notificationManager.notify(fromUid, 2, notificationBuilder.build());
            }
        }
    }

    private void showImageMessageNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction, String message, String image){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.message";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.messages),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                generateRandom(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel(getString(R.string.reply))
                .build();

        Intent actionIntent = new Intent(this, NotificationReceiver.class);
        actionIntent.putExtra("user_id", fromUid);
        actionIntent.putExtra("action", "reply");
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this,
                generateRandom(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_white_icon,
                getString(R.string.reply),
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorNotification))
                .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigLargeIcon(setProfileImage(profileImage))
                            .bigPicture(ImageLoader.getInstance().loadImageSync(image))
                            .setBigContentTitle(nameSurname))
                .setContentTitle(nameSurname)
                .setLargeIcon(ImageLoader.getInstance().loadImageSync(image))
                .setContentText(message.equals("") ? message : getString(R.string._photo))
                .setSubText(email)
                .addAction(replyAction)
                .addAction(R.color.colorNotification, getString(R.string.mark_as_read), PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "read").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if(!ChatsFragment.isChat){
            if(ChatActivity.NOTIF_ID != null){
                if(!ChatActivity.NOTIF_ID.equals(fromUid)){
                    notificationManager.notify(fromUid, 2, notificationBuilder.build());
                }
            }
            else{
                notificationManager.notify(fromUid, 2, notificationBuilder.build());
            }
        }
    }

    private void showMessageNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction, String message){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.message";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.messages),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                generateRandom(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel(getString(R.string.reply))
                .build();

        Intent actionIntent = new Intent(this, NotificationReceiver.class);
        actionIntent.putExtra("user_id", fromUid);
        actionIntent.putExtra("action", "reply");
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this,
                generateRandom(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_white_icon,
                getString(R.string.reply),
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorNotification))
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle(nameSurname)
                        .addLine(message))
                .setContentTitle(nameSurname)
                .setContentText(message)
                .setSubText(email)
                .addAction(replyAction)
                .addAction(R.color.colorNotification, getString(R.string.mark_as_read), PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "read").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setLargeIcon(setProfileImage(profileImage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if(!ChatsFragment.isChat){
            if(ChatActivity.NOTIF_ID != null){
                if(!ChatActivity.NOTIF_ID.equals(fromUid)){
                    notificationManager.notify(fromUid, 2, notificationBuilder.build());
                }
            }
            else{
                notificationManager.notify(fromUid, 2, notificationBuilder.build());
            }
        }
    }

    private void showConfirmNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.confirm";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.friendship_confirm),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                generateRandom(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorNotification))
                .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle(getString(R.string.accepted_friendship_request))
                            .addLine(Html.fromHtml("<b>"+nameSurname+ "</b> "+getString(R.string.accepted_the_req_friendship))))
                .setContentTitle(getString(R.string.accepted_friendship_request))
                .setContentText(Html.fromHtml("<b>"+nameSurname+ "</b> "+getString(R.string.accepted_the_req_friendship)))
                .setSubText(email)
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setLargeIcon(setProfileImage(profileImage))
                .addAction(R.color.colorNotification, getString(R.string.view_profile), resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);
        notificationManager.notify(fromUid, 1, notificationBuilder.build());
    }

    private void showRequestNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.request";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.friendship_requests),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                generateRandom(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent actionIntent = new Intent(this, NotificationReceiver.class);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorNotification))
                .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle(getString(R.string.friendship_req))
                            .addLine(Html.fromHtml("<b>"+nameSurname+ "</b> " + getString(R.string.has_sent_you_req))))
                .setContentTitle(getString(R.string.friendship_req))
                .setSubText(email)
                .setContentText(Html.fromHtml("<b>"+nameSurname+ "</b> " + getString(R.string.has_sent_you_req)))
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .addAction(R.color.colorNotification, getString(R.string.cancel), PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "cancel").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .addAction(R.color.colorNotification, getString(R.string.confirm), PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "confirm").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setLargeIcon(setProfileImage(profileImage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);



        notificationManager.notify(fromUid, 0, notificationBuilder.build());
    }

    private Bitmap setProfileImage(String profileImage){
        if(profileImage.substring(0,8).equals("default_")){
            String text = profileImage.substring(8,9);
            int index = Integer.parseInt(text);
            int image = getProfileImage(index);
            return getCircleBitmap(BitmapFactory.decodeResource(getResources(), image));
        }
        else{
            return getCircleBitmap(ImageLoader.getInstance().loadImageSync(profileImage));
        }
    }

    private int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }

    private int getProfileImage(int index) {
        switch (index){
            case 0:
                return R.mipmap.avatar_0;
            case 1:
                return R.mipmap.avatar_1;
            case 2:
                return R.mipmap.avatar_2;
            case 3:
                return R.mipmap.avatar_3;
            case 4:
                return R.mipmap.avatar_4;
            case 5:
                return R.mipmap.avatar_5;
            case 6:
                return R.mipmap.avatar_6;
            case 7:
                return R.mipmap.avatar_7;
            case 8:
                return R.mipmap.avatar_8;
            case 9:
                return R.mipmap.avatar_9;
        }

        return 0;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
