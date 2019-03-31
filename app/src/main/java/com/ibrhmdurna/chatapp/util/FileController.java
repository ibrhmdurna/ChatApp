package com.ibrhmdurna.chatapp.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileController {

    public static void insertImage(Bitmap bitmap){
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyyMMdd", d.getTime());
        String newImageName = "IMG_"+s+"_"+System.currentTimeMillis()+".jpg";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

        String ExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath()+"/DCIM/ChatApp/Sent";
        File fileInfo = new File(ExternalStorageDirectory);
        File file = new File(ExternalStorageDirectory + File.separator, newImageName);

        FileOutputStream fileOutputStream = null;
        try {
            if(fileInfo.isDirectory() || fileInfo.mkdirs()){
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes.toByteArray());
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void insertProfileImage(Bitmap bitmap){
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyyMMdd", d.getTime());
        String newImageName = "IMG_"+s+"_"+System.currentTimeMillis()+".jpg";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

        String ExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath()+"/DCIM/ChatApp/Profile";
        File fileInfo = new File(ExternalStorageDirectory);
        File file = new File(ExternalStorageDirectory + File.separator, newImageName);

        FileOutputStream fileOutputStream = null;
        try {
            if(fileInfo.isDirectory() || fileInfo.mkdirs()){
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes.toByteArray());
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<String> getFolderFile(String path){
        ArrayList<String> allFiles = new ArrayList<>();

        File file = new File(path);
        File[] folderAllFiles = file.listFiles();

        if(folderAllFiles != null){

            if(folderAllFiles.length > 1){
                Arrays.sort(folderAllFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.lastModified() > o2.lastModified() ? -1 : 1;
                    }
                });
            }

            for (File folderAllFile : folderAllFiles) {
                if (folderAllFile.isFile()) {

                    String readFilePath = folderAllFile.getAbsolutePath();

                    int count = readFilePath.lastIndexOf(".");

                    if (count > 0) {
                        String fileType = readFilePath.substring(readFilePath.lastIndexOf("."));

                        if (fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals(".png")) {
                            allFiles.add(readFilePath);
                        }
                    }
                }
            }
        }

        return allFiles;
    }

    public static boolean isEmptyFile(String path){
        File file = new File(path);

        File[] fileList = file.listFiles();

        if(fileList != null){
            if(fileList.length > 1){
                Arrays.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.lastModified() > o2.lastModified() ? -1 : 1;
                    }
                });
            }

            for(File fileItem : fileList){
                String readItem = fileItem.getAbsolutePath();

                int count = readItem.lastIndexOf(".");

                if(count > 0){
                    String fileType = readItem.substring(readItem.lastIndexOf("."));
                    if (fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals(".png")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int getAllGalleryImageCount(Context context){
        String[] columns = (String[]) Arrays.asList(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID).toArray();
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        Cursor imageCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy + " DESC"
        );

        return imageCursor.getCount();
    }

    public static ArrayList<String> getAllGalleryPhoto(Context context){
        ArrayList<String> galleyImageUrls;
        String[] columns = (String[]) Arrays.asList(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID).toArray();
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        Cursor imageCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy + " DESC"
        );

        galleyImageUrls = new ArrayList<>();

        for(int i = 0; i < imageCursor.getCount(); i++){
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            galleyImageUrls.add((imageCursor.getString(dataColumnIndex)));
        }

        return galleyImageUrls;
    }

    public static String getAllGallery(Context context){
        String[] columns = (String[]) Arrays.asList(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID).toArray();
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        Cursor imageCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy + " DESC"
        );

        for(int i = 0; i < imageCursor.getCount(); i++){
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            return imageCursor.getString(dataColumnIndex);
        }

        return null;
    }

    public static String getAlbumLastPhoto(String path){
        File file = new File(path);
        File[] folderAllFiles = file.listFiles();

        if(folderAllFiles != null){
            if(folderAllFiles.length > 1){
                Arrays.sort(folderAllFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.lastModified() > o2.lastModified() ? -1 : 1;
                    }
                });
            }

            for(File folderAllFile : folderAllFiles){
                if(folderAllFile.isFile()){
                    String readImage = folderAllFile.getAbsolutePath();

                    int count = readImage.lastIndexOf(".");

                    if (count > 0) {
                        String fileType = readImage.substring(readImage.lastIndexOf("."));

                        if (fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals(".png")) {
                            return readImage;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static int getAlbumPhotoCount(String path){
        File file = new File(path);
        File[] folderAllFiles = file.listFiles();

        int countPhoto = 0;

        if(folderAllFiles != null){
            if(folderAllFiles.length > 1){
                Arrays.sort(folderAllFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.lastModified() > o2.lastModified() ? -1 : 1;
                    }
                });
            }

            for(File folderAllFile : folderAllFiles){
                if(folderAllFile.isFile()){
                    String readImage = folderAllFile.getAbsolutePath();

                    int count = readImage.lastIndexOf(".");

                    if (count > 0) {
                        String fileType = readImage.substring(readImage.lastIndexOf("."));

                        if (fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals(".png")) {
                            countPhoto++;
                        }
                    }
                }
            }
        }

        return countPhoto;
    }
}
