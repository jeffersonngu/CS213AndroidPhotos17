package com.photos.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.photos.R;
import com.photos.models.Photo;
import com.photos.ui.adapters.ImageViewerAdapter;
import com.photos.util.PhotosViewUtils;
import com.photos.viewmodels.ImageViewerViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageViewerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageViewerAdapter adapter;
    private ImageViewerViewModel imageViewerViewModel;

    private int albumId;

    private ViewPager2 viewpager;

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
        viewpager = findViewById(R.id.vp_imageviewer);
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

        Button overflowMenu = findViewById(R.id.btn_imageviewer_overflowMenu);
        overflowMenu.setOnClickListener(this::showMenu);

        Button back = findViewById(R.id.btn_imageviewer_back);
        back.setOnClickListener(v -> finish());

        AtomicBoolean showDetails = new AtomicBoolean(true); /* Currently showing details or not */
        ImageButton detailsToggle = findViewById(R.id.ibtn_imageviewer_toggleDetails);
        detailsToggle.setOnClickListener(v -> {
            if (showDetails.get()) {
                detailsToggle.setImageResource(R.drawable.show);
                showDetails.set(false);
            } else {
                detailsToggle.setImageResource(R.drawable.hide);
                showDetails.set(true);
            }
        });
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().setGroupDividerEnabled(true);
        popup.setOnMenuItemClickListener(this);
        popup.getMenuInflater().inflate(R.menu.popm_imageviewer, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popm_imageviewer_modifyLocationTag) {
            modifyLocationTagDialog(adapter.getPhoto(viewpager.getCurrentItem()));
            return true;
        } else if (id == R.id.popm_imageviewer_addPersonTag) {
            return true;
        } else if (id == R.id.popm_imageviewer_deletePersonTag) {
            return true;
        }
        return false;
    }

    public void modifyLocationTagDialog(Photo photo) {
        EditText editText = new EditText(this);
        editText.setText(photo.getLocation());
        editText.setMaxLines(1);
        PhotosViewUtils.addEditTextFilter(editText, new InputFilter.LengthFilter(20));
        new AlertDialog.Builder(this)
                .setTitle("Modify Location Tag")
                .setMessage("Set a new location")
                .setView(editText)
                .setPositiveButton("Submit", (dialogInterface, which) -> imageViewerViewModel.photoSetLocation(photo, editText.getText().toString()))
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageViewerViewModel.onDestroy();
    }
}
