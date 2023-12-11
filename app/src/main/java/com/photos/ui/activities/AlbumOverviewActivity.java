package com.photos.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.models.Album;
import com.photos.models.Photo;
import com.photos.ui.adapters.AlbumOverviewAdapter;
import com.photos.util.PhotosViewUtils;
import com.photos.viewmodels.AlbumOverviewViewModel;

import java.util.List;
import java.util.Objects;

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

        ImageButton searchTagButton = findViewById(R.id.ibtn_albumoverview_search);
        searchTagButton.setOnClickListener(l -> searchPhotosDialog());
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
            if (input.isBlank()) {
                alertDialog.setMessage("Only non blank names allowed");
            } else if (!input.matches("^[a-zA-Z0-9]*")) {
                alertDialog.setMessage("Only alphanumeric (letters and numbers no whitespaces) names allowed");
            } else {
                Boolean result = albumOverviewViewModel.addNewAlbum(new Album(input));
                if (result == null) {
                    alertDialog.setMessage("Timed Out: The Album may or may not have been created");
                } else if (!result) {
                    alertDialog.setMessage("That Album name already exists");
                } else {
                    Toast.makeText(this, "Successfully created Album " + input, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
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
            if (input.isBlank()) {
                alertDialog.setMessage("Only non blank names allowed");
            } else if (!input.matches("^[a-zA-Z0-9]*")) {
                alertDialog.setMessage("Only alphanumeric (letters and numbers no whitespaces) names allowed");
            } else {
                Boolean result = albumOverviewViewModel.renameAlbum(album, input);
                if (result == null) {
                    alertDialog.setMessage("Timed Out: The Album may or may not have been renamed");
                } else if (!result) {
                    alertDialog.setMessage("That Album name already exists");
                } else {
                    Toast.makeText(this, "Successfully renamed Album to " + input, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
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

    public void searchPhotosDialog() {
        View searchTagView = getLayoutInflater().inflate(R.layout.layout_tagsearch, null);

        List<Album> albumList = adapter.getCurrentAlbumList();
        String[] locationArray = albumList.stream()
                .flatMap(album -> album.getPhotoList().stream())
                .map(Photo::getLocation)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(String[]::new);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationArray);
        AutoCompleteTextView locationAutoCompleteTextView = searchTagView.findViewById(R.id.actv_tagsearch_location);
        locationAutoCompleteTextView.setAdapter(locationAdapter);

        String[] peopleArray = albumList.stream()
                .flatMap(album -> album.getPhotoList().stream())
                .flatMap(photo -> photo.getPeople().stream())
                .distinct()
                .toArray(String[]::new);
        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, peopleArray);
        AutoCompleteTextView personAutoCompleteTextView = searchTagView.findViewById(R.id.actv_tagsearch_person);
        personAutoCompleteTextView.setAdapter(personAdapter);

        RadioGroup radioGroup = searchTagView.findViewById(R.id.rg_tagsearch);

        new AlertDialog.Builder(this)
                .setTitle("Search Photos")
                .setView(searchTagView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    Intent intent = new Intent(this, AlbumViewerActivity.class);
                    intent.putExtra("location", locationAutoCompleteTextView.getText().toString());
                    intent.putExtra("person", personAutoCompleteTextView.getText().toString());
                    intent.putExtra("conjunction", radioGroup.getCheckedRadioButtonId() == R.id.rb_tagsearch_and);
                    this.startActivity(intent);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        albumOverviewViewModel.onDestroy();
    }
}
