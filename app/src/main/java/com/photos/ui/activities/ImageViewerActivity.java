package com.photos.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.photos.R;
import com.photos.models.Photo;
import com.photos.ui.adapters.ImageViewerAdapter;
import com.photos.viewmodels.ImageViewerViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageViewerAdapter adapter;
    private ImageViewerViewModel imageViewerViewModel;

    private int albumId;

    @SuppressLint("ClickableViewAccessibility")
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

        AppBarLayout appBarLayout = findViewById(R.id.abl_imageviewer);
        Button back = findViewById(R.id.btn_imageviewer_back);

        /* Note: Interestingly we must access the internal RecyclerView of viewpager instead */
        viewpager.getChildAt(0).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (appBarLayout.getVisibility() == View.GONE) {
                    Objects.requireNonNull(getWindow().getDecorView().getWindowInsetsController()).show(WindowInsets.Type.systemBars());
                    appBarLayout.setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(getWindow().getDecorView().getWindowInsetsController()).hide(WindowInsets.Type.systemBars());
                    appBarLayout.setVisibility(View.GONE);
                }
            }
            return false;
        });
    }
}
