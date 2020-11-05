package com.app.theshineindia.device_scan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.narayanacharya.waveview.WaveView;
import com.yangp.ypwaveview.YPWaveView;

import java.util.ArrayList;
import java.util.List;


public class AppListActivity extends AppCompatActivity {
    RecyclerView rv_app_list;
    AppListAdapter mAdapter;
    List<App> app_list = new ArrayList<>();

    RelativeLayout RL_wave_view;
    YPWaveView yp_waveView;
    WaveView waveView;
    TextView tv_app;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        getInstalledAppList();

        setToolbar();

        initUI();

        setupRecyclerView();
    }

    private void getInstalledAppList() {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }


    private void initUI() {
        RL_wave_view = findViewById(R.id.RL_wave_view);
        rv_app_list = findViewById(R.id.rv_app_list);
        yp_waveView = findViewById(R.id.YPWaveView);
        waveView = findViewById(R.id.waveView);
        tv_app = findViewById(R.id.tv_app);

        waveView.play();
    }

    private void setToolbar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            app_list = new ApkInfoExtractor(AppListActivity.this).GetAllInstalledApkInfo();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        startScan(SharedMethods.getRandomNumberInRange(40, 50) * 1000);
    }

    private void startScan(long scan_time) {
        count = app_list.size();

        new CountDownTimer(scan_time, 200) {

            public void onTick(long millisUntilFinished) {
                long d1 = scan_time - millisUntilFinished;
                double d2 = (double) d1 / (double) scan_time;
                int d3 = (int) (d2 * 1000);

                yp_waveView.setProgress(d3);

                if (app_list.size() > 0) {
                    if (count < app_list.size()) {
                        tv_app.setText(app_list.get(count).getPackage_name());
                        count++;
                    } else {
                        count = 0;
                    }
                }
            }

            public void onFinish() {
                waveView.pause();
                yp_waveView.setProgress(1000);
                count = 0;

                tv_app.setTextSize(20);
                tv_app.setText("Device scanned successfully");

                new Handler().postDelayed(() -> {
                    RL_wave_view.setVisibility(View.GONE);
                    setAdapter();
                },2000);
            }
        }.
                start();
    }


    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_app_list.setLayoutManager(linearLayoutManager);
    }

    public void setAdapter() {
        mAdapter = new AppListAdapter(app_list, this);
        rv_app_list.setAdapter(mAdapter);
    }
}
