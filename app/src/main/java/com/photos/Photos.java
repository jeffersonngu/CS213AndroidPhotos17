package com.photos;

import android.app.Application;

import com.photos.albumsoverview.Album;

import java.util.ArrayList;
import java.util.List;

public class Photos extends Application {

    private static List<Album> albumList;

    @Override
    public void onCreate() {
        super.onCreate();
        albumList = new ArrayList<>();
    }

    public static List<Album> getAlbumList() {
        return albumList;
    }
}
