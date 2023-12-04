package com.photos.models;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class Album {

    @Embedded
    private final AlbumInfo albumInfo;

    @Relation(parentColumn = "name", entityColumn = "uri")
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

    @Entity(tableName = "albums")
    public static class AlbumInfo {

        @NonNull
        @PrimaryKey
        private String name;

        public AlbumInfo(@NonNull String name) {
            this.name = name;
        }

        @NonNull
        public String getName() { return name; }
    }
}
