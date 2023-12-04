package com.photos.albumsoverview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumOverviewActivity extends AppCompatActivity {

    private List<AlbumModel> albumList;
    private AlbumListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_overview);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        albumList = new ArrayList<>();
        albumList.add(new AlbumModel("Meep")); // Test
        // albumList.add(new AlbumModel("Moop")); // Test

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AlbumListAdapter(albumList);
        recyclerView.setAdapter(adapter);

        Button addAlbumButton = findViewById(R.id.button);
        addAlbumButton.setOnClickListener(l -> addAlbumDialog());
    }

    private void addAlbumDialog() {
        EditText editText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Album:")
                .setView(editText)
                .setPositiveButton("Submit", (dialogInterface, i) -> {
                    String input = editText.getText().toString();
                    albumList.add(new AlbumModel(input));
                    adapter.notifyItemInserted(albumList.size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
