package com.ibrhmdurna.chatapp.util.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.findAll.MessageFindAll;
import com.ibrhmdurna.chatapp.settings.EditAccountActivity;
import com.ibrhmdurna.chatapp.start.RegisterFinishActivity;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.ibrhmdurna.chatapp.util.adapter.MessageAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileController {

    private static FileController instance;

    private FileController(){}

    public static synchronized FileController getInstance(){
        if(instance == null){
            synchronized (FileController.class){
                instance = new FileController();
            }
        }
        return instance;
    }

    public ArrayList<String> getFolderFile(String path){
        ArrayList<String> allFiles = new ArrayList<>();

        File file = new File(path);
        File[] folderAllFiles = file.listFiles();

        if(folderAllFiles != null){

            if(folderAllFiles.length > 1){
                Arrays.sort(folderAllFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.lastModified() > o2.lastModified() ? - 1 : 1;
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

    public boolean isEmptyFile(String path){
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

    public int getAllGalleryImageCount(Context context){
        String[] columns = (String[]) Arrays.asList(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID).toArray();
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        Cursor imageCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy + " DESC"
        );

        return imageCursor.getCount();
    }

    public ArrayList<String> getAllGalleryPhoto(Context context){
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

    public String getAllGallery(Context context){
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

    public String getAlbumLastPhoto(String path){
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

    public int getAlbumPhotoCount(String path){
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

    public void compressToPhoto(Activity context, Bitmap bitmap, boolean isRegister){
        new PhotoCompressAsyncTask(context, isRegister).execute(bitmap);
    }

    public void compressToImageMessage(Bitmap bitmap, String chatUid, String imageName){
        new ImageMessageCompressAsyncTask(chatUid, imageName).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, bitmap);
    }

    public void compressToDownloadImage(String url, String chatUid, String imageName, SpinKitView loadingBar, ImageView imageView){
        new DownloadMyImageCompressAsyncTask(chatUid, imageName, loadingBar, imageView).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
    }

    public void compressToDownloadAndSaveImage(String url, String chatUid, String imageName, SpinKitView loadingBar, ImageView imageView, MessageAdapter adapter){
        new DownloadImageCompressAsyncTask(chatUid, imageName, loadingBar, imageView, adapter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
    }

    private static class PhotoCompressAsyncTask extends AsyncTask<Bitmap, byte[], byte[]>{

        @SuppressLint("StaticFieldLeak")
        private Activity context;
        private AlertDialog loading;
        private boolean isRegister;

        private PhotoCompressAsyncTask(Activity context, boolean isRegister){
            this.context = context;
            this.isRegister = isRegister;
        }


        @Override
        protected synchronized byte[] doInBackground(Bitmap... bitmaps) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.PNG, 75, stream);
            byte[] bytes = stream.toByteArray();
            bitmaps[0].recycle();

            return bytes;
        }

        @Override
        protected synchronized void onPreExecute() {
            loading = DialogController.getInstance().dialogLoading(context, context.getString(R.string.compressing));
            loading.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(byte[] data) {
            loading.dismiss();

            ImageController.getInstance().setProfileImageBytes(data);

            if(isRegister){
                Intent editIntent = new Intent(context, RegisterFinishActivity.class);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(editIntent);
            }
            else {
                Intent editIntent = new Intent(context, EditAccountActivity.class);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(editIntent);
            }

            super.onPostExecute(data);
        }
    }

    private static class ImageMessageCompressAsyncTask extends AsyncTask<Bitmap, String, String>{

        private String imageName;
        private String chatUid;

        public ImageMessageCompressAsyncTask(String chatUid, String imageName){
            this.imageName = imageName;
            this.chatUid = chatUid;
        }

        @Override
        protected synchronized String doInBackground(Bitmap... bitmaps) {

            Date d = new Date();
            CharSequence s  = DateFormat.format("yyyyMMdd", d.getTime());
            String newImageName = "IMG_"+s+"_"+System.currentTimeMillis()+".jpg";

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 75, bytes);

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

            return file.getAbsolutePath();
        }

        @Override
        protected synchronized void onPostExecute(String s) {
            Firebase.getInstance().getDatabaseReference().child("Messages").child(FirebaseAuth.getInstance().getUid()).child(chatUid).child(imageName).child("path").setValue(s);

            super.onPostExecute(s);
        }
    }

    private static class ImageMessageSaveCompressAsyncTask extends AsyncTask<Bitmap, String, String>{

        private String imageName;
        private String chatUid;

        @SuppressLint("StaticFieldLeak")
        private SpinKitView loadingBar;
        @SuppressLint("StaticFieldLeak")
        private ImageView imageView;

        private MessageAdapter adapter;

        public ImageMessageSaveCompressAsyncTask(String chatUid, String imageName, SpinKitView loadingBar, ImageView imageView, MessageAdapter adapter){
            this.imageName = imageName;
            this.chatUid = chatUid;
            this.loadingBar = loadingBar;
            this.imageView = imageView;
            this.adapter = adapter;
        }

        @Override
        protected synchronized String doInBackground(Bitmap... bitmaps) {

            Date d = new Date();
            CharSequence s  = DateFormat.format("yyyyMMdd", d.getTime());
            String newImageName = "IMG_"+s+"_"+System.currentTimeMillis()+".jpg";

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 75, bytes);

            String ExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath()+"/DCIM/ChatApp";
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

            return fileOutputStream != null ? file.getAbsolutePath() : null;
        }

        @Override
        protected synchronized void onPostExecute(final String s) {

            final String newPath = s;

            if(newPath != null){
                Firebase.getInstance().getDatabaseReference().child("Messages").child(FirebaseAuth.getInstance().getUid()).child(chatUid).child(imageName).child("path").setValue(newPath).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Firebase.getInstance().getDatabaseReference().child("Messages").child(FirebaseAuth.getInstance().getUid()).child(chatUid).child(imageName).child("download").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        File imgFile = new File(newPath);

                                        if(imgFile.exists()){
                                            UniversalImageLoader.setImage(newPath, imageView, null, "file://");
                                            loadingBar.setVisibility(View.GONE);
                                            loadingBar.setIndeterminate(false);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }


            super.onPostExecute(s);
        }
    }

    private static class DownloadMyImageCompressAsyncTask extends AsyncTask<String, String, Bitmap>{

        private String imageName;
        private String chatUid;

        @SuppressLint("StaticFieldLeak")
        private SpinKitView loadingBar;
        @SuppressLint("StaticFieldLeak")
        private ImageView imageView;

        public DownloadMyImageCompressAsyncTask(String chatUid, String imageName, SpinKitView loadingBar, ImageView imageView){
            this.imageName = imageName;
            this.chatUid = chatUid;
            this.loadingBar = loadingBar;
            this.imageView = imageView;
        }

        @Override
        protected synchronized void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setIndeterminate(true);
            super.onPreExecute();
        }

        @Override
        protected synchronized Bitmap doInBackground(String... strings) {
            Bitmap bm = null;
            InputStream is = null;
            BufferedInputStream bis = null;
            try
            {
                URLConnection conn = new URL(strings[0]).openConnection();
                conn.connect();
                is = conn.getInputStream();
                bis = new BufferedInputStream(is, 8192);
                bm = BitmapFactory.decodeStream(bis);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (bis != null)
                {
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return bm;
        }

        @Override
        protected synchronized void onPostExecute(Bitmap bitmap) {

            loadingBar.setVisibility(View.GONE);
            loadingBar.setIndeterminate(false);
            imageView.setImageBitmap(bitmap);

            new ImageMessageCompressAsyncTask(chatUid, imageName).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, bitmap);

            super.onPostExecute(bitmap);
        }
    }

    private static class DownloadImageCompressAsyncTask extends AsyncTask<String, String, Bitmap>{

        private String imageName;
        private String chatUid;

        @SuppressLint("StaticFieldLeak")
        private SpinKitView loadingBar;
        @SuppressLint("StaticFieldLeak")
        private ImageView imageView;
        private MessageAdapter adapter;

        public DownloadImageCompressAsyncTask(String chatUid, String imageName, SpinKitView loadingBar, ImageView imageView, MessageAdapter adapter){
            this.imageName = imageName;
            this.chatUid = chatUid;
            this.loadingBar = loadingBar;
            this.imageView = imageView;
            this.adapter = adapter;
        }

        @Override
        protected synchronized void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setIndeterminate(true);
            super.onPreExecute();
        }

        @Override
        protected synchronized Bitmap doInBackground(String... strings) {
            Bitmap bm = null;
            InputStream is = null;
            BufferedInputStream bis = null;
            try
            {
                URLConnection conn = new URL(strings[0]).openConnection();
                conn.connect();
                is = conn.getInputStream();
                bis = new BufferedInputStream(is, 8192);
                bm = BitmapFactory.decodeStream(bis);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (bis != null)
                {
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return bm;
        }

        @Override
        protected synchronized void onPostExecute(Bitmap bitmap) {

            new ImageMessageSaveCompressAsyncTask(chatUid, imageName, loadingBar, imageView, adapter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, bitmap);

            super.onPostExecute(bitmap);
        }
    }
}
