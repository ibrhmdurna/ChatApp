package com.ibrhmdurna.chatapp.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.local.SolidColorActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.dialog.BackgroundBottomSheetDialog;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.ImageController;

import java.io.ByteArrayOutputStream;

public class ChatSettingsActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, BackgroundBottomSheetDialog.BottomSheetListener {

    private SharedPreferences prefs;

    private ImageView backgroundView;
    private TextView saveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        toolsManagement();
    }

    private void deleteBackground(){

        prefs = getSharedPreferences("CHAT", MODE_PRIVATE);

        String backgroundImage = prefs.getString("BACKGROUND_IMAGE", null);
        int backgroundColor = prefs.getInt("BACKGROUND_COLOR", 0);

        if(backgroundImage == null && backgroundColor == 0){
            Toast.makeText(getApplicationContext(), "Background does not already exist!", Toast.LENGTH_SHORT).show();
        }
        else {
            ImageController.setBackgroundColor(0);
            ImageController.setBackgroundImage(null);
            saveView.setEnabled(false);
            saveView.setSaveEnabled(false);
            SharedPreferences prefs = getSharedPreferences("CHAT", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("BACKGROUND_COLOR", 0);
            editor.putString("BACKGROUND_IMAGE", null);
            editor.apply();

            backgroundView.setImageDrawable(getDrawable(R.drawable.splash_background));
            Toast.makeText(getApplicationContext(), "Deleted background!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBackground(){
        if(ImageController.getBackgroundImage() != null){

            Bitmap bm = ImageController.getBackgroundImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            String encoded = Base64.encodeToString(b, Base64.DEFAULT);

            SharedPreferences prefs = getSharedPreferences("CHAT", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("BACKGROUND_IMAGE", encoded);
            editor.putInt("BACKGROUND_COLOR", 0);
            editor.apply();

            backgroundView.setImageBitmap(bm);

        }
        if (ImageController.getBackgroundColor() != 0) {

            SharedPreferences prefs = getSharedPreferences("CHAT", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("BACKGROUND_IMAGE", null);
            editor.putInt("BACKGROUND_COLOR", ImageController.getBackgroundColor());
            editor.apply();

            backgroundView.setImageResource(ImageController.getBackgroundColor());
        }

        ImageController.setBackgroundColor(0);
        ImageController.setBackgroundImage(null);
        saveView.setEnabled(false);
        saveView.setSaveEnabled(false);
        Toast.makeText(getApplicationContext(), "Background successfully changed.", Toast.LENGTH_SHORT).show();
    }

    private void backgroundProcess(){

        if(ImageController.getBackgroundImage() != null){
            backgroundView.setImageBitmap(ImageController.getBackgroundImage());
            saveView.setEnabled(true);
            saveView.setSaveEnabled(true);
            return;
        }

        if(ImageController.getBackgroundColor() != 0){
            backgroundView.setImageResource(ImageController.getBackgroundColor());
            saveView.setEnabled(true);
            saveView.setSaveEnabled(true);
            return;
        }

        prefs = getSharedPreferences("CHAT", MODE_PRIVATE);

        String backgroundImage = prefs.getString("BACKGROUND_IMAGE", null);
        int backgroundColor = prefs.getInt("BACKGROUND_COLOR", 0);

        if(backgroundImage != null){
            byte[] imageAsBytes = Base64.decode(backgroundImage.getBytes(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            backgroundView.setImageBitmap(bitmap);
        }
        else if(backgroundColor != 0){
            backgroundView.setImageResource(backgroundColor);
        }
    }

    @Override
    public void buildView(){
        backgroundView = findViewById(R.id.chat_background_view);
        saveView = findViewById(R.id.chat_background_save_view);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.chat_settings_toolbar);
        buildView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        backgroundProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.background_change_view:
                BackgroundBottomSheetDialog backgroundBottomSheetDialog = new BackgroundBottomSheetDialog();
                backgroundBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
            case R.id.chat_background_save_view:
                saveBackground();
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("isContext", "Background");
                startActivity(galleryIntent);
                break;
            case "color":
                Intent solidIntent = new Intent(this, SolidColorActivity.class);
                startActivity(solidIntent);
                break;
            case "delete":
                deleteBackground();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageController.setBackgroundColor(0);
        ImageController.setBackgroundImage(null);
    }
}
