package com.photos.database;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverter {

    /**
     * Since we enforced a URI as NonNull, we do not have to worry about Uri::parse
     * @param value Converted String
     * @return URI of Photo
     */
    @TypeConverter
    public static Uri toUri(String value) {
        return Uri.parse(value);
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
        return uri.toString();
    }
}
