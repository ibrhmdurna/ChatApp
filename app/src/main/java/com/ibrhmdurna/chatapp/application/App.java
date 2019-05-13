package com.ibrhmdurna.chatapp.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        EmojiManager.install(new GoogleEmojiProvider());

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        //ImageLoader.getInstance().clearMemoryCache();
        //ImageLoader.getInstance().clearDiskCache();

        SharedPreferences prefs = getSharedPreferences("THEME", MODE_PRIVATE);
        if(prefs.getBoolean("NIGHT_MODE", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Theme.getInstance().setThemeColor(prefs.getInt("THEME_COLOR", R.color.colorAccent));

        SharedPreferences prefsR = getSharedPreferences("REMEMBER", MODE_PRIVATE);

        Remember.getInstance().setEmail(prefsR.getString("EMAIL", null));
        Remember.getInstance().setPassword(prefsR.getString("PASSWORD", null));
    }

    public static class Background{

        private static Background instance;

        private List<String> pageStackList;

        private Background(){}

        public static synchronized Background getInstance() {
            if(instance == null){
                synchronized (Background.class){
                    instance = new Background();
                }
            }
            return instance;
        }

        public List<String> getPageStackList(){
            if(pageStackList == null){
                pageStackList = new ArrayList<>();
            }
            return pageStackList;
        }

        public void clearAllPage(){
            getPageStackList().clear();
        }

        public int pageStackChildCount(){
            return getPageStackList().size();
        }

        public void addPage(String tag){
            getPageStackList().add(tag);
        }

        public void removePage(){
            getPageStackList().remove(pageStackList.size() - 1);
            getPageStackList().remove(pageStackList.size() - 1);
        }

        public void clearThisPage(String tag){
            for(int i = 0; i < getPageStackList().size(); i++){
                if(getPageStackList().get(i).equals(tag)){
                    getPageStackList().remove(i);
                    break;
                }
            }
        }
    }

    public static class Theme{
        private static int THEME_COLOR;

        private static Theme instance;

        private Theme(){}

        public static synchronized Theme getInstance(){
            if(instance == null){
                synchronized (Theme.class){
                    instance = new Theme();
                }
            }
            return instance;
        }

        public int getThemeColor() {
            return THEME_COLOR;
        }

        public void setThemeColor(int themeColor) {
            THEME_COLOR = themeColor;
        }

        public void getTheme(Context context){
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                switch (getThemeColor()){
                    case R.color.colorAccent:
                        context.setTheme(R.style.AppTheme_DarkTheme);
                        break;
                    case R.color.colorOfficialBlue:
                        context.setTheme(R.style.AppTheme_DarkTheme_OfficialBlue);
                        break;
                    case R.color.colorBlue:
                        context.setTheme(R.style.AppTheme_DarkTheme_OfficialBlue);
                        break;
                    case R.color.colorIndigo:
                        context.setTheme(R.style.AppTheme_DarkTheme_Indigo);
                        break;
                    case R.color.colorDeepPurple:
                        context.setTheme(R.style.AppTheme_DarkTheme_DeepPurple);
                        break;
                    case R.color.colorPurple:
                        context.setTheme(R.style.AppTheme_DarkTheme_Purple);
                        break;
                    case R.color.colorLightPurple:
                        context.setTheme(R.style.AppTheme_DarkTheme_LightPurple);
                        break;
                    case R.color.colorPink:
                        context.setTheme(R.style.AppTheme_DarkTheme_Pink);
                        break;
                    case R.color.colorRed:
                        context.setTheme(R.style.AppTheme_DarkTheme_Red);
                        break;
                    case R.color.colorDeepOrange:
                        context.setTheme(R.style.AppTheme_DarkTheme_DeepOrange);
                        break;
                    case R.color.colorExtraOrange:
                        context.setTheme(R.style.AppTheme_DarkTheme_ExtraOrange);
                        break;
                    case R.color.colorOrange:
                        context.setTheme(R.style.AppTheme_DarkTheme_Orange);
                        break;
                    case R.color.colorTeal:
                        context.setTheme(R.style.AppTheme_DarkTheme_Teal);
                        break;
                    case R.color.colorCyan:
                        context.setTheme(R.style.AppTheme_DarkTheme_Cyan);
                        break;
                    case R.color.colorLightGreen:
                        context.setTheme(R.style.AppTheme_DarkTheme_LightGreen);
                        break;
                    case R.color.colorOfficialGreen:
                        context.setTheme(R.style.AppTheme_DarkTheme_OfficialGreen);
                        break;
                    case R.color.colorGreen:
                        context.setTheme(R.style.AppTheme_DarkTheme_Green);
                        break;
                }
            }
            else {
                switch (getThemeColor()){
                    case R.color.colorAccent:
                        context.setTheme(R.style.AppTheme_LightTheme);
                        break;
                    case R.color.colorOfficialBlue:
                        context.setTheme(R.style.AppTheme_LightTheme_OfficialBlue);
                        break;
                    case R.color.colorBlue:
                        context.setTheme(R.style.AppTheme_LightTheme_OfficialBlue);
                        break;
                    case R.color.colorIndigo:
                        context.setTheme(R.style.AppTheme_LightTheme_Indigo);
                        break;
                    case R.color.colorDeepPurple:
                        context.setTheme(R.style.AppTheme_LightTheme_DeepPurple);
                        break;
                    case R.color.colorPurple:
                        context.setTheme(R.style.AppTheme_LightTheme_Purple);
                        break;
                    case R.color.colorLightPurple:
                        context.setTheme(R.style.AppTheme_LightTheme_LightPurple);
                        break;
                    case R.color.colorPink:
                        context.setTheme(R.style.AppTheme_LightTheme_Pink);
                        break;
                    case R.color.colorRed:
                        context.setTheme(R.style.AppTheme_LightTheme_Red);
                        break;
                    case R.color.colorDeepOrange:
                        context.setTheme(R.style.AppTheme_LightTheme_DeepOrange);
                        break;
                    case R.color.colorExtraOrange:
                        context.setTheme(R.style.AppTheme_LightTheme_ExtraOrange);
                        break;
                    case R.color.colorOrange:
                        context.setTheme(R.style.AppTheme_LightTheme_Orange);
                        break;
                    case R.color.colorTeal:
                        context.setTheme(R.style.AppTheme_LightTheme_Teal);
                        break;
                    case R.color.colorCyan:
                        context.setTheme(R.style.AppTheme_LightTheme_Cyan);
                        break;
                    case R.color.colorLightGreen:
                        context.setTheme(R.style.AppTheme_LightTheme_LightGreen);
                        break;
                    case R.color.colorOfficialGreen:
                        context.setTheme(R.style.AppTheme_LightTheme_OfficialGreen);
                        break;
                    case R.color.colorGreen:
                        context.setTheme(R.style.AppTheme_LightTheme_Green);
                        break;
                }
            }
        }

        public void getTransparentTheme(Context context){
            switch (getThemeColor()){
                case R.color.colorAccent:
                    context.setTheme(R.style.AppTheme_DarkTheme_Transparent);
                    break;
                case R.color.colorOfficialBlue:
                    context.setTheme(R.style.AppTheme_DarkTheme_OfficialBlue_Transparent);
                    break;
                case R.color.colorBlue:
                    context.setTheme(R.style.AppTheme_DarkTheme_OfficialBlue_Transparent);
                    break;
                case R.color.colorIndigo:
                    context.setTheme(R.style.AppTheme_DarkTheme_Indigo_Transparent);
                    break;
                case R.color.colorDeepPurple:
                    context.setTheme(R.style.AppTheme_DarkTheme_DeepPurple_Transparent);
                    break;
                case R.color.colorPurple:
                    context.setTheme(R.style.AppTheme_DarkTheme_Purple_Transparent);
                    break;
                case R.color.colorLightPurple:
                    context.setTheme(R.style.AppTheme_DarkTheme_LightPurple_Transparent);
                    break;
                case R.color.colorPink:
                    context.setTheme(R.style.AppTheme_DarkTheme_Pink_Transparent);
                    break;
                case R.color.colorRed:
                    context.setTheme(R.style.AppTheme_DarkTheme_Red_Transparent);
                    break;
                case R.color.colorDeepOrange:
                    context.setTheme(R.style.AppTheme_DarkTheme_DeepOrange_Transparent);
                    break;
                case R.color.colorExtraOrange:
                    context.setTheme(R.style.AppTheme_DarkTheme_ExtraOrange_Transparent);
                    break;
                case R.color.colorOrange:
                    context.setTheme(R.style.AppTheme_DarkTheme_Orange_Transparent);
                    break;
                case R.color.colorTeal:
                    context.setTheme(R.style.AppTheme_DarkTheme_Teal_Transparent);
                    break;
                case R.color.colorCyan:
                    context.setTheme(R.style.AppTheme_DarkTheme_Cyan_Transparent);
                    break;
                case R.color.colorLightGreen:
                    context.setTheme(R.style.AppTheme_DarkTheme_LightGreen_Transparent);
                    break;
                case R.color.colorOfficialGreen:
                    context.setTheme(R.style.AppTheme_DarkTheme_OfficialGreen_Transparent);
                    break;
                case R.color.colorGreen:
                    context.setTheme(R.style.AppTheme_DarkTheme_Green_Transparent);
                    break;
            }
        }
    }

    public static class Remember{

        private static Remember instance;

        private String email;
        private String password;

        private Remember(){}

        public static synchronized Remember getInstance(){
            if(instance == null){
                synchronized (Remember.class){
                    instance = new Remember();
                }
            }
            return instance;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
