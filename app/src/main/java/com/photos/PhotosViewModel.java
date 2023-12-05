package com.photos;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.photos.models.Album;

import java.util.List;

public class PhotosViewModel extends ViewModel {

    @NonNull
    private final PhotosRepository photosRepository;

    private final LiveData<List<Album>> albumListLiveData;

    public PhotosViewModel(@NonNull PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
        this.albumListLiveData = photosRepository.getAlbumListData();
    }

    public LiveData<List<Album>> getAlbumListLiveData() {
        return albumListLiveData;
    }

    public void addNewAlbum(Album album) {
        photosRepository.upsertAlbum(album);
        // albumListLiveData.getValue().add(album);
    }

    /* https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#java */
    public static final ViewModelInitializer<PhotosViewModel> initializer = new ViewModelInitializer<>(
            PhotosViewModel.class,
            creationExtras -> {
                PhotosApplication app = (PhotosApplication) creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
                assert app != null;
                return new PhotosViewModel(app.getPhotosRepository());
            }
    );
}
