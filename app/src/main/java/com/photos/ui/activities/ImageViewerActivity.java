package com.photos.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.photos.R;
import com.photos.models.Photo;
import com.photos.ui.adapters.ImageViewerAdapter;
import com.photos.viewmodels.ImageViewerViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageViewerAdapter adapter;
    private ImageViewerViewModel imageViewerViewModel;

    private int albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) finish();

        int entryPosition = getIntent().getIntExtra("entryPosition", -1);
        if (entryPosition < 0) finish();

        imageViewerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ImageViewerViewModel.INITIALIZER)
        ).get(ImageViewerViewModel.class);

        setContentView(R.layout.activity_imageviewer);
        ViewPager2 viewpager = findViewById(R.id.vp_imageviewer);
        viewpager.setPageTransformer(new MarginPageTransformer(50));

        LiveData<List<Photo>> photoListLiveData = imageViewerViewModel.getPhotoListLiveData(albumId);

        adapter = new ImageViewerAdapter(photoListLiveData.getValue());
        viewpager.setAdapter(adapter);

        AtomicBoolean firstRun = new AtomicBoolean(true); /* Only set the entry on first run, use atomic due to lambda */
        photoListLiveData.observe(this, photoList -> {
            adapter.setPhotoList(photoList);
            if (firstRun.get()) {
                firstRun.set(false);
                viewpager.setCurrentItem(entryPosition, true);
            }
        });
    }
}
