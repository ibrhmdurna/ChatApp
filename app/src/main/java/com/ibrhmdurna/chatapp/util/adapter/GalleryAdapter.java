package com.ibrhmdurna.chatapp.util.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.models.File;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.GridImageView;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<File> fileList;
    private Context context;
    private OnItemClickListener listener;

    public GalleryAdapter(Context context, List<File> fileList){
        this.context = context;
        this.fileList = fileList;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_layout, viewGroup, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder galleryViewHolder, int i) {
        galleryViewHolder.setTitle(fileList.get(i).getTitle());
        galleryViewHolder.setCount(fileList.get(i).getCount()+"");
        galleryViewHolder.setImage(fileList.get(i).getImage());

        if(i % 2 == 0){
            galleryViewHolder.getRootView().setPadding(0,10,40,10);
        }else {
            galleryViewHolder.getRootView().setPadding(40,10,0,10);
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{

        private TextView title, count;
        private GridImageView image;
        private LinearLayout rootView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_text);
            count = itemView.findViewById(R.id.count_text);
            image = itemView.findViewById(R.id.album_last_image);
            rootView = itemView.findViewById(R.id.album_root_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public LinearLayout getRootView() {
            return rootView;
        }

        public void setImage(String path){
            UniversalImageLoader.setImage(path, image, null, "file:///");
        }

        public void setCount(String text){
            count.setText(text);
        }

        public void setTitle(String text){
            title.setText(text);
        }
    }
}
