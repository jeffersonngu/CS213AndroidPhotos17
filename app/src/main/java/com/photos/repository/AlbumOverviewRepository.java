package com.photos.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.photos.database.AlbumsDao;
import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AlbumOverviewRepository {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AlbumsDao albumsDao;

    public AlbumOverviewRepository(Application application) {
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(application);
        albumsDao = albumsDatabase.albumDao();
    }

    public LiveData<List<Album>> getAlbumListData() {
        return albumsDao.getAllAlbums();
    }

    @Nullable
    public Boolean insertAlbum(Album album) {
        Future<Boolean> result = executorService.submit(() -> albumsDao.insertAlbum(album));
        try {
            return result.get(1000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w("Photos", "Inserting Album timed out! " + e);
        } catch (InterruptedException e) {
            Log.w("Photos", "Executor Service was interrupted: " + e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteAlbum(Album album) {
        executorService.execute(() -> albumsDao.deleteAlbum(album));
    }

    @Nullable
    public Boolean renameAlbum(Album album, String newName) {
        Future<Integer> result = executorService.submit(() -> albumsDao.renameAlbum(album.getAlbumInfo().getId(), newName));
        try {
            return result.get(1000, TimeUnit.MILLISECONDS) > 0;
        } catch (TimeoutException e) {
            Log.w("Photos", "Renaming Album timed out! " + e);
        } catch (InterruptedException e) {
            Log.w("Photos", "Executor Service was interrupted: " + e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void onDestroy() {
        executorService.shutdown();
    }
}
