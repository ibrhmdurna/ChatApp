package com.ibrhmdurna.chatapp.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v7.app.AppCompatDelegate;

import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        EmojiManager.install(new GoogleEmojiProvider());

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        prefs = getSharedPreferences("THEME",MODE_PRIVATE);
        if(prefs.getBoolean("NIGHT_MODE", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Theme.setThemeColor(prefs.getInt("THEME_COLOR", R.color.colorAccent));
    }

    public static class Background{
        private static List<String> pageStackList = new ArrayList<>();

        public static List<String> getPageStackList(){
            return pageStackList;
        }

        public static int pageStackChildCount(){
            return pageStackList.size();
        }

        public static void addPage(String tag){
            pageStackList.add(tag);
        }

        public static void removePage(){
            pageStackList.remove(pageStackList.size() - 1);
            pageStackList.remove(pageStackList.size() - 1);
        }
    }

    public static class Theme{
        private static int THEME_COLOR;

        public static int getThemeColor() {
            return THEME_COLOR;
        }

        public static void setThemeColor(int themeColor) {
            THEME_COLOR = themeColor;
        }

        public static void getTheme(Context context){
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
                        context.setTheme(R.style.AppTheme_DarkTheme_Green);
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
                        context.setTheme(R.style.AppTheme_LightTheme_Green);
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

        public static void getTransparentTheme(Context context){
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
                    context.setTheme(R.style.AppTheme_DarkTheme_Green_Transparent);
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
}
