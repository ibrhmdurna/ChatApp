package com.ibrhmdurna.chatapp.image;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.vanniktech.emoji.EmojiTextView;

import java.util.Objects;

public class PhotoActivity extends AppCompatActivity implements ViewComponentFactory {

    private TextView nameSurnameText;
    private TextView timeText;
    private PhotoView photoView;
    private EmojiTextView contentText;

    private String uid;
    private long time;
    private String path;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        uid = getIntent().getStringExtra("user_id");
        time = getIntent().getLongExtra("time", 0);
        path = getIntent().getStringExtra("path");
        content = getIntent().getStringExtra("content");

        toolsManagement();
    }

    private void getContent(){
        if(uid != null){

            String currentUid = FirebaseAuth.getInstance().getUid();

            if(currentUid != null){
                Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            nameSurnameText.setText(getString(R.string.chatapp_user));
                        }
                        else{
                            Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Account account = dataSnapshot.getValue(Account.class);

                                        if (account != null){
                                            nameSurnameText.setText(account.getNameSurname());
                                        }
                                    }
                                    else{
                                        nameSurnameText.setText(getString(R.string.chatapp_user));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        /*
        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if(photoView.getScale() > 1){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                else{

                }
            }
        });
        */

        if(content.equals("")){
            contentText.setVisibility(View.GONE);
        }
        else{
            contentText.setVisibility(View.VISIBLE);
            contentText.setText(content);
        }

        UniversalImageLoader.setImage(path, photoView, null, "file://");

        timeText.setText(GetTimeAgo.getInstance().getPhotoTimeAgo(this, time));
    }

    private void toolbarProcess(){
        Toolbar toolbar = findViewById(R.id.photo_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon_2);
    }

    @Override
    public void toolsManagement() {
        toolbarProcess();
        buildView();
        getContent();
    }

    @Override
    public void buildView() {
        nameSurnameText = findViewById(R.id.photo_name_surname);
        timeText = findViewById(R.id.photo_time);
        photoView = findViewById(R.id.photo_view);
        contentText = findViewById(R.id.message_content);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
    }
}
