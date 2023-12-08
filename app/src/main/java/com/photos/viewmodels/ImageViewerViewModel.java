package com.photos.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.photos.PhotosApplication;
import com.photos.models.Photo;
import com.photos.repository.ImageViewerRepository;

import java.util.List;

public class ImageViewerViewModel extends ViewModel {

    @NonNull
    private final ImageViewerRepository imageViewerRepository;

    public ImageViewerViewModel(@NonNull ImageViewerRepository imageViewerRepository) {
        this.imageViewerRepository = imageViewerRepository;
    }

    public LiveData<List<Photo>> getPhotoListLiveData(int albumId) {
        return imageViewerRepository.getPhotoListData(albumId);
    }

    /* https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#java */
    public static final ViewModelInitializer<ImageViewerViewModel> INITIALIZER = new ViewModelInitializer<>(
            ImageViewerViewModel.class,
            creationExtras -> {
                PhotosApplication app = (PhotosApplication) creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
                assert app != null;
                return new ImageViewerViewModel(new ImageViewerRepository(app));
            }
    );
}
