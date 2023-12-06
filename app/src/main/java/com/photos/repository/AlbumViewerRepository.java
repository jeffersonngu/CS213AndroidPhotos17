package com.photos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

public class AlbumViewerRepository {

    private final AlbumsDao albumsDao;

    public AlbumViewerRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<Album> getAlbum(int albumId) {
        return albumsDao.getAlbum(albumId);
    }
}
