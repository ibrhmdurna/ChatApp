package com.ibrhmdurna.chatapp.util.controller;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageController {

    private static List<String> path;
    private static Bitmap image, cameraPath, cameraImage, cameraCroppedImage, backgroundImage;
    private static int backgroundColor = 0;
    private static byte[] profileImageBytes;

    public ImageController() {
    }

    public static byte[] getProfileImageBytes(){
        return profileImageBytes;
    }

    public static void setProfileImageBytes(byte[] profileImageBytes) {
        ImageController.profileImageBytes = profileImageBytes;
    }

    public static List<String> getPath() {
        if(path == null)
            path = new ArrayList<>();
        return path;
    }

    public static void setPath(List<String> path) {
        ImageController.path = path;
    }

    public static Bitmap getImage() {
        return image;
    }

    public static void setImage(Bitmap image) {
        ImageController.image = image;
    }

    public static Bitmap getCameraImage() {
        return cameraImage;
    }

    public static void setCameraImage(Bitmap cameraImage) {
        ImageController.cameraImage = cameraImage;
    }

    public static Bitmap getCameraCroppedImage() {
        return cameraCroppedImage;
    }

    public static void setCameraCroppedImage(Bitmap cameraCroppedImage) {
        ImageController.cameraCroppedImage = cameraCroppedImage;
    }

    public static Bitmap getCameraPath() {
        return cameraPath;
    }

    public static void setCameraPath(Bitmap cameraPath) {
        ImageController.cameraPath = cameraPath;
    }

    public static Bitmap getBackgroundImage() {
        return backgroundImage;
    }

    public static void setBackgroundImage(Bitmap backgroundImage) {
        ImageController.backgroundImage = backgroundImage;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(int backgroundColor) {
        ImageController.backgroundColor = backgroundColor;
    }
}
