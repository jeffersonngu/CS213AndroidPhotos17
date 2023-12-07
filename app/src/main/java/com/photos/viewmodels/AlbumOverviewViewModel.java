package com.photos.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.photos.PhotosApplication;
import com.photos.repository.AlbumOverviewRepository;
import com.photos.models.Album;

import java.util.List;

public class AlbumOverviewViewModel extends ViewModel {

    @NonNull
    private final AlbumOverviewRepository albumOverviewRepository;

    private final LiveData<List<Album>> albumListLiveData;

    public AlbumOverviewViewModel(@NonNull AlbumOverviewRepository photosRepository) {
        this.albumOverviewRepository = photosRepository;
        this.albumListLiveData = photosRepository.getAlbumListData();
    }

    public LiveData<List<Album>> getAlbumListLiveData() {
        return albumListLiveData;
    }

    public void addNewAlbum(Album album) {
        albumOverviewRepository.insertAlbum(album);
    }

    public void removeAlbum(Album album) {
        albumOverviewRepository.deleteAlbum(album);
    }

    /* https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#java */
    public static final ViewModelInitializer<AlbumOverviewViewModel> INITIALIZER = new ViewModelInitializer<>(
        AlbumOverviewViewModel.class,
        creationExtras -> {
            PhotosApplication app = (PhotosApplication) creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
            assert app != null;
            return new AlbumOverviewViewModel(new AlbumOverviewRepository(app));
        }
    );
}
