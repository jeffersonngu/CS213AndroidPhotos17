package com.photos.albumsoverview;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.photos.albumviewer.PhotoModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumModel extends ViewModel {

    private final MutableLiveData<List<PhotoModel>> photoList = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> name = new MutableLiveData<>("No Name Found");

    public AlbumModel(String name) {
        this.name.setValue(name);
    }

    public String getName() {
        return name.getValue();
    }

    public void rename(String newName) {
        name.setValue(newName);
    }

    public List<PhotoModel> getPhotoList() {
        return photoList.getValue();
    }
}
