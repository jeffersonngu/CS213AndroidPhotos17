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
    public abstract void insertAlbumInfo(Album.AlbumInfo albumInfo);

    @Update
    public abstract void updateAlbumInfo(Album.AlbumInfo albumInfo);

    @Delete
    public abstract void deleteAlbumInfo(Album.AlbumInfo albumInfo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPhoto(Photo photo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPhotoList(List<Photo> photoList);

    @Update
    public abstract void updatePhoto(Photo photo);

    @Update
    public abstract void updatePhotoList(List<Photo> photoList);

    @Delete
    public abstract void deletePhoto(Photo photo);

    @Transaction
    public void insertAlbum(Album album) {
        insertAlbumInfo(album.getAlbumInfo());
        insertPhotoList(album.getPhotoList());
    }

    @Transaction
    public void updateAlbum(Album album) {
        updateAlbumInfo(album.getAlbumInfo());
        updatePhotoList(album.getPhotoList());
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
