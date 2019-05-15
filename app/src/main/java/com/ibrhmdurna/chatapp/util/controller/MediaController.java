package com.ibrhmdurna.chatapp.util.controller;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MediaController {

    private MediaController() {}

    public static class Recorder{

        private MediaRecorder mediaRecorder;

        private long startHTime = 0L;
        private Handler customHandler = new Handler();
        private long timeInMilliseconds = 0L;
        private long timeSwapBuff = 0L;
        private long updatedTime = 0L;

        private TextView durationText;

        private static Recorder instance;

        private Recorder(){}

        public static synchronized Recorder getInstance() {
            if(instance == null){
                synchronized (Recorder.class){
                    instance = new Recorder();
                }
            }
            return instance;
        }

        private MediaRecorder getMediaRecorder() {
            if(mediaRecorder == null){
                mediaRecorder = new MediaRecorder();
            }
            return mediaRecorder;
        }

        public void releaseMediaRecorder(){
            if(mediaRecorder != null){
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }

        public void setRecorderOutputFile(String path){
            if(mediaRecorder != null){
                mediaRecorder.setOutputFile(path);
            }
        }

        public void clearRecord(boolean clear, String path){

            startHTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;

            releaseMediaRecorder();

            if(clear && path != null){
                File f = new File(path);
                f.delete();
            }
        }

        public void stopRecord(){
            mediaRecorder.stop();
            mediaRecorder.release();
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            customHandler.removeCallbacks(updateTimerThread);
        }

        public void startRecord(TextView duration, String path) throws Exception {
            this.getMediaRecorder();
            this.setRecorderOutputFile(path);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(64000);
            mediaRecorder.setAudioSamplingRate(16000);

            durationText = duration;

            mediaRecorder.prepare();
            mediaRecorder.start();
            durationText.setText("00:00");
            startHTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        }

        private Runnable updateTimerThread = new Runnable() {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void run() {
                updateRun(this);
            }

        };

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        private void updateRun(Runnable runnable){
            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            if (durationText != null)
                durationText.setText("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs));



            customHandler.postDelayed(runnable, 100);
        }
    }

    public static class Player{

        private static Player instance;

        private long startHTime = 0L;
        private Handler customHandler = new Handler();
        private long timeInMilliseconds = 0L;
        private long timeSwapBuff = 0L;
        private long updatedTime = 0L;

        private String path;

        private TextView durationText;
        private SeekBar lineBar;
        private ImageButton playItem;
        private ImageButton pauseItem;

        private Player(){}

        public static synchronized Player getInstance() {
            if(instance == null){
                synchronized (Player.class){
                    instance = new Player();
                }
            }
            return instance;
        }

        private MediaPlayer mediaPlayer;

        public MediaPlayer getMediaPlayer() {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            return mediaPlayer;
        }

        public void setComponent(TextView duration, SeekBar lineBar, ImageButton playItem, ImageButton pauseItem, String path){
            durationText = duration;
            this.lineBar = lineBar;
            this.playItem = playItem;
            this.pauseItem = pauseItem;
            this.path = path;
        }

        private void releaseMediaPlayer(){
            if(mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        private void setPlayerOutputFile() throws IOException{
            if(mediaPlayer != null){
                mediaPlayer.setDataSource(path);
            }
        }

        public void resumeVoice(){
            playItem.setVisibility(View.GONE);
            pauseItem.setVisibility(View.VISIBLE);

            mediaPlayer.start();
            customHandler.postDelayed(updateTimerThread, 100);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    pauseItem.setVisibility(View.GONE);
                    playItem.setVisibility(View.VISIBLE);
                    lineBar.setProgress(0);
                    mediaPlayer.stop();
                    releaseMediaPlayer();
                    customHandler.removeCallbacks(updateTimerThread);
                }
            });
        }

        public void pauseVoice(){
            playItem.setVisibility(View.VISIBLE);
            pauseItem.setVisibility(View.GONE);

            if(mediaPlayer != null){
                mediaPlayer.pause();
                customHandler.removeCallbacks(updateTimerThread);
            }
        }

        public void playVoice() throws Exception{
            playItem.setVisibility(View.GONE);
            pauseItem.setVisibility(View.VISIBLE);

            this.getMediaPlayer();
            this.setPlayerOutputFile();

            mediaPlayer.prepare();
            mediaPlayer.start();
            durationText.setText("00:00");
            startHTime = mediaPlayer.getCurrentPosition();
            lineBar.setMax(mediaPlayer.getDuration());
            lineBar.setProgress((int) startHTime);
            customHandler.postDelayed(updateTimerThread, 0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    pauseItem.setVisibility(View.GONE);
                    playItem.setVisibility(View.VISIBLE);
                    lineBar.setProgress(0);
                    mediaPlayer.stop();
                    releaseMediaPlayer();
                    customHandler.removeCallbacks(updateTimerThread);
                }
            });

            lineBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser && mediaPlayer != null){
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private Runnable updateTimerThread = new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                updateRun(this);
            }
        };

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        private void updateRun(Runnable runnable){
            timeInMilliseconds = mediaPlayer.getCurrentPosition() - startHTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            lineBar.setProgress((int) timeInMilliseconds);

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            if (durationText != null)
                durationText.setText("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs));

            customHandler.postDelayed(runnable, 0);
        }
    }
}
