package com.ibrhmdurna.chatapp.util.controller;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;

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
}
