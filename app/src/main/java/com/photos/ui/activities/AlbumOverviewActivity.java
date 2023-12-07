package com.photos.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.ui.adapters.AlbumOverviewAdapter;
import com.photos.viewmodels.AlbumOverviewViewModel;

public class AlbumOverviewActivity extends AppCompatActivity implements AlbumOverviewListener {

    private AlbumOverviewAdapter adapter;
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

        adapter = new AlbumOverviewAdapter(this, albumOverviewViewModel.getAlbumListLiveData().getValue());
        recyclerView.setAdapter(adapter);

        albumOverviewViewModel.getAlbumListLiveData().observe(this, albums -> adapter.setAlbumList(albums));

        Button addAlbumButton = findViewById(R.id.btn_albumoverview_add);
        addAlbumButton.setOnClickListener(l -> addAlbumDialog());

        registerForContextMenu(recyclerView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.cm_albumoverview, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cm_albumoverview_rename) {
            renameAlbumDialog(adapter.getLastLongClickAlbum());
            return true;
        } else if (id == R.id.cm_albumoverview_delete) {
            return true;
        }
        return false;
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

    public void renameAlbumDialog(Album album) {
        EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setMessage("Album Name")
                .setView(editText)
                .setPositiveButton("Submit", (dialogInterface, which) -> {
                    String input = editText.getText().toString();
                    albumOverviewViewModel.renameAlbum(album, input);
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
