package com.ibrhmdurna.chatapp.util;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.message.Text;
import com.ibrhmdurna.chatapp.database.strategy.SendMessage;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Message;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        final String user_id = intent.getStringExtra("user_id");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (action) {
            case "cancel":
                Delete.getInstance().request(user_id);
                notificationManager.cancel(user_id, 0);
                break;
            case "confirm":
                Insert.getInstance().friend(user_id);
                notificationManager.cancel(user_id, 0);
                break;
            case "profile":
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("user_id", user_id);
                context.startActivity(profileIntent);
                notificationManager.cancel(user_id, 1);
                break;
            case "reply":
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

                if (remoteInput != null) {
                    final CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
                    Message message = new Message(FirebaseAuth.getInstance().getUid(), replyText.toString(), "Text", System.currentTimeMillis(), false, false, false, false);
                    SendMessage sendMessage = new SendMessage(new Text());
                    sendMessage.Send(message, user_id);

                    Update.getInstance().messageSeen(user_id, true);
                    Update.getInstance().chatSeen(user_id, true);

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Update.getInstance().messageSeen(user_id, false);
                            Update.getInstance().chatSeen(user_id, false);
                        }
                    },2000);

                    notificationManager.cancel(user_id, 2);
                }
                break;
            case "read":
                Update.getInstance().messageSeen(user_id, true);
                Update.getInstance().chatSeen(user_id, true);

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Update.getInstance().messageSeen(user_id, false);
                        Update.getInstance().chatSeen(user_id, false);
                    }
                },2000);

                notificationManager.cancel(user_id, 2);
                break;
        }
    }
}
