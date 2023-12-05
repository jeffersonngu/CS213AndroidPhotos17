package com.photos.models;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class Album {

    @Embedded
    private final AlbumInfo albumInfo;

    @Relation(parentColumn = "id", entityColumn = "albumId")
    private final List<Photo> photoList;

    public Album(String name) {
        albumInfo = new AlbumInfo(name);
        photoList = new ArrayList<>();
    }

    public Album(AlbumInfo albumInfo, List<Photo> photoList) {
        this.albumInfo = albumInfo;
        this.photoList = photoList;
    }

    @NonNull
    public String getName() {
        return albumInfo.name;
    }

    public void rename(String newName) {
        albumInfo.name = newName;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public AlbumInfo getAlbumInfo() {
        return albumInfo;
    }

    @Entity(tableName = "albums", indices = {
            @Index(value = "name", unique = true)
    })
    public static class AlbumInfo {

        @PrimaryKey(autoGenerate = true)
        private int id;

        @NonNull
        private String name;

        @Ignore
        public AlbumInfo(@NonNull String name) {
            this.name = name;
        }

        public AlbumInfo(int id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @NonNull
        public String getName() { return name; }
    }
}
