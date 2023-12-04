package com.photos.albumsoverview;

import androidx.lifecycle.ViewModel;

import com.photos.albumviewer.Photo;

import java.util.ArrayList;
import java.util.List;


public class Album extends ViewModel {

    private final List<Photo> photoList = new ArrayList<>();

    private String name;

    public Album(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        name = newName;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }
}
