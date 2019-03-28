package com.ibrhmdurna.chatapp.Image;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Utils.File;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.AlbumAdapter;
import com.ibrhmdurna.chatapp.Utils.GalleryAdapter;
import com.ibrhmdurna.chatapp.Utils.Environment;
import com.ibrhmdurna.chatapp.Utils.FileProcess;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewFactory extends AppCompatActivity implements ViewComponentFactory {

    private List<File> files;
    private RecyclerView galleryContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView subTitle, noPhotosView;

    private boolean isBack = true;

    private String isContext = "Share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        isContext = getIntent().getStringExtra("isContext");

        permissionProcess();
    }

    private void permissionProcess(){
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    toolsManagement();
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog();
                }
                else {
                    GalleryViewFactory.super.onBackPressed();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                GalleryViewFactory.super.onBackPressed();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private void buildGalleryPath(){
        files = new ArrayList<>();

        String root = android.os.Environment.getExternalStorageDirectory().getPath();
        String cameraPath = root + "/DCIM/Camera";
        String downloadPath = root + "/Download";
        String whatsAppPath = root + "/WhatsApp/Media/WhatsApp Images";
        String screenShotsPath = root + "/DCIM/Screenshots";
        String chatAppPath = root + "/DCIM/ChatApp/Sent";
        String twitterPath = root + "/Pictures/Twitter";
        String facebookPath = root + "/DCIM/Facebook";
        String instagramPath = root + "/Pictures/Instagram";
        String picsArtPath = root + "/Pictures/PicsArt";
        String snapchatPath = root + "/Snapchat";

        if(FileProcess.isEmptyFile(root)){
            files.add(new File("All Photos", root, FileProcess.getAllGallery(this), FileProcess.getAllGalleryImageCount(this)));
        }
        if(FileProcess.isEmptyFile(cameraPath)){
            files.add(new File("Camera", cameraPath, FileProcess.getAlbumLastPhoto(cameraPath), FileProcess.getAlbumPhotoCount(cameraPath)));
        }
        if(FileProcess.isEmptyFile(downloadPath)){
            files.add(new File("Download", downloadPath, FileProcess.getAlbumLastPhoto(downloadPath), FileProcess.getAlbumPhotoCount(downloadPath)));
        }
        if(FileProcess.isEmptyFile(screenShotsPath)){
            files.add(new File("ScreenShots", screenShotsPath, FileProcess.getAlbumLastPhoto(screenShotsPath), FileProcess.getAlbumPhotoCount(screenShotsPath)));
        }
        if(FileProcess.isEmptyFile(chatAppPath)){
            files.add(new File("ChatApp", chatAppPath, FileProcess.getAlbumLastPhoto(chatAppPath), FileProcess.getAlbumPhotoCount(chatAppPath)));
        }
        if(FileProcess.isEmptyFile(facebookPath)){
            files.add(new File("Facebook", facebookPath, FileProcess.getAlbumLastPhoto(facebookPath), FileProcess.getAlbumPhotoCount(facebookPath)));
        }
        if(FileProcess.isEmptyFile(instagramPath)){
            files.add(new File("Instagram", instagramPath, FileProcess.getAlbumLastPhoto(instagramPath), FileProcess.getAlbumPhotoCount(instagramPath)));
        }
        if(FileProcess.isEmptyFile(picsArtPath)){
            files.add(new File("PicsArt", picsArtPath, FileProcess.getAlbumLastPhoto(picsArtPath), FileProcess.getAlbumPhotoCount(picsArtPath)));
        }
        if(FileProcess.isEmptyFile(snapchatPath)){
            files.add(new File("Snapchat", snapchatPath, FileProcess.getAlbumLastPhoto(snapchatPath), FileProcess.getAlbumPhotoCount(snapchatPath)));
        }
        if(FileProcess.isEmptyFile(twitterPath)){
            files.add(new File("Twitter", twitterPath, FileProcess.getAlbumLastPhoto(twitterPath), FileProcess.getAlbumPhotoCount(twitterPath)));
        }
        if(FileProcess.isEmptyFile(whatsAppPath)){
            files.add(new File("WhatsApp Images", whatsAppPath, FileProcess.getAlbumLastPhoto(whatsAppPath), FileProcess.getAlbumPhotoCount(whatsAppPath)));
        }

        getGallery();
    }

    private void getGallery(){
        isBack = true;
        subTitle.setText(null);
        collapsingToolbarLayout.setTitle("Gallery");
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, files);
        galleryContainer = findViewById(R.id.gallery_container);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        galleryContainer.setLayoutManager(gridLayoutManager);

        galleryContainer.setHasFixedSize(true);
        galleryContainer.setAdapter(galleryAdapter);

        if(files.size() == 0){
            noPhotosView.setVisibility(View.VISIBLE);
        }

        galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(int position) {
                if(files.get(position).getTitle().equals("All Photos")){
                    getAlbumPhoto(FileProcess.getAllGalleryPhoto(getApplicationContext()));
                }
                else {
                    getAlbumPhoto(FileProcess.getFolderFile(files.get(position).getPath()));
                }
                collapsingToolbarLayout.setTitle(files.get(position).getTitle());
                if(files.get(position).getCount() > 1){
                    subTitle.setText(files.get(position).getCount() + " photos");
                }
                else {
                    subTitle.setText(files.get(position).getCount() + " photo");
                }
                isBack = false;
            }
        });
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer.removeAllViews();
        final AlbumAdapter albumAdapter = new AlbumAdapter(this, list, isContext);
        galleryContainer.setAdapter(albumAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        galleryContainer.setLayoutManager(gridLayoutManager);

        galleryContainer.setHasFixedSize(true);
        galleryContainer.setItemViewCacheSize(100);
        galleryContainer.setDrawingCacheEnabled(true);
        galleryContainer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    private void permissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText("You need to provide the necessary permissions to reach this section, please go to the settings and give the necessary permissions.");

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText("Cancel");
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GalleryViewFactory.super.onBackPressed();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText("Settings!");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                settingsIntent.setData(uri);
                startActivity(settingsIntent);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void onBack(){
        if(isBack)
            super.onBackPressed();
        else
            getGallery();
    }

    @Override
    public void buildView(){
        collapsingToolbarLayout = findViewById(R.id.gallery_collapsing_bar);
        subTitle = findViewById(R.id.gallery_subtitle);
        noPhotosView = findViewById(R.id.no_photos_view);
    }

    @Override
    public void toolsManagement(){
        Environment.toolbarProcess(this, R.id.gallery_toolbar);
        buildView();
        buildGalleryPath();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBack();
        return true;
    }

    @Override
    public void onBackPressed() {
        onBack();
    }
}
