package com.photos.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.photos.util.PhotosViewUtils;
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

        adapter = new AlbumOverviewAdapter(this, albumOverviewViewModel.getAlbumListLiveData().getValue());
        recyclerView.setAdapter(adapter);

        registerForContextMenu(recyclerView);

        albumOverviewViewModel.getAlbumListLiveData().observe(this, albums -> adapter.setAlbumList(albums));

        Button addAlbumButton = findViewById(R.id.btn_albumoverview_add);
        addAlbumButton.setOnClickListener(l -> addAlbumDialog());
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
            removeAlbumDialog(adapter.getLastLongClickAlbum());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void addAlbumDialog() {
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        PhotosViewUtils.addEditTextFilter(editText, new InputFilter.LengthFilter(20));
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Album")
                .setMessage("Album Name")
                .setView(editText)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();

        /* Custom handler for Submit */
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String input = editText.getText().toString();
            if (input.matches("^[a-zA-Z0-9]*")) {
                Boolean result = albumOverviewViewModel.addNewAlbum(new Album(input));
                if (result == null) {
                    alertDialog.setMessage("Timed Out: The Album may or may not have been created");
                } else if (!result) {
                    alertDialog.setMessage("That Album name already exists");
                } else {
                    alertDialog.dismiss();
                }
            } else {
                alertDialog.setMessage("Only alphanumeric (letters and numbers no whitespaces) names allowed");
            }
        });
    }

    public void renameAlbumDialog(Album album) {
        EditText editText = new EditText(this);
        editText.setText(album.getName());
        editText.setMaxLines(1);
        PhotosViewUtils.addEditTextFilter(editText, new InputFilter.LengthFilter(20));
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setMessage("Album Name")
                .setView(editText)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();

        /* Custom handler for Submit */
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String input = editText.getText().toString();
            if (input.matches("^[a-zA-Z0-9]*")) {
                Boolean result = albumOverviewViewModel.renameAlbum(album, input);
                if (result == null) {
                    alertDialog.setMessage("Timed Out: The Album may or may not have been renamed");
                } else if (!result) {
                    alertDialog.setMessage("That Album name already exists");
                } else {
                    alertDialog.dismiss();
                }
            } else {
                alertDialog.setMessage("Only alphanumeric (letters and numbers no whitespaces) names allowed");
            }
        });
    }

    public void removeAlbumDialog(Album album) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Album?")
                .setMessage("Are you sure you want to delete album '" + album.getName() + "' and all its photos?")
                .setPositiveButton("Confirm", (dialogInterface, which) -> albumOverviewViewModel.removeAlbum(album))
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
