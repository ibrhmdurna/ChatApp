package com.ibrhmdurna.chatapp.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.image.CameraActivity;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.dialog.ProfileBottomSheetDialog;

import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFinishActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, ProfileBottomSheetDialog.BottomSheetListener {

    private CircleImageView profileImage;
    private TextView profileText, bodyText;

    private int imageIndex;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);

        toolsManagement();
    }

    private void profileImageDeleteProcess(){
        ImageController.setProfileImageBytes(null);

        randomProfileImage();

        account.setProfile_image("default_" + imageIndex);
        account.setThumb_image("default_" + imageIndex);

        String name = account.getName().substring(0,1);
        profileText.setVisibility(View.VISIBLE);
        profileText.setText(name);

    }

    private void profileImageProcess(){

        byte[] bytes = ImageController.getProfileImageBytes();

        if(bytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
            profileText.setText(null);
            profileText.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void infoProcess(){
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        bodyText.setText("Nice to see you among us. Welcome\n"+name+" "+surname+"!");
        profileText.setText(name.substring(0,1));
    }

    private void randomProfileImage(){
        Random r = new Random();
        imageIndex = r.nextInt(10);
        switch (imageIndex){
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

    private void registerProcess(){
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String phone = getIntent().getStringExtra("phone");
        String birthday = getIntent().getStringExtra("birthday");
        int gender = getIntent().getIntExtra("gender", 0);
        int location = getIntent().getIntExtra("location", 0);
        String image = "default_" + imageIndex;
        Map<String, String> last_seen = ServerValue.TIMESTAMP;

        Bitmap bitmap = null;

        if(ImageController.getProfileImageBytes() != null){
            bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        }

        account = new Account(email, name, surname, phone, birthday, gender, location, image, image, false, null);
        Insert.getInstance().register(account, password, bitmap, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileImageProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }
    @Override
    public void buildView(){
        profileImage = findViewById(R.id.profile_image);
        profileText = findViewById(R.id.profile_text);
        bodyText = findViewById(R.id.reg_finish_body_text);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcess(this, R.id.register_finish_toolbar);
        buildView();
        randomProfileImage();
        infoProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reg_finish_back_btn:
                super.onBackPressed();
                break;
            case R.id.register_finish_btn:
                registerProcess();
                break;
            case R.id.change_profile_image:
                ProfileBottomSheetDialog profileBottomSheetDialog = new ProfileBottomSheetDialog(profileText.getVisibility() == View.VISIBLE);
                profileBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("isContext","Profile");
                galleryIntent.putExtra("isRegister", true);
                startActivity(galleryIntent);
                break;
            case "camera":
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtra("isContext","Profile");
                cameraIntent.putExtra("isRegister", true);
                startActivity(cameraIntent);
                break;
            case "delete":
                profileImageDeleteProcess();
                break;
        }
    }
}
