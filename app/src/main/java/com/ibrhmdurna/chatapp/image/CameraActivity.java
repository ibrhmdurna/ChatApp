package com.ibrhmdurna.chatapp.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.adapter.CameraAlbumAdapter;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.util.List;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ViewComponentFactory {

    private ImageView flashView;
    private com.otaliastudios.cameraview.CameraView cameraView;

    private String isContext = "Share";
    private boolean isRegister;

    private RecyclerView galleryContainer;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        uid = getIntent().getStringExtra("user_id");

        permissionProcess();
    }

    private void galleryProcess(){
        getAlbumPhoto(FileController.getInstance().getAllGalleryPhoto(getApplicationContext()));
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer = findViewById(R.id.camera_gallery_container);
        galleryContainer.removeAllViews();
        final CameraAlbumAdapter albumAdapter = new CameraAlbumAdapter(this, list, isContext, isRegister, uid);
        galleryContainer.setAdapter(albumAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryContainer.setLayoutManager(linearLayoutManager);

        galleryContainer.setHasFixedSize(true);
        galleryContainer.setItemViewCacheSize(100);
        galleryContainer.setDrawingCacheEnabled(true);
        galleryContainer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    private void takePhoto(){
        switch (cameraView.getFacing()){
            case BACK:
                cameraView.capturePicture();
                break;
            case FRONT:
                cameraView.captureSnapshot();
                break;
        }
    }

    private void rotateCameraProcess(){
        switch (cameraView.getFacing()){
            case BACK:
                cameraView.setFacing(Facing.FRONT);
                break;
            case FRONT:
                cameraView.setFacing(Facing.BACK);
                break;
        }
    }

    private void flashProcess(){
        switch (cameraView.getFlash()){
            case OFF:
                flashView.setImageDrawable(getDrawable(R.drawable.ic_flash_on_icon));
                cameraView.setFlash(Flash.ON);
                break;
            case ON:
                flashView.setImageDrawable(getDrawable(R.drawable.ic_flash_auto_icon));
                cameraView.setFlash(Flash.AUTO);
                break;
            case AUTO:
                flashView.setImageDrawable(getDrawable(R.drawable.ic_flash_off_icon));
                cameraView.setFlash(Flash.OFF);
                break;
        }
    }

    private void cameraProcess(){
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);

        isContext = getIntent().getStringExtra("isContext");
        isRegister = getIntent().getBooleanExtra("isRegister", false);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                ImageController.getInstance().getPath().clear();
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap image = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                ImageController.getInstance().setCameraImage(image);

                switch (isContext) {
                    case "Share":
                        Intent shareIntent = new Intent(getApplicationContext(), ShareActivity.class);
                        shareIntent.putExtra("user_id", uid);
                        startActivity(shareIntent);
                        overridePendingTransition(0, 0);
                        break;
                    case "Profile":
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileImageActivity.class);
                        profileIntent.putExtra("isRegister", isRegister);
                        startActivity(profileIntent);
                        overridePendingTransition(0, 0);
                        break;
                    case "Background":
                        Intent backgroundIntent = new Intent(getApplicationContext(), BackgroundActivity.class);
                        startActivity(backgroundIntent);
                        overridePendingTransition(0, 0);
                        break;
                }
            }
        });
    }

    private void permissionProcess(){
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
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
                    CameraActivity.super.onBackPressed();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                CameraActivity.super.onBackPressed();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
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
                CameraActivity.super.onBackPressed();
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

        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void buildView(){
        galleryContainer = findViewById(R.id.camera_gallery_container);
        cameraView = findViewById(R.id.camera_view);
        flashView = findViewById(R.id.camera_flash_view);
    }

    @Override
    public void toolsManagement(){
        buildView();
        cameraProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cameraView != null)
            cameraView.start();

        galleryProcess();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraView != null)
            cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraView != null) {
            cameraView.destroy();
        }
        ImageController.getInstance().setCameraImage(null);
        ImageController.getInstance().setCameraCroppedImage(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_flash_view:
                flashProcess();
                break;
            case R.id.camera_rotate_view:
                rotateCameraProcess();
                break;
            case R.id.camera_take_view:
                takePhoto();
                break;
            case R.id.camera_all_photo_item:
                Intent galleryIntent = new Intent(this, CameraGalleryActivity.class);
                galleryIntent.putExtra("user_id", uid);
                galleryIntent.putExtra("isContext", isContext);
                startActivity(galleryIntent);
                break;
        }
    }
}
