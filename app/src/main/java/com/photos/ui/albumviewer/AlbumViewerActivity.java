package com.photos.ui.albumviewer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.viewmodels.AlbumViewerViewModel;

public class AlbumViewerActivity extends AppCompatActivity {

    private AlbumViewerAdapter adapter;
    private AlbumViewerViewModel albumViewerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int albumId = getIntent().getIntExtra("albumId", -1);
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
    }
}
