package com.ibrhmdurna.chatapp.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Main.MainActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class ThemesActivity extends AppCompatActivity implements View.OnClickListener {

    private static int THEME_COLOR;

    private List<RelativeLayout> selectedList;

    private SwitchButton darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        toolsManagement();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.Theme.getTheme(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    private void checkThemeColor(){
        selectedListAllVisibilityGone();
        switch (App.Theme.getThemeColor()){
            case R.color.colorAccent:
                findViewById(R.id.color_accent_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorAccent;
                break;
            case R.color.colorOfficialBlue:
                findViewById(R.id.color_official_blue_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOfficialBlue;
                break;
            case R.color.colorBlue:
                findViewById(R.id.color_blue_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorBlue;
                break;
            case R.color.colorIndigo:
                findViewById(R.id.color_indigo_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorIndigo;
                break;
            case R.color.colorDeepPurple:
                findViewById(R.id.color_deep_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorDeepPurple;
                break;
            case R.color.colorPurple:
                findViewById(R.id.color_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorPurple;
                break;
            case R.color.colorLightPurple:
                findViewById(R.id.color_light_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorLightPurple;
                break;
            case R.color.colorPink:
                findViewById(R.id.color_pink_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorPink;
                break;
            case R.color.colorRed:
                findViewById(R.id.color_red_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorRed;
                break;
            case R.color.colorDeepOrange:
                findViewById(R.id.color_deep_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorDeepOrange;
                break;
            case R.color.colorExtraOrange:
                findViewById(R.id.color_extra_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorExtraOrange;
                break;
            case R.color.colorOrange:
                findViewById(R.id.color_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOrange;
                break;
            case R.color.colorTeal:
                findViewById(R.id.color_teal_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorTeal;
                break;
            case R.color.colorCyan:
                findViewById(R.id.color_cyan_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorCyan;
                break;
            case R.color.colorLightGreen:
                findViewById(R.id.color_light_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorLightGreen;
                break;
            case R.color.colorOfficialGreen:
                findViewById(R.id.color_official_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOfficialGreen;
                break;
            case R.color.colorGreen:
                findViewById(R.id.color_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorGreen;
                break;
        }
    }

    private void restart(){
        finish();
        App.Background.getPageStackList().clear();
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void checkTheme(){
        darkModeSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences prefs = getSharedPreferences("THEME", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("NIGHT_MODE", isChecked);
                editor.apply();
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                restart();
            }
        });
    }

    private void selectedListView(){
        selectedList = new ArrayList<>();
        selectedList.add((RelativeLayout) findViewById(R.id.color_accent_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_official_blue_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_blue_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_indigo_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_deep_purple_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_purple_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_light_purple_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_pink_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_red_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_deep_orange_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_extra_orange_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_orange_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_teal_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_cyan_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_light_green_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_official_green_selected));
        selectedList.add((RelativeLayout) findViewById(R.id.color_green_selected));
    }

    private void selectedListAllVisibilityGone(){
        for (int i = 0; i < selectedList.size(); i++){
            selectedList.get(i).setVisibility(View.GONE);
        }
    }

    private void buildView(){
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
    }

    private void toolsManagement(){
        Environment.toolbarProcess(this, R.id.themes_toolbar);
        buildView();
        selectedListView();
        checkTheme();
        checkThemeColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.apply_btn:
                App.Theme.setThemeColor(THEME_COLOR);
                SharedPreferences prefs = getSharedPreferences("THEME", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("THEME_COLOR", THEME_COLOR);
                editor.apply();
                restart();
                break;
            case R.id.color_accent_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_accent_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorAccent;
                break;
            case R.id.color_official_blue_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_official_blue_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOfficialBlue;
                break;
            case R.id.color_blue_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_blue_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorBlue;
                break;
            case R.id.color_indigo_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_indigo_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorIndigo;
                break;
            case R.id.color_deep_purple_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_deep_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorDeepPurple;
                break;
            case R.id.color_purple_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorPurple;
                break;
            case R.id.color_light_purple_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_light_purple_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorLightPurple;
                break;
            case R.id.color_pink_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_pink_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorPink;
                break;
            case R.id.color_red_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_red_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorRed;
                break;
            case R.id.color_deep_orange_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_deep_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorDeepOrange;
                break;
            case R.id.color_extra_orange_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_extra_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorExtraOrange;
                break;
            case R.id.color_orange_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_orange_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOrange;
                break;
            case R.id.color_teal_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_teal_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorTeal;
                break;
            case R.id.color_cyan_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_cyan_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorCyan;
                break;
            case R.id.color_light_green_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_light_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorLightGreen;
                break;
            case R.id.color_official_green_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_official_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorOfficialGreen;
                break;
            case R.id.color_green_layout:
                selectedListAllVisibilityGone();
                findViewById(R.id.color_green_selected).setVisibility(View.VISIBLE);
                THEME_COLOR = R.color.colorGreen;
                break;
        }
    }
}
