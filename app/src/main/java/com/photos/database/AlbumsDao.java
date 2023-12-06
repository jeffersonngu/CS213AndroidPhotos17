package com.photos.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.photos.models.Album;
import com.photos.models.Photo;

import java.util.List;

/**
 * Handles our Inserts and Transactions
 */
@Dao
public abstract class AlbumsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertAlbumInfo(Album.AlbumInfo albumInfo);

    @Update
    public abstract void updateAlbumInfo(Album.AlbumInfo albumInfo);

    @Transaction
    public void upsertAlbumInfo(Album.AlbumInfo albumInfo) {
        if (insertAlbumInfo(albumInfo) == -1) {
            updateAlbumInfo(albumInfo);
        }
    }

    @Delete
    public abstract void deleteAlbumInfo(Album.AlbumInfo albumInfo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertPhoto(Photo photo);

    @Update
    public abstract void updatePhoto(Photo photo);

    @Transaction
    public void upsertPhoto(Photo photo) {
        if (insertPhoto(photo) == -1) {
            updatePhoto(photo);
        }
    }

    @Transaction
    public void upsertPhotoList(List<Photo> photoList) {
        photoList.forEach(this::upsertPhoto);
    }

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

    @Transaction
    @Query("SELECT * FROM albums WHERE id = :albumId")
    public abstract LiveData<Album> getAlbum(int albumId);
}
