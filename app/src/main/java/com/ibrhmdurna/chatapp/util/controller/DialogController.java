package com.ibrhmdurna.chatapp.util.controller;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Message;

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
                    Delete.getInstance().myMessage(message, chatUid);
                }
                else{
                    Delete.getInstance().myMessage(message, chatUid);
                }
            }
        });

        LinearLayout unsendItem = view.findViewById(R.id.unSend_item);
        if(myMessage){
            unsendItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Delete.getInstance().unSendMessage(message, chatUid);
                }
            });
        }
        else{
            unsendItem.setVisibility(View.GONE);
        }

        LinearLayout copyItem = view.findViewById(R.id.copy_item);
        copyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", message.getMessage());
                clipboard.setPrimaryClip(clip);
            }
        });

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

        LinearLayout viewItem = view.findViewById(R.id.view_item);
        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent profileIntent = new Intent(context.getActivity(), ProfileActivity.class);
                profileIntent.putExtra("user_id", chatUid);
                context.startActivity(profileIntent);
            }
        });

        LinearLayout clearItem = view.findViewById(R.id.clear_item);
        clearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Delete.getInstance().clearChat(chatUid);
            }
        });

        LinearLayout deleteItem = view.findViewById(R.id.delete_item);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Delete.getInstance().deleteChat(chatUid);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
}
