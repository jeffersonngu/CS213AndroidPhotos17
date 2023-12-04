package com.photos.albumsoverview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.Photos;
import com.photos.R;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlbumModel currentAlbum = Photos.getAlbumList().get(position);

        holder.title.setText(currentAlbum.getName());
        holder.photoCount.setText(String.valueOf(currentAlbum.getPhotoList().size()));

        // holder.imageView.setImageResource(R.drawable.no_image_icon);
        // holder.imageView.setAdjustViewBounds(true);
        // holder.imageView.setMaxWidth(200);
        // holder.imageView.setMaxHeight(200);
        //
        // ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        // layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        // layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // holder.imageView.setLayoutParams(layoutParams);
        //
        // holder.thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public int getItemCount() {
        return Photos.getAlbumList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView thumbnail;
        private final TextView title;
        private final TextView photoCount;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.album_name_txt);
            thumbnail = view.findViewById(R.id.album_thumbnail_img);
            photoCount = view.findViewById(R.id.album_photoCount_txt);
        }
    }
}
