package com.photos.albumsoverview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.Photos;
import com.photos.R;

public class AlbumOverviewActivity extends AppCompatActivity {

    private AlbumListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumoverview);

        RecyclerView recyclerView = findViewById(R.id.rv_albumoverview);

        Photos.getAlbumList().add(new Album("Meep")); // Test
        Photos.getAlbumList().add(new Album("Moop")); // Test

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AlbumListAdapter();
        recyclerView.setAdapter(adapter);

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
                    Photos.getAlbumList().add(new Album(input));
                    adapter.notifyItemInserted(Photos.getAlbumList().size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
