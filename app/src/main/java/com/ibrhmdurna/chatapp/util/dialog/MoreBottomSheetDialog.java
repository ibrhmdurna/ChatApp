package com.ibrhmdurna.chatapp.util.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;

@SuppressLint("ValidFragment")
public class MoreBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private String uid;

    @SuppressLint("ValidFragment")
    public MoreBottomSheetDialog(String uid){
        this.uid = uid;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.more_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) contentView.getParent()).setBackgroundColor(getContext().getColor(android.R.color.transparent));

        LinearLayout shareItem = contentView.findViewById(R.id.share_item);
        LinearLayout blockItem = contentView.findViewById(R.id.block_item);
        LinearLayout reportItem = contentView.findViewById(R.id.report_item);
        final LinearLayout deleteItem = contentView.findViewById(R.id.delete_friend_item);

        shareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("share");
            }
        });

        blockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("block");
            }
        });

        reportItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("report");
            }
        });

        String current_uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    deleteItem.setVisibility(View.VISIBLE);
                }
                else {
                    deleteItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
