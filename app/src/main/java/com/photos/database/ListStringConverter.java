package com.photos.database;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ListStringConverter {

    @TypeConverter
    public static List<String> toStringList(String value) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(value);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return new JSONArray(list).toString();
    }
}
