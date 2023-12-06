package com.photos.ui.albumsoverview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.domain.Album;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private final Context context;

    /* https://developer.android.com/reference/android/support/v7/recyclerview/extensions/AsyncListDiffer.html */
    private final AsyncListDiffer<Album> albumDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);
    public static final DiffUtil.ItemCallback<Album> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldAlbum.getAlbumInfo().getId() == newAlbum.getAlbumInfo().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldAlbum.equals(newAlbum);
        }
    };

    public AlbumListAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumDiffer.submitList(albumList);
    }

    public void setAlbumList(List<Album> albumList) {
        albumDiffer.submitList(albumList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album currentAlbum = albumDiffer.getCurrentList().get(position);

        holder.title.setText(currentAlbum.getName());
        holder.photoCount.setText(String.valueOf(currentAlbum.getPhotoList().size()));
    }

    @Override
    public int getItemCount() {
       return albumDiffer.getCurrentList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView thumbnail;
        private final TextView title;
        private final TextView photoCount;

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);

            title = view.findViewById(R.id.tv_album_name);
            thumbnail = view.findViewById(R.id.iv_album_thumbnail);
            photoCount = view.findViewById(R.id.tv_album_photocount);
        }

        @Override
        public void onClick(View v) {
            if (getLayoutPosition() != RecyclerView.NO_POSITION && getLayoutPosition() == getAdapterPosition()) {
                ((AlbumOverviewActivity) context).viewAlbum(albumDiffer.getCurrentList().get(getLayoutPosition()));
            }
        }
    }
}
