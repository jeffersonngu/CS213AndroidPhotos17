package com.photos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

import java.util.List;
import java.util.concurrent.Executors;

public class AlbumOverviewRepository {

    private final AlbumsDao albumsDao;

    public AlbumOverviewRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<List<Album>> getAlbumListData() {
        return albumsDao.getAllAlbums();
    }

    public void insertAlbum(Album album) {
        Executors.newSingleThreadExecutor().execute(() -> albumsDao.insertAlbum(album));
    }

    public void deleteAlbum(Album album) {
        Executors.newSingleThreadExecutor().execute(() -> albumsDao.deleteAlbum(album));
    }

    public void renameAlbum(Album album, String newName) {
        Executors.newSingleThreadExecutor().execute(() -> albumsDao.renameAlbum(album.getAlbumInfo().getId(), newName));
    }
}
