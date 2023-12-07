package com.photos.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.photos.PhotosApplication;
import com.photos.models.Album;
import com.photos.models.Photo;
import com.photos.repository.AlbumViewerRepository;

public class AlbumViewerViewModel extends ViewModel {

    @NonNull
    private final AlbumViewerRepository albumViewerRepository;

    public AlbumViewerViewModel(@NonNull AlbumViewerRepository albumViewerRepository) {
        this.albumViewerRepository = albumViewerRepository;
    }

    public LiveData<Album> getAlbum(int albumId) {
        return albumViewerRepository.getAlbum(albumId);
    }

    public void addNewPhoto(Photo photo) {
        albumViewerRepository.insertPhoto(photo);
    }

    public void removePhoto(Photo photo) {
        albumViewerRepository.deletePhoto(photo);
    }

    /* https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#java */
    public static final ViewModelInitializer<AlbumViewerViewModel> INITIALIZER = new ViewModelInitializer<>(
        AlbumViewerViewModel.class,
        creationExtras -> {
            PhotosApplication app = (PhotosApplication) creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
            assert app != null;
            return new AlbumViewerViewModel(new AlbumViewerRepository(app));
        }
    );
}
