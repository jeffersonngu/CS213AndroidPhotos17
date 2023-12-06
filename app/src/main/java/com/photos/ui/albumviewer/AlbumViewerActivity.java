package com.photos.ui.albumviewer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AlbumViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int albumId = getIntent().getIntExtra("albumId", -1);
        if (albumId < 0) finish();


    }
}
