package com.ibrhmdurna.chatapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.Person;
import android.support.v4.app.RemoteInput;
import android.text.Html;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ibrhmdurna.chatapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_type = remoteMessage.getData().get("notification_type");
        String name_surname = remoteMessage.getData().get("name_surname");
        String profile_image = remoteMessage.getData().get("profile_image");
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String email = remoteMessage.getData().get("email");
        String click_action = remoteMessage.getData().get("click_action");

        if(notification_type.equals("request")){
            showRequestNotification(name_surname, profile_image, from_user_id, email, click_action);
        }
        else if(notification_type.equals("confirm")){
            showConfirmNotification(name_surname, profile_image, from_user_id, email, click_action);
        }
        else if(notification_type.equals("message")){
            String message = remoteMessage.getData().get("message");
            showMessageNotification(name_surname, profile_image, from_user_id, email, click_action, message);
        }
    }

    private void showMessageNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction, String message) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.messages";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Messages",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Messages Notification");
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

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Reply")
                .build();

        Intent actionIntent = new Intent(this, NotificationReceiver.class);
        actionIntent.putExtra("user_id", fromUid);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this,
                generateRandom(),actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_white_icon,
                "reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();


        Person person = new Person.Builder().setUri(profileImage).setName(nameSurname).build();
        /*
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);
        messagingStyle.setConversationTitle(nameSurname);*/

        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle(person)
                .addMessage(message, System.currentTimeMillis(), person);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getColor(R.color.colorAccent))
                .setStyle(messagingStyle)
                .setSubText(email)
                .addAction(replyAction)
                .setLargeIcon(setProfileImage(profileImage))
                .setContentTitle(nameSurname)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);

        notificationManager.notify(fromUid, generateRandom() , notificationBuilder.build());
    }

    private void showConfirmNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.confirm";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Friendship Confirm",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Confirm Notification");
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
                .setColor(getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle("Accepted friendship request")
                            .addLine(Html.fromHtml("<b>"+nameSurname+"</b> accepted the request for friendship!")))
                .setContentTitle("Accepted friendship request")
                .setContentText(Html.fromHtml("<b>"+nameSurname+"</b> accepted the request for friendship!"))
                .setSubText(email)
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .setLargeIcon(setProfileImage(profileImage))
                .addAction(R.color.colorAccent, "View Profile", resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);
        notificationManager.notify(fromUid, 1, notificationBuilder.build());
    }

    private void showRequestNotification(String nameSurname, String profileImage, String fromUid, String email, String clickAction) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "com.ibrhmdurna.chatapp.request";
        final String GROUP_ID = "com.ibrhmdurna.chatapp.req.group";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannelGroup channelGroup = new NotificationChannelGroup(
                    GROUP_ID,
                    "Request group"
            );

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Friendship Requests",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Requests Notification");
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
                .setColor(getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle("Friendship request")
                            .addLine(Html.fromHtml("<b>"+nameSurname+"</b> has sent you request")))
                .setContentTitle("Friendship request")
                .setSubText(email)
                .setContentText(Html.fromHtml("<b>"+nameSurname+"</b> has sent you request"))
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(true)
                .addAction(R.color.colorAccent, "Cancel", PendingIntent.getBroadcast(
                        this,
                        generateRandom(),
                        actionIntent.putExtra("action", "cancel").putExtra("user_id", fromUid),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .addAction(R.color.colorAccent, "Confirm", PendingIntent.getBroadcast(
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

    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }

    private int getProfileImage(int index) {
        switch (index){
            case 0:
                return R.drawable.ic_avatar_0;
            case 1:
                return R.drawable.ic_avatar_1;
            case 2:
                return R.drawable.ic_avatar_2;
            case 3:
                return R.drawable.ic_avatar_3;
            case 4:
                return R.drawable.ic_avatar_4;
            case 5:
                return R.drawable.ic_avatar_5;
            case 6:
                return R.drawable.ic_avatar_6;
            case 7:
                return R.drawable.ic_avatar_7;
            case 8:
                return R.drawable.ic_avatar_8;
            case 9:
                return R.drawable.ic_avatar_9;
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
