package com.photos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.photos.albumsoverview.AlbumOverviewActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // viewModel = new ViewModelProvider(this).get(MainActivity.MainViewModel.class);

        // SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        // splashScreen.setKeepOnScreenCondition(() -> Boolean.FALSE.equals(viewModel.getIsReady().getValue()));

        // setContentView(R.layout.activity_welcome);

        Intent intent = new Intent(this, AlbumOverviewActivity.class);
        startActivity(intent);
        finish();
    }

    public static class MainViewModel extends ViewModel {
        private final MutableLiveData<Boolean> isReady = new MutableLiveData<>(false);

        public MainViewModel() {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> isReady.postValue(true), 3000L, TimeUnit.MILLISECONDS);
        }

        public MutableLiveData<Boolean> getIsReady() {
            return isReady;
        }
    }
}