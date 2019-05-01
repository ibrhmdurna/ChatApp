package com.ibrhmdurna.chatapp.util;

import android.text.format.DateFormat;

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

    public String getMessageAgo(Long time){

        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String timeFormatString;
        String dateTimeFormatString;
        String yearTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            timeFormatString = "HH:mm";
            dateTimeFormatString = "d MMMM HH:mm";
            yearTimeFormatString = "dd/MM/yy HH:mm";
        }else{
            timeFormatString = "h:mm aa";
            dateTimeFormatString = "d MMMM h:mm aa";
            yearTimeFormatString = "dd/MM/yy h:mm aa";
        }

        if(now.get(Calendar.YEAR) != mTime.get(Calendar.YEAR)){
            return DateFormat.format(yearTimeFormatString, mTime).toString();
        } else if (now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)) {
            return "Today " + DateFormat.format(timeFormatString, mTime);
        } else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return "Yesterday " + DateFormat.format(timeFormatString, mTime);
        } else {
            return DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }

    public String getLastSeenAgo(Long time){

        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String timeFormatString;
        String dateTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            timeFormatString = "HH:mm";
            dateTimeFormatString = "dd/MM/yy HH:mm";
        }else{
            timeFormatString = "h:mm aa";
            dateTimeFormatString = "MM/dd/YY HH:mm aa";
        }

        if(now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH))
            return "Last seen today " + DateFormat.format(timeFormatString, mTime);
        else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return "Last seen yesterday " + DateFormat.format(timeFormatString, mTime);
        } else {
            return "Last seen " + DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }

    public String getChatTimeAgo(Long time){
        Calendar mTime = Calendar.getInstance();
        mTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        String timeFormatString;
        String dateTimeFormatString;

        String language = Locale.getDefault().getLanguage();
        if(language.equals("tr")) {
            timeFormatString = "HH:mm";
            dateTimeFormatString = "dd/MM/yy";
        }else{
            timeFormatString = "h:mm aa";
            dateTimeFormatString = "dd/MM/yy";
        }
        if (now.get(Calendar.DATE) == mTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)) {
            return DateFormat.format(timeFormatString, mTime).toString();
        } else if (now.get(Calendar.DATE) - mTime.get(Calendar.DATE) == 1
                && now.get(Calendar.MONTH) == mTime.get(Calendar.MONTH)){
            return "Yesterday";
        } else {
            return DateFormat.format(dateTimeFormatString, mTime).toString();
        }
    }
}
