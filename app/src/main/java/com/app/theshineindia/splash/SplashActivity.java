package com.app.theshineindia.splash;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.theshineindia.R;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.splash.versionutils.Const;
import com.app.theshineindia.splash.versionutils.VersionListener;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements VersionListener {
    String currentVersion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        checkVersion();
    }


    private void checkVersion() {
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv_version = findViewById(R.id.tv_version);
            tv_version.setText("Version " + currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new GetVersionCode(this).execute();

        //gotoNextScreen(2000);
    }

    @Override
    public void onGetResponse(String onlineVersion) {
        Log.d("1111", "Current version " + currentVersion + "......playstore version " + onlineVersion);
        if (currentVersion.compareTo(onlineVersion) >= 0) {
            gotoNextScreen(2000);
        } else {
            showUpdateDialog();
        }

    }

    private void showUpdateDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new version to continue use")
                .setCancelable(false)
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getPackageName())));
                        dialogInterface.dismiss();
                        finish();
                    }
                }).setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoNextScreen(1000);
                        // finish();
                    }
                }).create();
        dialog.show();
    }


    public static class GetVersionCode extends AsyncTask<Void, String, String> {
        private VersionListener listener;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        GetVersionCode(VersionListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.e("inside", "doInBackground");
            String newVersion = null;

            try {
                Document document = Jsoup.connect(Const.LINK)
                        .timeout(30000)
                        .userAgent(Const.USER_AGENT)
                        .referrer(Const.REFERRER)
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText(Const.CURRENT_VERSION);
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                //Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)
                listener.onGetResponse(onlineVersion);
            }
        }

    }


    @SuppressLint("HandlerLeak")
    private void gotoNextScreen(long delay) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                IntentController.sendIntent(SplashActivity.this, LoginActivity.class);
                finish();
            }
        }.sendEmptyMessageDelayed(0, delay);
    }


}
