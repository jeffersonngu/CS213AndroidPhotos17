package com.photos.database;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public class SetStringConverter {

    @TypeConverter
    public static Set<String> toStringSet(String value) {
        Set<String> set = new HashSet<>();
        try {
            JSONArray jsonArray = new JSONArray(value);
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return set;
    }

    @TypeConverter
    public static String fromStringSet(Set<String> set) {
        return new JSONArray(set).toString();
    }
}
