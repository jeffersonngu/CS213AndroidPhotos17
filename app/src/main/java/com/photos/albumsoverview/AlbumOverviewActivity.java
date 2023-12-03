package com.photos.albumsoverview;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_overview);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<String> albumList = new ArrayList<>();
        albumList.add("Meep");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AlbumListAdapter adapter = new AlbumListAdapter(albumList);
        recyclerView.setAdapter(adapter);

        Button addAlbumButton = findViewById(R.id.button);
        addAlbumButton.setOnClickListener(l -> addAlbumDialog());
    }

    private void addAlbumDialog() {

    }
}
