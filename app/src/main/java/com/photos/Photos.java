package com.photos;

import android.app.Application;

import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

import java.util.List;

public class Photos extends Application {

    private static List<Album> albumList;

    @Override
    public void onCreate() {
        super.onCreate();
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInstance(this);
        albumList = albumsDatabase.albumDao().getAllAlbums();
    }

    public static List<Album> getAlbumList() {
        return albumList;
    }
}
