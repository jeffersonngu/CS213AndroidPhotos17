package com.photos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

import java.util.List;
import java.util.concurrent.Executors;

public class PhotosRepository {

    private final AlbumsDao albumsDao;
    private final LiveData<List<Album>> albumListData;

    public PhotosRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
        albumListData = albumsDao.getAllAlbums();
    }

    public LiveData<List<Album>> getAlbumListData() {
        return albumListData;
    }

    public void upsertAlbum(Album album) {
        Executors.newSingleThreadExecutor().execute(() -> albumsDao.upsertAlbum(album));
    }
}
