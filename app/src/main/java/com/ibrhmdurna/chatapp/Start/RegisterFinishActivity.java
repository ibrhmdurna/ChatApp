package com.ibrhmdurna.chatapp.Start;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;
import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.Database.Insert;
import com.ibrhmdurna.chatapp.Models.Account;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Util.Environment;

import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFinishActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener{

    private CircleImageView profileImage;
    private TextView profileText, bodyText;

    private int imageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);

        toolsManagement();
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
        String gender = getIntent().getStringExtra("gender");
        String location = getIntent().getStringExtra("location");
        String image = "default_"+imageIndex;
        Map<String, String> last_seen = ServerValue.TIMESTAMP;

        Account account = new Account(email, name, surname, phone, birthday, gender, location, image, image, false, last_seen);
        Insert.getInstance().register(account, password, this);
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
        }
    }
}
