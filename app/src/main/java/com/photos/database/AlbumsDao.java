package com.photos.database;

import android.content.Context;

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
import com.photos.util.PhotoFileUtil;

import java.util.List;

/**
 * Handles our Inserts and Transactions
 */
@Dao
public abstract class AlbumsDao {

    private Context context;

    /**
     * Pass a context to help with Transactions
     * @param context Any context, preferably the Application's Context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAlbumInfo(Album.AlbumInfo albumInfo);

    @Update
    public abstract void updateAlbumInfo(Album.AlbumInfo albumInfo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPhoto(Photo photo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPhotoList(List<Photo> photoList);

    @Update
    public abstract void updatePhotoList(List<Photo> photoList);

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

    @Query("UPDATE albums SET name = :newName WHERE id = :albumId")
    public abstract void renameAlbum(int albumId, String newName);

    private void deletePhotoFile(Photo photo) {
        context.deleteFile(PhotoFileUtil.getFileName(context, photo.getUri()));
    }

    @Delete
    abstract void deleteAlbumInfo(Album.AlbumInfo albumInfo);

    @Delete
    abstract void deletePhotoEntity(Photo photo);

    @Transaction
    public void deletePhoto(Photo photo) {
        deletePhotoFile(photo);
        deletePhotoEntity(photo);
    }

    @Transaction
    public void deleteAlbum(Album album) {
        album.getPhotoList().forEach(this::deletePhotoFile);
        deleteAlbumInfo(album.getAlbumInfo()); /* Because we use onDelete CASCADE we do not have to worry about Photo */
    }

    @Transaction
    @Query("SELECT * FROM albums")
    public abstract LiveData<List<Album>> getAllAlbums();

    @Transaction
    @Query("SELECT * FROM albums WHERE id = :albumId")
    public abstract LiveData<Album> getAlbum(int albumId);
}
