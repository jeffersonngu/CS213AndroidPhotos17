package com.photos.albumviewer;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Photo {

    private final Uri uri;

    /**
     * Location tag type
     */
    private String location;

    /**
     * Person tag type
     */
    private final List<String> people = new ArrayList<>();

    public Photo(Uri uri) {
        this.uri = uri;
    }

    public String getLocation() { return location; }

    public void setLocation(String newLocation) { this.location = newLocation; }

    public List<String> getPeople() { return people; }
}
