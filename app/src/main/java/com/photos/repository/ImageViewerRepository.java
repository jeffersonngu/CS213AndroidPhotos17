package com.photos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Photo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageViewerRepository {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AlbumsDao albumsDao;

    public ImageViewerRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<List<Photo>> getPhotoListData(int albumId) {
        return albumsDao.getPhotoList(albumId);
    }

    public void photoSetLocation(int photoId, String newLocation) {
        executorService.execute(() -> albumsDao.photoSetLocation(photoId, newLocation));
    }

    public void photoPeopleListAdd(int photoId, String newPerson) {
        executorService.execute(() -> albumsDao.photoPeopleListAdd(photoId, newPerson));
    }

    public void photoPeopleListRemove(Photo photo, String person) {
        if (photo.getPeople().contains(person)) {
            executorService.execute(() -> albumsDao.photoPeopleListRemove(photo.getId(), person));
        }
    }

    public void onDestroy() {
        executorService.shutdown();
    }
}
