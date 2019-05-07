package com.ibrhmdurna.chatapp.util.controller;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.findAll.MessageFindAll;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.settings.DarkModeActivity;

import java.io.File;

public class DialogController {

    private static DialogController instance;

    private DialogController(){}

    public static synchronized DialogController getInstance() {
        if(instance == null){
            synchronized (DialogController.class){
                instance = new DialogController();
            }
        }
        return instance;
    }

    public AlertDialog dialogCustom(Activity context, String content, String negative, String positive){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView contentText = view.findViewById(R.id.dialog_content_text);
        contentText.setText(content);

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        if(negative == null){
            negativeBtn.setVisibility(View.GONE);
        }
        else {
            negativeBtn.setText(negative);
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText(positive);

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }

    public AlertDialog dialogChatClear(Activity context, String content, String positive){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_delete_chat, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView contentText = view.findViewById(R.id.dialog_content_text);
        contentText.setText(content);

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText(positive);

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }

    public AlertDialog dialogLoading(Activity context, String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView contentText = view.findViewById(R.id.loading_title);
        contentText.setText(content);

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public AlertDialog dialogImageMessage(final Activity context, final Message message, final String chatUid, final boolean myMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_image_message, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final CheckBox removeDeviceCheck = view.findViewById(R.id.delete_device_check);

        LinearLayout deleteItem = view.findViewById(R.id.delete_item);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(myMessage){
                    Delete.getInstance().imageMessage(message, chatUid);
                }
                else{
                    Delete.getInstance().message(message, chatUid);
                }

                if(removeDeviceCheck.isChecked()){
                    File file = new File(message.getPath());

                    if(file.exists()){
                        file.delete();
                    }
                }
            }
        });

        LinearLayout unsendItem = view.findViewById(R.id.unSend_item);
        if(!message.isUnsend()){
            unsendItem.setVisibility(View.VISIBLE);
            if(myMessage){
                unsendItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Update.getInstance().unSendMessage(chatUid, message);

                        if(removeDeviceCheck.isChecked()){
                            File file = new File(message.getPath());

                            if(file.exists()){
                                file.delete();
                            }
                        }
                    }
                });
            }
            else{
                unsendItem.setVisibility(View.GONE);
            }
        }
        else{
            unsendItem.setVisibility(View.GONE);
        }


        LinearLayout copyItem = view.findViewById(R.id.copy_item);

        if(!message.getMessage().equals("") && !message.isUnsend()){
            copyItem.setVisibility(View.VISIBLE);
            copyItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", message.getMessage());
                    clipboard.setPrimaryClip(clip);
                }
            });
        }
        else{
            copyItem.setVisibility(View.GONE);
        }

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public AlertDialog dialogMessage(final Activity context, final Message message, final String chatUid, final boolean myMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_message, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        LinearLayout deleteItem = view.findViewById(R.id.delete_item);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(myMessage){
                    Delete.getInstance().message(message, chatUid);
                }
                else{
                    Delete.getInstance().message(message, chatUid);
                }
            }
        });

        LinearLayout unsendItem = view.findViewById(R.id.unSend_item);
        if(!message.isUnsend()){
            unsendItem.setVisibility(View.VISIBLE);
            if(myMessage){
                unsendItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Update.getInstance().unSendMessage(chatUid, message);
                    }
                });
            }
            else{
                unsendItem.setVisibility(View.GONE);
            }
        }
        else{
            unsendItem.setVisibility(View.GONE);
        }


        LinearLayout copyItem = view.findViewById(R.id.copy_item);
        if(!message.isUnsend()){
            copyItem.setVisibility(View.VISIBLE);
            copyItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", message.getMessage());
                    clipboard.setPrimaryClip(clip);
                }
            });
        }
        else{
            copyItem.setVisibility(View.GONE);
        }

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public AlertDialog dialogChat(final Fragment context, final String chatUid){
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        View view = context.getLayoutInflater().inflate(R.layout.dialog_chat, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final LinearLayout viewItem = view.findViewById(R.id.view_item);
        final LinearLayout markItem = view.findViewById(R.id.mark_as_read_item);
        final View line = view.findViewById(R.id.chat_dialog_line);

        Firebase.getInstance().getDatabaseReference().child("Accounts").child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    viewItem.setVisibility(View.VISIBLE);
                    markItem.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    viewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent profileIntent = new Intent(context.getActivity(), ProfileActivity.class);
                            profileIntent.putExtra("user_id", chatUid);
                            context.startActivity(profileIntent);
                        }
                    });
                }
                else{
                    line.setVisibility(View.GONE);
                    markItem.setVisibility(View.GONE);
                    viewItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public AlertDialog dialogDarkMode(final Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_dark, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent darkIntent = new Intent(context, DarkModeActivity.class);
                context.startActivity(darkIntent);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation_Fade;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
