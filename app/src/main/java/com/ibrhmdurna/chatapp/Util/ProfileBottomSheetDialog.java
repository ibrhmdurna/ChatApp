package com.ibrhmdurna.chatapp.Util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ibrhmdurna.chatapp.R;

@SuppressLint("ValidFragment")
public class ProfileBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private boolean feedBack;

    @SuppressLint("ValidFragment")
    public ProfileBottomSheetDialog(boolean feedBack){
        this.feedBack = feedBack;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.profile_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) contentView.getParent()).setBackgroundColor(getContext().getColor(android.R.color.transparent));

        LinearLayout deleteLayout = contentView.findViewById(R.id.delete_item_layout);
        ImageView galleryItem = contentView.findViewById(R.id.gallery_item);
        ImageView cameraItem = contentView.findViewById(R.id.camera_item);
        ImageView deleteItem = contentView.findViewById(R.id.delete_item);

        galleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("gallery");
            }
        });

        cameraItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("camera");
            }
        });

        if(feedBack){
            deleteLayout.setVisibility(View.GONE);
        }

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("delete");
            }
        });
    }

    public interface BottomSheetListener{
        void onButtonClicked(String action);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(e.toString());
        }
    }
}
