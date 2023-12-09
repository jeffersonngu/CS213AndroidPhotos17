package com.photos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.MutableLiveData;

import com.photos.ui.activities.AlbumOverviewActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Splish Splash SplashScreen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MutableLiveData<Boolean> isReady = new MutableLiveData<>(true);
        assert isReady.getValue() != null;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            isReady.postValue(false);
            Intent intent = new Intent(this, AlbumOverviewActivity.class);
            startActivity(intent);
            finish();
        }, 3000L, TimeUnit.MILLISECONDS);

        SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(isReady::getValue);

    }
}