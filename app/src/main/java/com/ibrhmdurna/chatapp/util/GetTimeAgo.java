package com.ibrhmdurna.chatapp.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.ibrhmdurna.chatapp.R;

import java.util.Calendar;
import java.util.Locale;

public class GetTimeAgo {

    private static GetTimeAgo instance;

    private GetTimeAgo() {}

    public static synchronized GetTimeAgo getInstance() {
        if(instance == null){
            synchronized (GetTimeAgo.class){
                instance = new GetTimeAgo();
            }
        }
        return instance;
    }

    public String getMessageAgo(Context context, Long time){

        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String dateTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            dateTimeFormatString = "d MMMM yyyy";
        }else{
            dateTimeFormatString = "MMMM d, yyyy";
        }

        if (now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)) {
            return context.getString(R.string.today);
        } else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return context.getString(R.string.yesterday);
        } else {
            return DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }

    public String getLastSeenAgo(Context context, Long time){

        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String timeFormatString;
        String dateTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            timeFormatString = "HH:mm";
            dateTimeFormatString = "dd/MM/yy, HH:mm";
        }else{
            timeFormatString = "h:mm aa";
            dateTimeFormatString = "MM/dd/YY, HH:mm aa";
        }

        if(now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH))
            return context.getString(R.string.last_seen_today) + " " + DateFormat.format(timeFormatString, mTime);
        else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return context.getString(R.string.last_seen_yesterday) + " " + DateFormat.format(timeFormatString, mTime);
        } else {
            return context.getString(R.string.last_seen) + " " + DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }

    public String getChatTimeAgo(Context context, Long time){
        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String timeFormatString;
        String dateTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            timeFormatString = "HH:mm";
            dateTimeFormatString = "dd.MM.yy";
        }else{
            timeFormatString = "HH:mm";
            dateTimeFormatString = "MM/dd/yy";
        }
        if (now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)) {
            return DateFormat.format(timeFormatString, mTime).toString();
        } else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return context.getString(R.string.yesterday);
        } else {
            return DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }
}
