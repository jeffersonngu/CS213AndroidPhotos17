package com.photos.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "photos")
public class Photo {

    @NonNull
    @PrimaryKey
    private final Uri uri;

    /**
     * Location tag type
     */
    @ColumnInfo
    private String location;

    /**
     * Person tag type
     */
    @ColumnInfo
    private List<String> people = new ArrayList<>();

    /**
     * Main constructor
     * @param uri URI of Photo
     */
    @Ignore
    public Photo(@NonNull Uri uri) {
        this.uri = uri;
    }

    public Photo(@NonNull Uri uri, String location, List<String> people) {
        this.uri = uri;
        this.location = location;
        this.people = people;
    }

    @NonNull
    public Uri getUri() { return uri; }

    public String getLocation() { return location; }

    public List<String> getPeople() { return people; }
}
