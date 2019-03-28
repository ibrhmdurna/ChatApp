package com.ibrhmdurna.chatapp.Util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Settings.ChatSettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private Context context;
    private List<Integer> colorList;

    public ColorAdapter(Context context){
        this.context = context;
        colorList = new ArrayList<>();
        setColorList();
    }

    private void setColorList(){
        for (int i = 0; i <= 26; i++){
            switch (i){
                case 0:
                    colorList.add(R.color.solidColor0);
                    break;
                case 1:
                    colorList.add(R.color.solidColor1);
                    break;
                case 2:
                    colorList.add(R.color.solidColor2);
                    break;
                case 3:
                    colorList.add(R.color.solidColor3);
                    break;
                case 4:
                    colorList.add(R.color.solidColor4);
                    break;
                case 5:
                    colorList.add(R.color.solidColor5);
                    break;
                case 6:
                    colorList.add(R.color.solidColor6);
                    break;
                case 7:
                    colorList.add(R.color.solidColor7);
                    break;
                case 8:
                    colorList.add(R.color.solidColor8);
                    break;
                case 9:
                    colorList.add(R.color.solidColor9);
                    break;
                case 10:
                    colorList.add(R.color.solidColor10);
                    break;
                case 11:
                    colorList.add(R.color.solidColor11);
                    break;
                case 12:
                    colorList.add(R.color.solidColor12);
                    break;
                case 13:
                    colorList.add(R.color.solidColor13);
                    break;
                case 14:
                    colorList.add(R.color.solidColor14);
                    break;
                case 15:
                    colorList.add(R.color.solidColor15);
                    break;
                case 16:
                    colorList.add(R.color.solidColor16);
                    break;
                case 17:
                    colorList.add(R.color.solidColor17);
                    break;
                case 18:
                    colorList.add(R.color.solidColor18);
                    break;
                case 19:
                    colorList.add(R.color.solidColor19);
                    break;
                case 20:
                    colorList.add(R.color.solidColor20);
                    break;
                case 21:
                    colorList.add(R.color.solidColor21);
                    break;
                case 22:
                    colorList.add(R.color.solidColor22);
                    break;
                case 23:
                    colorList.add(R.color.solidColor23);
                    break;
                case 24:
                    colorList.add(R.color.solidColor24);
                    break;
                case 25:
                    colorList.add(R.color.solidColor25);
                    break;
                case 26:
                    colorList.add(R.color.solidColor26);
                    break;
            }
        }
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.solid_color_layout, viewGroup, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder colorViewHolder, int i) {
        colorViewHolder.setColor(colorList.get(i));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder{

        private ImageView solidView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);

            solidView = itemView.findViewById(R.id.solid_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageController.setBackgroundImage(null);
                    ImageController.setBackgroundColor(colorList.get(getAdapterPosition()));

                    Intent chatSettingsIntent = new Intent(context, ChatSettingsActivity.class);
                    chatSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(chatSettingsIntent);
                }
            });
        }

        public void setColor(int color){
            solidView.setImageResource(color);
        }
    }
}
