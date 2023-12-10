package com.photos.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
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
import java.util.stream.IntStream;

public class ImageViewerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageViewerAdapter adapter;
    private ImageViewerViewModel imageViewerViewModel;

    private int albumId;

    private ViewPager2 viewpager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Checks that we entered the activity correctly */
        albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) finish();

        int entryPosition = getIntent().getIntExtra("entryPosition", -1);
        if (entryPosition < 0) finish();

        /* Initializing our ViewModel */
        imageViewerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ImageViewerViewModel.INITIALIZER)
        ).get(ImageViewerViewModel.class);

        /* Initializing ViewPager layout */
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

        /* Popup Menu */
        Button overflowMenu = findViewById(R.id.btn_imageviewer_overflowMenu);
        overflowMenu.setOnClickListener(this::showMenu);

        /* Go back to AlbumViewer */
        Button back = findViewById(R.id.btn_imageviewer_back);
        back.setOnClickListener(v -> finish());

        /* Tag Details Handling */
        AtomicBoolean showDetails = new AtomicBoolean(true); /* Currently showing details or not */
        ImageButton detailsToggle = findViewById(R.id.ibtn_imageviewer_toggleDetails);
        ScrollView detailScrollView = findViewById(R.id.sv_imageviewer);
        detailsToggle.setOnClickListener(v -> {
            if (showDetails.get()) {
                detailsToggle.setImageResource(R.drawable.hide);
                showDetails.set(false);
                detailScrollView.setVisibility(View.GONE);
            } else {
                detailsToggle.setImageResource(R.drawable.show);
                showDetails.set(true);
                detailScrollView.setVisibility(View.VISIBLE);
            }
        });

        photoListLiveData.observe(this, photoList -> updateDetails());
        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDetails();
            }
        });

        /* AppBar and ToolBar View Handling */
        AppBarLayout appBarLayout = findViewById(R.id.abl_imageviewer);

        GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                if (appBarLayout.getVisibility() == View.GONE) {
                    Objects.requireNonNull(getWindow().getDecorView().getWindowInsetsController()).show(WindowInsets.Type.systemBars());
                    appBarLayout.setVisibility(View.VISIBLE);
                    if (showDetails.get()) {
                        detailScrollView.setVisibility(View.VISIBLE);
                    }
                    detailScrollView.setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(getWindow().getDecorView().getWindowInsetsController()).hide(WindowInsets.Type.systemBars());
                    appBarLayout.setVisibility(View.GONE);
                    detailScrollView.setVisibility(View.GONE);
                }
                return false;
            }
        });

        /* Note: Interestingly we must access the internal RecyclerView of viewpager instead */
        viewpager.getChildAt(0).setOnTouchListener((view, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void updateDetails() {
        Photo photo = adapter.getPhoto(viewpager.getCurrentItem());

        TextView locationTagDetails = findViewById(R.id.tv_imageviewer_location);
        if (photo.getLocation() != null) {
            StringBuilder stringBuilder = new StringBuilder("Location: ").append(photo.getLocation());
            locationTagDetails.setText(stringBuilder);
        } else {
            StringBuilder stringBuilder = new StringBuilder("Location: None");
            locationTagDetails.setText(stringBuilder);
        }

        TextView peopleTagDetails = findViewById(R.id.tv_imageviewer_people);
        if (!photo.getPeople().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("People:\n");
            photo.getPeople().forEach(s -> stringBuilder.append(" ").append(s).append("\n"));
            peopleTagDetails.setText(stringBuilder);
        } else {
            StringBuilder stringBuilder = new StringBuilder("People: None");
            peopleTagDetails.setText(stringBuilder);
        }
    }

    private void showMenu(View v) {
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
            addPersonTagDialog(adapter.getPhoto(viewpager.getCurrentItem()));
            return true;
        } else if (id == R.id.popm_imageviewer_deletePersonTag) {
            removePersonTagDialog(adapter.getPhoto(viewpager.getCurrentItem()));
            return true;
        }
        return false;
    }

    private void modifyLocationTagDialog(Photo photo) {
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

    private void addPersonTagDialog(Photo photo) {
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        PhotosViewUtils.addEditTextFilter(editText, new InputFilter.LengthFilter(20));
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add to Person Tag")
                .setMessage("Add a new person to the people tag")
                .setView(editText)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();

        /* Custom handler for Submit */
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String input = editText.getText().toString();
            boolean result = imageViewerViewModel.photoAddPerson(photo, input);
            if (result) {
                alertDialog.dismiss();
            } else {
                alertDialog.setMessage("Could not add that person");
            }
        });
    }

    private void removePersonTagDialog(Photo photo) {
        String[] people = photo.getPeople().toArray(new String[0]);
        boolean[] checked = new boolean[photo.getPeople().size()];
        new AlertDialog.Builder(this)
                .setTitle("Remove from Person Tag") /* Cannot use both message and setItems at same time */
                .setMultiChoiceItems(people, checked, ((dialog, which, isChecked) -> checked[which] = isChecked))
                .setPositiveButton("Remove", ((dialog, which) -> {
                    IntStream.range(0, checked.length)
                            .filter(i -> checked[i])
                            .forEach(i -> imageViewerViewModel.photoRemovePerson(photo, people[i]));
                }))
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
