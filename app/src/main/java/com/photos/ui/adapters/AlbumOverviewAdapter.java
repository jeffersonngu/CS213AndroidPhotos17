package com.photos.ui.adapters;

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
import com.photos.models.Album;
import com.photos.ui.activities.AlbumOverviewActivity;

import java.util.List;

public class AlbumOverviewAdapter extends RecyclerView.Adapter<AlbumOverviewAdapter.ViewHolder> {

    private final Context context;

    /* https://developer.android.com/reference/android/support/v7/recyclerview/extensions/AsyncListDiffer.html */
    private final AsyncListDiffer<Album> mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            return oldAlbum.getAlbumInfo().getId() == newAlbum.getAlbumInfo().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            return oldAlbum.equals(newAlbum);
        }
    });

    public AlbumOverviewAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.mDiffer.submitList(albumList);
    }

    public void setAlbumList(List<Album> albumList) {
        mDiffer.submitList(albumList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album currentAlbum = mDiffer.getCurrentList().get(position);

        holder.title.setText(currentAlbum.getName());
        holder.photoCount.setText(String.valueOf(currentAlbum.getPhotoList().size()));
    }

    @Override
    public int getItemCount() {
       return mDiffer.getCurrentList().size();
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
                ((AlbumOverviewActivity) context).viewAlbum(mDiffer.getCurrentList().get(getLayoutPosition()));
            }
        }
    }
}
