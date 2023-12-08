package com.photos.ui.adapters;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.models.Photo;

import java.util.List;

public class ImageViewerAdapter extends RecyclerView.Adapter<ImageViewerAdapter.ViewHolder> {

    /* https://developer.android.com/reference/android/support/v7/recyclerview/extensions/AsyncListDiffer.html */
    private final AsyncListDiffer<Photo> mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.getId() == newPhoto.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.equals(newPhoto)
                    && oldPhoto.getAlbumId() == newPhoto.getAlbumId();
        }
    });

    public ImageViewerAdapter(List<Photo> photoList) {
        mDiffer.submitList(photoList);
    }

    public void setPhotoList(List<Photo> photoList) {
        mDiffer.submitList(photoList);
    }

    @NonNull
    @Override
    public ImageViewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewerAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageURI(null);

        Photo currentPhoto = mDiffer.getCurrentList().get(position);
        holder.imageView.setImageURI(currentPhoto.getUri());
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
