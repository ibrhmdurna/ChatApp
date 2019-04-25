package com.ibrhmdurna.chatapp.util.controller;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageController {

    private List<String> path;
    private Bitmap image, cameraImage, cameraCroppedImage, backgroundImage;
    private int backgroundColor = 0;
    private byte[] profileImageBytes;
    private String cameraPath;

    private static ImageController instance;

    public ImageController() {
    }

    public static ImageController getInstance(){
        if(instance == null){
            instance = new ImageController();
        }
        return instance;
    }

    public byte[] getProfileImageBytes(){
        return profileImageBytes;
    }

    public void setProfileImageBytes(byte[] profileImageBytes) {
        this.profileImageBytes = profileImageBytes;
    }

    public List<String> getPath() {
        if(path == null)
            path = new ArrayList<>();
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getCameraImage() {
        return cameraImage;
    }

    public void setCameraImage(Bitmap cameraImage) {
        this.cameraImage = cameraImage;
    }

    public Bitmap getCameraCroppedImage() {
        return cameraCroppedImage;
    }

    public void setCameraCroppedImage(Bitmap cameraCroppedImage) {
        this.cameraCroppedImage = cameraCroppedImage;
    }

    public String getCameraPath() {
        return cameraPath;
    }

    public void setCameraPath(String cameraPath) {
        this.cameraPath = cameraPath;
    }

    public Bitmap getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
