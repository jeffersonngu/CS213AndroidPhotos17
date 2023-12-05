package com.photos.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.photos.models.Album;
import com.photos.models.Photo;

import java.util.List;

/**
 * Handles our Inserts and Transactions
 */
@Dao
public abstract class AlbumsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertAlbumInfo(Album.AlbumInfo albumInfo);

    @Delete
    public abstract void deleteAlbumInfo(Album.AlbumInfo albumInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertPhoto(Photo photo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertPhotoList(List<Photo> photoList);

    @Delete
    public abstract void deletePhoto(Photo photo);

    @Transaction
    public void upsertAlbum(Album album) {
        upsertAlbumInfo(album.getAlbumInfo());
        upsertPhotoList(album.getPhotoList());
    }

    @Transaction
    public void upsertAlbumList(List<Album> albumList) {
        albumList.forEach(this::upsertAlbum);
    }

    @Transaction
    public void deleteAlbum(Album album) {
        deleteAlbumInfo(album.getAlbumInfo()); /* Because we use onDelete CASCADE we do not have to worry about Photo */
    }

    @Transaction
    @Query("SELECT * FROM albums")
    public abstract LiveData<List<Album>> getAllAlbums();
}
