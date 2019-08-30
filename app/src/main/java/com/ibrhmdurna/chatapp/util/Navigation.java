package com.ibrhmdurna.chatapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Navigation {

    private static Navigation instance;

    public synchronized static Navigation getInstance(){
        if(instance == null){
            synchronized (Navigation.class){
                instance = new Navigation();
            }
        }

        return instance;
    }

    private Navigation(){}

    public void goPage(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public void goPageAfterFinish(Activity context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        context.finish();
    }

    public void goPage(Context context, Activity activity, Intent... flags){
        Intent intent = new Intent(context, activity.getClass());
        for (Intent item : flags){
            intent.addFlags(item.getFlags());
        }
        context.startActivity(intent);
    }

    public void goPageAfterFinish(Activity context, Activity activity, Intent... flags){
        Intent intent = new Intent(context, activity.getClass());
        for (Intent item : flags){
            intent.addFlags(item.getFlags());
        }
        context.startActivity(intent);
        context.finish();
    }
}
