package com.ibrhmdurna.chatapp.Start;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Image.CameraActivity;
import com.ibrhmdurna.chatapp.Image.GalleryActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;
import com.ibrhmdurna.chatapp.Utils.ProfileBottomSheetDialog;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFinishActivity extends AppCompatActivity implements View.OnClickListener, ProfileBottomSheetDialog.BottomSheetListener {

    private CircleImageView profileImage;
    private TextView profileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);

        toolManagement();
        randomProfileImage();
    }

    private void randomProfileImage(){
        Random r = new Random();
        int index = r.nextInt(10);
        switch (index){
            case 0:
                profileImage.setImageResource(R.drawable.ic_avatar_0);
                break;
            case 1:
                profileImage.setImageResource(R.drawable.ic_avatar_1);
                break;
            case 2:
                profileImage.setImageResource(R.drawable.ic_avatar_2);
                break;
            case 3:
                profileImage.setImageResource(R.drawable.ic_avatar_3);
                break;
            case 4:
                profileImage.setImageResource(R.drawable.ic_avatar_4);
                break;
            case 5:
                profileImage.setImageResource(R.drawable.ic_avatar_5);
                break;
            case 6:
                profileImage.setImageResource(R.drawable.ic_avatar_6);
                break;
            case 7:
                profileImage.setImageResource(R.drawable.ic_avatar_7);
                break;
            case 8:
                profileImage.setImageResource(R.drawable.ic_avatar_8);
                break;
            case 9:
                profileImage.setImageResource(R.drawable.ic_avatar_9);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    private void buildView(){
        profileImage = findViewById(R.id.profile_image);
        profileText = findViewById(R.id.profile_text);
    }

    private void toolManagement(){
        Environment.toolbarProcess(this, R.id.register_finish_toolbar);
        buildView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_profile_image_view:
                ProfileBottomSheetDialog profileBottomSheetDialog = new ProfileBottomSheetDialog(profileText.getVisibility() == View.VISIBLE);
                profileBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
            break;
            case R.id.reg_finish_back_btn:
                super.onBackPressed();
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("isContext","Profile");
                startActivity(galleryIntent);
                break;
            case "camera":
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtra("isContext","Profile");
                startActivity(cameraIntent);
                break;
        }
    }
}
