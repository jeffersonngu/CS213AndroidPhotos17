package com.photos;

import android.app.Application;

import com.photos.albumsoverview.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class Photos extends Application {

    private static List<AlbumModel> albumList;

    @Override
    public void onCreate() {
        super.onCreate();
        albumList = new ArrayList<>();
    }

    public static List<AlbumModel> getAlbumList() {
        return albumList;
    }
}
