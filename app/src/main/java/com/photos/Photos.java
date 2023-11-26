package com.photos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Photos extends AppCompatActivity {

    private EditText fval;
    private EditText cval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // get edit text fields
        fval = findViewById(R.id.fval);
        cval = findViewById(R.id.cval);
    }

    public void ftoc(View view) {
        try {
            float fv = Float.parseFloat(fval.getText().toString());
            float cv = (fv - 32) * 5 / 9;
            cval.setText(String.format("%5.1f", cv));
        } catch (Exception e) {
            Toast.makeText(Photos.this, "Input is empty", Toast.LENGTH_LONG).show();
        }
    }

    public void ctof(View view) {
        try {
            float cv = Float.parseFloat(cval.getText().toString());
            float fv = cv*9/5+32;
            fval.setText(String.format("%5.1f", fv));
        } catch (Exception e) {
            Toast.makeText(Photos.this, "Input is empty", Toast.LENGTH_LONG).show();
        }
    }
}