package com.photos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.photos.models.Album;
import com.photos.models.Photo;

import java.util.List;

@Dao
public interface AlbumsDao {

    @Insert
    long insertAlbum(Album.AlbumInfo album);

    @Insert
    long[] insertPhoto(List<Photo> photoList);

    @Transaction
    @Query("SELECT * FROM albums")
    List<Album> getAllAlbums();
}
