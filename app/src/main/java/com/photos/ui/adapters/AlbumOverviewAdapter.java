package com.photos.ui.adapters;

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
import com.photos.models.Photo;
import com.photos.ui.activities.AlbumOverviewListener;

import java.util.List;
import java.util.Objects;

public class AlbumOverviewAdapter extends RecyclerView.Adapter<AlbumOverviewAdapter.ViewHolder> {

    private final AlbumOverviewListener listener;

    /**
     * RecyclerView as of now does not directly use ContextMenuInfo, we can do
     * a rather hacky workaround by just saving the last known clicked
     * position and using that.
     */
    private int lastLongClickPosition;

    /* https://developer.android.com/reference/android/support/v7/recyclerview/extensions/AsyncListDiffer.html */
    private final AsyncListDiffer<Album> mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            return oldAlbum.getAlbumInfo().getId() == newAlbum.getAlbumInfo().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldAlbum, @NonNull Album newAlbum) {
            List<Photo> oldPhotoList = oldAlbum.getPhotoList();
            Photo oldLastPhoto = oldPhotoList.isEmpty() ? null : oldPhotoList.get(oldPhotoList.size() - 1);

            List<Photo> newPhotoList = newAlbum.getPhotoList();
            Photo newLastPhoto = newPhotoList.isEmpty() ? null : newPhotoList.get(newPhotoList.size() - 1);

            return oldAlbum.equals(newAlbum)
                    && oldAlbum.getPhotoList().size() == newAlbum.getPhotoList().size()
                    && Objects.equals(oldLastPhoto, newLastPhoto);
        }
    });

    public AlbumOverviewAdapter(AlbumOverviewListener listener, List<Album> albumList) {
        this.listener = listener;
        setAlbumList(albumList);
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
        holder.thumbnail.setImageResource(R.drawable.no_image_icon); /* Interestingly, if a holder was deleted, then a new one is added it is not properly "wiped" before recycled */

        Album currentAlbum = mDiffer.getCurrentList().get(position);

        holder.title.setText(currentAlbum.getName());
        holder.photoCount.setText(String.valueOf(currentAlbum.getPhotoList().size()));

        if (!currentAlbum.getPhotoList().isEmpty()) {
            Photo lastPhoto = currentAlbum.getPhotoList().get(currentAlbum.getPhotoList().size() - 1);
            holder.thumbnail.setImageURI(lastPhoto.getUri());
        }
    }

    @Override
    public int getItemCount() {
       return mDiffer.getCurrentList().size();
    }

    public Album getLastLongClickAlbum() {
        return mDiffer.getCurrentList().get(lastLongClickPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView thumbnail;
        private final TextView title;
        private final TextView photoCount;

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(null);

            title = view.findViewById(R.id.tv_album_name);
            thumbnail = view.findViewById(R.id.iv_album_thumbnail);
            photoCount = view.findViewById(R.id.tv_album_photocount);

            view.setOnLongClickListener(tempView -> {
                lastLongClickPosition = getLayoutPosition();
                return false;
            });
        }

        @Override
        public void onClick(View v) {
            if (getLayoutPosition() != RecyclerView.NO_POSITION && getLayoutPosition() == getAdapterPosition()) {
                listener.viewAlbum(mDiffer.getCurrentList().get(getLayoutPosition()));
            }
        }
    }
}
