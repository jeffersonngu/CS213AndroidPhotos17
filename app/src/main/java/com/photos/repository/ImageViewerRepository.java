package com.photos.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Photo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        if (newLocation.isBlank()) {
            executorService.execute(() -> albumsDao.photoSetLocation(photoId, null));
        } else {
            executorService.execute(() -> albumsDao.photoSetLocation(photoId, newLocation));
        }
    }

    public boolean photoPeopleSetAdd(Photo photo, String newPerson) {
        Future<Boolean> result = executorService.submit(() -> albumsDao.photoPeopleSetAdd(photo, newPerson));
        try {
            return result.get(1000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w("Photos", "Remove Person from Photo timed out! " + e);
        } catch (InterruptedException e) {
            Log.w("Photos", "Executor Service was interrupted: " + e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void photoPeopleSetRemove(Photo photo, String person) {
        executorService.execute(() -> albumsDao.photoPeopleSetRemove(photo, person));
    }

    public void onDestroy() {
        executorService.shutdown();
    }
}
