package com.photos.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;
import com.photos.models.Photo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AlbumViewerRepository {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AlbumsDao albumsDao;

    public AlbumViewerRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<List<Photo>> getPhotoListLiveData(int albumId) {
        return albumsDao.getPhotoList(albumId);
    }

    public void insertPhoto(Photo photo) {
        executorService.execute(() -> albumsDao.insertPhoto(photo));
    }

    public void deletePhoto(Photo photo) {
        executorService.execute(() -> albumsDao.deletePhoto(photo));
    }

    public List<Album> getAllAlbums() {
        Future<List<Album>> result = executorService.submit(albumsDao::getAllAlbumsNoLive);
        try {
            return result.get(10000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w("Photos", "Extracting Albums timed out! " + e);
        } catch (InterruptedException e) {
            Log.w("Photos", "Executor Service was interrupted: " + e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void movePhoto(Photo photo, Album newAlbum) {
        executorService.execute(() -> albumsDao.movePhoto(photo.getId(), newAlbum.getAlbumInfo().getId()));
    }

    public LiveData<List<Photo>> queryPhotos(String location, String person, boolean conjunction) {
        boolean hasLocation = !location.isBlank();
        boolean hasPerson = !person.isBlank();
        if (!hasLocation && !hasPerson) { /* Neither location/person */
            return albumsDao.getPhotoList();
        } else if (!hasPerson) { /* Only location */
            return albumsDao.getPhotoListFromLocation(location);
        } else if (!hasLocation) { /* Only person */
            return albumsDao.getPhotoListFromPerson(person);
        } else { /* Both location/person */
            if (conjunction) {
                return albumsDao.getPhotoListFromLocationAndPerson(location, person);
            } else {
                return albumsDao.getPhotoListFromLocationOrPerson(location, person);
            }
        }
    }

    public void onDestroy() {
        executorService.shutdown();
    }
}
