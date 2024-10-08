package com.photos.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.models.Photo;
import com.photos.ui.adapters.AlbumViewerAdapter;
import com.photos.util.PhotosFileUtils;
import com.photos.viewmodels.AlbumViewerViewModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AlbumViewerActivity extends AppCompatActivity implements AlbumViewerListener, PopupMenu.OnMenuItemClickListener {

    private AlbumViewerAdapter adapter;
    private AlbumViewerViewModel albumViewerViewModel;

    private int albumId;

    private String location;
    private String person;
    private boolean conjunction;

    /* https://developer.android.com/training/data-storage/shared/photopicker */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::savePhoto);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumViewerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AlbumViewerViewModel.INITIALIZER)
        ).get(AlbumViewerViewModel.class);

        LiveData<List<Photo>> photoListLiveData;

        albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) { /* Search Mode */
            location = getIntent().getStringExtra("location");
            person = getIntent().getStringExtra("person");
            conjunction = getIntent().getBooleanExtra("conjunction", true);
            photoListLiveData = albumViewerViewModel.getPhotoListLiveData(location, person, conjunction);
        } else { /* Album mode */
            photoListLiveData = albumViewerViewModel.getPhotoListLiveData(albumId);
        }

        setContentView(R.layout.activity_albumviewer);
        RecyclerView recyclerView = findViewById(R.id.rv_albumviewer);

        adapter = new AlbumViewerAdapter(this, photoListLiveData.getValue());
        recyclerView.setAdapter(adapter);

        registerForContextMenu(recyclerView);

        photoListLiveData.observe(this, album -> adapter.setPhotoList(album));

        if (albumId < 0) {
            findViewById(R.id.btn_albumviewer_add).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_albumviewer_add).setOnClickListener(this::showMenu);
        }

        findViewById(R.id.btn_albumviewer_back).setOnClickListener(v -> finish());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.getMenuInflater().inflate(R.menu.popm_albumviewer, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popm_albumviewer_gallery) {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            return true;
        }
        // } else if (id == R.id.popm_albumviewer_file) {
        //     // Stuff
        //     return true;
        // } else if (id == R.id.popm_albumviewer_url) {
        //     // Stuff
        //     return true;
        // }
        return false;
    }

    public void savePhoto(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, R.string.albumviewer_nulluri, Toast.LENGTH_SHORT).show();
            return;
        }
        /* Copy the photo locally */
        String fileName = PhotosFileUtils.getFileName(this, uri);
        Pair<String, File> destFile = PhotosFileUtils.getDest(this, fileName);
        PhotosFileUtils.copyFileToLocal(this, uri, destFile.first);

        /* Store in database */
        Uri destUri = Uri.fromFile(destFile.second);
        Photo photo = new Photo(destUri, albumId);
        albumViewerViewModel.addNewPhoto(photo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.cm_albumviewer, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cm_albumviewer_delete) {
            removePhotoDialog(adapter.getLastLongClickPhoto());
            return true;
        } else if (id == R.id.cm_albumviewer_move) {
            movePhotoDialog(adapter.getLastLongClickPhoto());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void removePhotoDialog(Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Photo?")
                .setMessage("Are you sure you want to delete photo '" + PhotosFileUtils.getFileName(this, photo.getUri()) + "'?")
                .setPositiveButton("Confirm", (dialogInterface, which) -> albumViewerViewModel.removePhoto(photo))
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void movePhotoDialog(Photo photo) {
        List<Album> albums = albumViewerViewModel.getAllAlbums();
        if (albums == null) return;
        albums.removeIf(album -> album.getAlbumInfo().getId() == photo.getAlbumId());

        AtomicInteger selected = new AtomicInteger(-1);
        String[] albumNames = albums.stream().map(Album::getName).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Move Photo to Album:")
                .setSingleChoiceItems(albumNames, selected.get(), ((dialog, which) -> selected.set(which)))
                .setPositiveButton("Move", (dialog, which) -> {
                    if (selected.get() == -1) return;
                    albumViewerViewModel.movePhoto(photo, albums.get(selected.get()));
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void viewImage(int entryPosition) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        if (albumId < 0) {
            intent.putExtra("location", location);
            intent.putExtra("person", person);
            intent.putExtra("conjunction", conjunction);
        } else {
            intent.putExtra("albumId", albumId);
        }
        intent.putExtra("entryPosition", entryPosition);
        this.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        albumViewerViewModel.onDestroy();
    }
}
