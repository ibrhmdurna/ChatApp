package com.ibrhmdurna.chatapp.Utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ibrhmdurna.chatapp.Image.BackgroundActivity;
import com.ibrhmdurna.chatapp.Image.ProfileImageActivity;
import com.ibrhmdurna.chatapp.Image.ShareActivity;
import com.ibrhmdurna.chatapp.R;

import java.util.List;

public class CAlbumAdapter extends RecyclerView.Adapter<CAlbumAdapter.AlbumViewHolder> {

    private List<String> list;
    private AppCompatActivity context;

    private String isContext;

    public CAlbumAdapter(AppCompatActivity context, List<String> list, String isContext){
        this.context = context;
        this.list = list;
        this.isContext = isContext;
        ImageController.setPath(list);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.camera_gallery_layout, viewGroup, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder albumViewHolder, final int i) {
        albumViewHolder.setImage(list.get(i));

        albumViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (isContext) {
                    case "Share":
                        Intent shareIntent = new Intent(context, ShareActivity.class);
                        shareIntent.putExtra("position", i);
                        context.startActivity(shareIntent);
                        context.overridePendingTransition(0, 0);
                        break;
                    case "Profile":
                        Intent profileIntent = new Intent(context, ProfileImageActivity.class);
                        profileIntent.putExtra("position", i);
                        context.startActivity(profileIntent);
                        context.overridePendingTransition(0, 0);
                        break;
                    case "Background":
                        Intent backgroundIntent = new Intent(context, BackgroundActivity.class);
                        backgroundIntent.putExtra("position", i);
                        context.startActivity(backgroundIntent);
                        context.overridePendingTransition(0, 0);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        private GridImageView imageView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.album_image_view);
        }

        public GridImageView getImageView() {
            return imageView;
        }

        public void setImage(String path){
            UniversalImageLoader.setImage(path, imageView, null, "file://");
        }
    }
}
