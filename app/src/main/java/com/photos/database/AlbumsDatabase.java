package com.photos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.photos.models.Album;
import com.photos.models.Photo;

@Database(entities = { Album.AlbumInfo.class, Photo.class }, version = 1)
@TypeConverters({ SetStringConverter.class, UriConverter.class })
public abstract class AlbumsDatabase extends RoomDatabase {

    private static AlbumsDatabase instance;

    public abstract AlbumsDao albumDao();

    public static synchronized AlbumsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AlbumsDatabase.class,
                    "albums.db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
