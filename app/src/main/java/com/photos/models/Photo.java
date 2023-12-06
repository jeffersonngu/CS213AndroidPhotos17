package com.photos.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "photos", foreignKeys = {
        @ForeignKey(entity = Album.AlbumInfo.class, parentColumns = {"id"}, childColumns = {"albumId"},
                onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)
    }, indices = {
        @Index(value = "uri", unique = true),
        @Index(value = "albumId")
})
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private final Uri uri;

    @ColumnInfo
    private int albumId;

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

    public Photo(int id, @NonNull Uri uri, String location, List<String> people, int albumId) {
        this.id = id;
        this.uri = uri;
        this.location = location;
        this.people = people;
        this.albumId = albumId;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public Uri getUri() { return uri; }

    /**
     * The id of the Album that owns the Photo
     * @return The foreign key "albumId"
     */
    public int getAlbumId() { return albumId; }

    public String getLocation() { return location; }

    public List<String> getPeople() { return people; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Photo otherPhoto) {
            return this.getId() == otherPhoto.getId()
                    && this.getUri().equals(otherPhoto.getUri());
        }
        return false;
    }
}
