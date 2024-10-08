package com.photos.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.HashSet;
import java.util.Set;

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
    @ColumnInfo
    private final Uri uri;

    @ColumnInfo
    private final int albumId;

    /**
     * Location tag type
     */
    @Nullable
    @ColumnInfo
    private String location;

    /**
     * Person tag type
     */
    @ColumnInfo
    private final Set<String> people;

    /**
     * Main constructor
     * @param uri URI of Photo
     */
    @Ignore
    public Photo(@NonNull Uri uri, int albumId) {
        this.uri = uri;
        this.albumId = albumId;
        this.people = new HashSet<>();
    }

    public Photo(int id, @NonNull Uri uri, @Nullable String location, Set<String> people, int albumId) {
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

    @Nullable
    public String getLocation() { return location; }

    public Set<String> getPeople() { return people; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Photo otherPhoto) {
            return this.getId() == otherPhoto.getId()
                    && this.getUri().equals(otherPhoto.getUri());
        }
        return false;
    }
}
