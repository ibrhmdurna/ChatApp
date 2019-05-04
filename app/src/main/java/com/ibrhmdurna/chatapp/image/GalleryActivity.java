package com.ibrhmdurna.chatapp.image;

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

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.models.File;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.adapter.AlbumAdapter;
import com.ibrhmdurna.chatapp.util.adapter.GalleryAdapter;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ViewComponentFactory {

    private List<File> files;
    private RecyclerView galleryContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView subTitle, noPhotosView;

    private boolean isBack = true;

    private String isContext = "Share";
    private boolean isRegister;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        isContext = getIntent().getStringExtra("isContext");
        isRegister = getIntent().getBooleanExtra("isRegister", false);
        uid = getIntent().getStringExtra("user_id");

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
                    GalleryActivity.super.onBackPressed();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                GalleryActivity.super.onBackPressed();
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
        String chatAppPath = root + "/DCIM/ChatApp";
        String twitterPath = root + "/Pictures/Twitter";
        String facebookPath = root + "/DCIM/Facebook";
        String instagramPath = root + "/Pictures/Instagram";
        String picsArtPath = root + "/Pictures/PicsArt";
        String snapchatPath = root + "/Snapchat";
        String zedgePath = root + "/zedge/wallpaper";

        if(FileController.getInstance().isEmptyFile(root)){
            files.add(new File(getString(R.string.all_photos), root, FileController.getInstance().getAllGallery(this), FileController.getInstance().getAllGalleryImageCount(this)));
        }
        if(FileController.getInstance().isEmptyFile(cameraPath)){
            files.add(new File(getString(R.string.camera), cameraPath, FileController.getInstance().getAlbumLastPhoto(cameraPath), FileController.getInstance().getAlbumPhotoCount(cameraPath)));
        }
        if(FileController.getInstance().isEmptyFile(downloadPath)){
            files.add(new File(getString(R.string.download), downloadPath, FileController.getInstance().getAlbumLastPhoto(downloadPath), FileController.getInstance().getAlbumPhotoCount(downloadPath)));
        }
        if(FileController.getInstance().isEmptyFile(screenShotsPath)){
            files.add(new File(getString(R.string.screen_shots), screenShotsPath, FileController.getInstance().getAlbumLastPhoto(screenShotsPath), FileController.getInstance().getAlbumPhotoCount(screenShotsPath)));
        }
        if(FileController.getInstance().isEmptyFile(chatAppPath)){
            files.add(new File("ChatApp", chatAppPath, FileController.getInstance().getAlbumLastPhoto(chatAppPath), FileController.getInstance().getAlbumPhotoCount(chatAppPath)));
        }
        if(FileController.getInstance().isEmptyFile(facebookPath)){
            files.add(new File("Facebook", facebookPath, FileController.getInstance().getAlbumLastPhoto(facebookPath), FileController.getInstance().getAlbumPhotoCount(facebookPath)));
        }
        if(FileController.getInstance().isEmptyFile(instagramPath)){
            files.add(new File("Instagram", instagramPath, FileController.getInstance().getAlbumLastPhoto(instagramPath), FileController.getInstance().getAlbumPhotoCount(instagramPath)));
        }
        if(FileController.getInstance().isEmptyFile(picsArtPath)){
            files.add(new File("PicsArt", picsArtPath, FileController.getInstance().getAlbumLastPhoto(picsArtPath), FileController.getInstance().getAlbumPhotoCount(picsArtPath)));
        }
        if(FileController.getInstance().isEmptyFile(snapchatPath)){
            files.add(new File("Snapchat", snapchatPath, FileController.getInstance().getAlbumLastPhoto(snapchatPath), FileController.getInstance().getAlbumPhotoCount(snapchatPath)));
        }
        if(FileController.getInstance().isEmptyFile(twitterPath)){
            files.add(new File("Twitter", twitterPath, FileController.getInstance().getAlbumLastPhoto(twitterPath), FileController.getInstance().getAlbumPhotoCount(twitterPath)));
        }
        if(FileController.getInstance().isEmptyFile(whatsAppPath)){
            files.add(new File("WhatsApp Images", whatsAppPath, FileController.getInstance().getAlbumLastPhoto(whatsAppPath), FileController.getInstance().getAlbumPhotoCount(whatsAppPath)));
        }
        if(FileController.getInstance().isEmptyFile(zedgePath)){
            files.add(new File("Zedge", zedgePath, FileController.getInstance().getAlbumLastPhoto(zedgePath), FileController.getInstance().getAlbumPhotoCount(zedgePath)));
        }

        getGallery();
    }

    private void getGallery(){
        isBack = true;
        subTitle.setText(null);
        collapsingToolbarLayout.setTitle(getString(R.string.gallery));
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
                if(files.get(position).getTitle().equals(getString(R.string.all_photos))){
                    getAlbumPhoto(FileController.getInstance().getAllGalleryPhoto(getApplicationContext()));
                }
                else {
                    getAlbumPhoto(FileController.getInstance().getFolderFile(files.get(position).getPath()));
                }
                collapsingToolbarLayout.setTitle(files.get(position).getTitle());
                if(files.get(position).getCount() > 1){
                    subTitle.setText(files.get(position).getCount() + " " + getString(R.string.photos));
                }
                else {
                    subTitle.setText(files.get(position).getCount() + " " + getString(R.string.photo));
                }
                isBack = false;
            }
        });
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer.removeAllViews();
        final AlbumAdapter albumAdapter = new AlbumAdapter(this, list, isContext, isRegister, uid);
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
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText(getString(R.string.permission_content));

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText(getString(R.string.cancel));
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GalleryActivity.super.onBackPressed();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText(getString(R.string._settings));
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
        Environment.getInstance().toolbarProcess(this, R.id.gallery_toolbar);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }
}
