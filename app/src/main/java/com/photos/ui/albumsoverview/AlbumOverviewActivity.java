package com.photos.ui.albumsoverview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.viewmodels.PhotosViewModel;
import com.photos.R;
import com.photos.domain.Album;

public class AlbumOverviewActivity extends AppCompatActivity {

    private AlbumListAdapter adapter;

    private PhotosViewModel photosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photosViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(PhotosViewModel.initializer)
        ).get(PhotosViewModel.class);

        setContentView(R.layout.activity_albumoverview);

        RecyclerView recyclerView = findViewById(R.id.rv_albumoverview);

        photosViewModel.addNewAlbum(new Album("Meep")); // Test
        photosViewModel.addNewAlbum(new Album("Moop")); // Test

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AlbumListAdapter(photosViewModel.getAlbumListLiveData().getValue());
        recyclerView.setAdapter(adapter);

        photosViewModel.getAlbumListLiveData().observe(this, albums -> adapter.setAlbumList(albums));

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
                    photosViewModel.addNewAlbum(new Album(input));
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
