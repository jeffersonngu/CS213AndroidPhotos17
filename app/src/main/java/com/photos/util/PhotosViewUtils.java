package com.photos.util;

import android.text.InputFilter;
import android.widget.EditText;

public class PhotosViewUtils {

    public static void addEditTextFilter(EditText editText, InputFilter inputFilter) {
        InputFilter[] editFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = inputFilter;
        editText.setFilters(newFilters);
    }
}
