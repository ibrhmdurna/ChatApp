package com.ibrhmdurna.chatapp.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.local.ProfileActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String user_id = intent.getStringExtra("user_id");

        if(action.equals("cancel")){
            Delete.getInstance().request(user_id);
        }
        else if(action.equals("confirm")){
            Insert.getInstance().friend(user_id);
        }
        else if(action.equals("profile")){
            Intent profileIntent = new Intent(context, ProfileActivity.class);
            profileIntent.putExtra("user_id", user_id);
            context.startActivity(profileIntent);
        }

        final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(user_id, 0);
    }
}
