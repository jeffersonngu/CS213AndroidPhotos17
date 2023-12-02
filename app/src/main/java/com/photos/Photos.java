package com.photos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.photos.albumsoverview.AlbumListAdapter;

import java.util.ArrayList;
import java.util.List;

public class Photos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<String> albumList = new ArrayList<>();
        albumList.add("Meep");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AlbumListAdapter adapter = new AlbumListAdapter(albumList);
        recyclerView.setAdapter(adapter);
    }
}