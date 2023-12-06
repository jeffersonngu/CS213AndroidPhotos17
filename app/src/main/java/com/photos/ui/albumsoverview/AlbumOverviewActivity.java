package com.photos.ui.albumsoverview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.ui.albumviewer.AlbumViewerActivity;
import com.photos.viewmodels.AlbumOverviewViewModel;

public class AlbumOverviewActivity extends AppCompatActivity {

    private AlbumListAdapter adapter;
    private AlbumOverviewViewModel albumOverviewViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumOverviewViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AlbumOverviewViewModel.INITIALIZER)
        ).get(AlbumOverviewViewModel.class);

        setContentView(R.layout.activity_albumoverview);
        RecyclerView recyclerView = findViewById(R.id.rv_albumoverview);

        albumOverviewViewModel.addNewAlbum(new Album("Meep")); // Test
        albumOverviewViewModel.addNewAlbum(new Album("Moop")); // Test

        adapter = new AlbumListAdapter(this, albumOverviewViewModel.getAlbumListLiveData().getValue());
        recyclerView.setAdapter(adapter);

        albumOverviewViewModel.getAlbumListLiveData().observe(this, albums -> adapter.setAlbumList(albums));

        Button addAlbumButton = findViewById(R.id.btn_albumoverview_add);
        addAlbumButton.setOnClickListener(l -> addAlbumDialog());
    }

    private void addAlbumDialog() {
        EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Album")
                .setMessage("Album Name")
                .setView(editText)
                .setPositiveButton("Submit", (dialogInterface, which) -> {
                    String input = editText.getText().toString();
                    albumOverviewViewModel.addNewAlbum(new Album(input));
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void viewAlbum(Album album) {
        Intent intent = new Intent(this, AlbumViewerActivity.class);
        intent.putExtra("albumId", album.getAlbumInfo().getId());
        this.startActivity(intent);
    }
}
