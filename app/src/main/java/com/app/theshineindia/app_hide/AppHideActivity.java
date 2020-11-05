package com.app.theshineindia.app_hide;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.device_scan.ApkInfoExtractor;
import com.app.theshineindia.device_scan.App;

import java.util.ArrayList;
import java.util.List;


public class AppHideActivity extends AppCompatActivity {
    RecyclerView rv_app_list;
    AppHideAdapter mAdapter;
    List<App> app_list = new ArrayList<>();
    TextView tv_app;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_hide);

        setToolbar();

        initUI();

        setupRecyclerView();

        getInstalledAppList();
    }

    private void getInstalledAppList() {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }


    private void initUI() {
        rv_app_list = findViewById(R.id.rv_app_list);
        tv_app = findViewById(R.id.tv_app);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setToolbar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            app_list = new ApkInfoExtractor(AppHideActivity.this).GetAllInstalledApkInfo();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressBar.setVisibility(View.GONE);
            setAdapter();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }


    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_app_list.setLayoutManager(linearLayoutManager);
    }

    public void setAdapter() {
        mAdapter = new AppHideAdapter(app_list, this);
        rv_app_list.setAdapter(mAdapter);
    }
}
