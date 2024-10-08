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
import com.photos.util.PhotosFileUtils;
import com.photos.util.PhotosViewUtils;
import com.photos.viewmodels.ImageViewerViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class ImageViewerActivity extends AppCompatActivity implements ImagerViewerListener, PopupMenu.OnMenuItemClickListener {

    private ImageViewerAdapter adapter;
    private ImageViewerViewModel imageViewerViewModel;

    private ViewPager2 viewpager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initializing our ViewModel */
        imageViewerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ImageViewerViewModel.INITIALIZER)
        ).get(ImageViewerViewModel.class);

        /* Check how we enter our activity */
        LiveData<List<Photo>> photoListLiveData;
        int albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) {
            String location = getIntent().getStringExtra("location");
            String person = getIntent().getStringExtra("person");
            boolean conjunction = getIntent().getBooleanExtra("conjunction", true);
            photoListLiveData = imageViewerViewModel.getPhotoListLiveData(location, person, conjunction);
        } else {
            photoListLiveData = imageViewerViewModel.getPhotoListLiveData(albumId);
        }

        int entryPosition = getIntent().getIntExtra("entryPosition", -1);
        if (entryPosition < 0) finish();

        /* Initializing ViewPager layout */
        setContentView(R.layout.activity_imageviewer);
        viewpager = findViewById(R.id.vp_imageviewer);
        viewpager.setPageTransformer(new MarginPageTransformer(50));

        adapter = new ImageViewerAdapter(this, photoListLiveData.getValue());
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

    public void updateDetails() {
        Photo photo = adapter.getPhoto(viewpager.getCurrentItem());

        TextView filenameDetails = findViewById(R.id.tv_imageviewer_filename);
        StringBuilder stringBuilderFilename = new StringBuilder("Name: ").append(PhotosFileUtils.getFileName(this, photo.getUri()));
        filenameDetails.setText(stringBuilderFilename);

        TextView locationTagDetails = findViewById(R.id.tv_imageviewer_location);
        StringBuilder stringBuilderLocation;
        if (photo.getLocation() != null) {
            stringBuilderLocation = new StringBuilder("Location: ").append(photo.getLocation());
        } else {
            stringBuilderLocation = new StringBuilder("Location: None");
        }
        locationTagDetails.setText(stringBuilderLocation);

        TextView peopleTagDetails = findViewById(R.id.tv_imageviewer_people);
        StringBuilder stringBuilderPeople;
        if (!photo.getPeople().isEmpty()) {
            stringBuilderPeople = new StringBuilder("People:\n");
            photo.getPeople().forEach(s -> stringBuilderPeople.append(" ").append(s).append("\n"));
        } else {
            stringBuilderPeople = new StringBuilder("People: None");
        }
        peopleTagDetails.setText(stringBuilderPeople);
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
        } else if (id == R.id.popm_imageviewer_deleteLocationTag) {
            deleteLocationTag(adapter.getPhoto(viewpager.getCurrentItem()));
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

    private void deleteLocationTag(Photo photo) {
        imageViewerViewModel.photoSetLocation(photo, "");
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
            if (input.isBlank()) {
                alertDialog.setMessage("Entered person is blank");
                return;
            }
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
                .setPositiveButton("Remove", (dialog, which) -> {
                    IntStream.range(0, checked.length)
                            .filter(i -> checked[i])
                            .forEach(i -> imageViewerViewModel.photoRemovePerson(photo, people[i]));
                })
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
