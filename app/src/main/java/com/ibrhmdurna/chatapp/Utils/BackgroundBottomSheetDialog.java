package com.ibrhmdurna.chatapp.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;

import com.ibrhmdurna.chatapp.R;

public class BackgroundBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.background_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) contentView.getParent()).setBackgroundColor(getContext().getColor(android.R.color.transparent));

        ImageView galleryItem = contentView.findViewById(R.id.gallery_item);
        ImageView solidItem = contentView.findViewById(R.id.solid_color_item);
        ImageView deleteItem = contentView.findViewById(R.id.delete_item);

        galleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("gallery");
            }
        });

        solidItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("color");
            }
        });

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
