package com.photos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Photo;

import java.util.List;

public class ImageViewerRepository {

    private final AlbumsDao albumsDao;

    public ImageViewerRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<List<Photo>> getPhotoListData(int albumId) {
        return albumsDao.getPhotoList(albumId);
    }
}
