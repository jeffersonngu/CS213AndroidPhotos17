package com.photos;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.photos.database.AlbumsDatabase;
import com.photos.models.Album;

import java.util.List;

public class Photos extends Application {

    private static List<Album> albumList;

    private final AlbumsDatabase DB = AlbumsDatabase.getInstance(this);

    @Override
    public void onCreate() {
        super.onCreate();

        albumList = DB.albumDao().getAllAlbums();

        registerCallbacks();
    }

    public static List<Album> getAlbumList() {
        return albumList;
    }

    private void registerCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                // Perhaps we want to do batch updates but for now no, we will use incremental
                // DB.albumDao().upsertAlbumList(albumList);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }
}
