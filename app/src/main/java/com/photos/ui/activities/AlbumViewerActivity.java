package com.photos.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.models.Photo;
import com.photos.ui.adapters.AlbumViewerAdapter;
import com.photos.util.PhotoFileUtil;
import com.photos.viewmodels.AlbumViewerViewModel;

import java.io.File;

public class AlbumViewerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private AlbumViewerAdapter adapter;
    private AlbumViewerViewModel albumViewerViewModel;

    private int albumId;

    /* https://developer.android.com/training/data-storage/shared/photopicker */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::savePhoto);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) finish();

        albumViewerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AlbumViewerViewModel.INITIALIZER)
        ).get(AlbumViewerViewModel.class);

        setContentView(R.layout.activity_albumviewer);
        RecyclerView recyclerView = findViewById(R.id.rv_albumviewer);

        LiveData<Album> albumLiveData = albumViewerViewModel.getAlbum(albumId);

        adapter = new AlbumViewerAdapter(this, albumLiveData.getValue());
        recyclerView.setAdapter(adapter);

        albumLiveData.observe(this, album -> adapter.setAlbum(album));

        findViewById(R.id.btn_albumviewer_add).setOnClickListener(this::showMenu);
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
        } else if (id == R.id.popm_albumviewer_file) {
            // Stuff
            return true;
        } else if (id == R.id.popm_albumviewer_url) {
            // Stuff
            return true;
        }
        return false;
    }

    public void savePhoto(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, R.string.albumviewer_nulluri, Toast.LENGTH_SHORT).show();
            return;
        }
        /* Copy the photo locally */
        String fileName = PhotoFileUtil.getFileName(this, uri);
        Pair<String, File> destFile = PhotoFileUtil.getDest(this, fileName);
        PhotoFileUtil.copyFileToLocal(this, uri, destFile.first);

        /* Store in database */
        Uri destUri = Uri.fromFile(destFile.second);
        Photo photo = new Photo(destUri, albumId);
        albumViewerViewModel.addNewPhoto(photo);
    }
}
