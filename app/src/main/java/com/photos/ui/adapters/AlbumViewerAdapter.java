package com.photos.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.models.Photo;

public class AlbumViewerAdapter extends RecyclerView.Adapter<AlbumViewerAdapter.ViewHolder> {

    private final Context context;

    /* https://developer.android.com/reference/android/support/v7/recyclerview/extensions/AsyncListDiffer.html */
    private final AsyncListDiffer<Photo> mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.getId() == newPhoto.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.equals(newPhoto)
                    && oldPhoto.getAlbumId() == newPhoto.getId();
        }
    });

    public AlbumViewerAdapter(Context context, Album album) {
        this.context = context;
        setAlbum(album);
    }

    public void setAlbum(Album album) {
        if (album != null) mDiffer.submitList(album.getPhotoList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo currentPhoto = mDiffer.getCurrentList().get(position);
        holder.thumbnail.setImageURI(currentPhoto.getUri());
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView thumbnail;

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);

            thumbnail = view.findViewById(R.id.iv_photo_thumbnail);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
