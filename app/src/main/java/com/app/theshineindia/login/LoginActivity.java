package com.app.theshineindia.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.forgot_password.ForgotActivity;
import com.app.theshineindia.registration.RegistrationActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;
import com.app.theshineindia.utils.Validator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    AppCompatEditText et_username, et_password;
    TextView tv_createaccount, tv_forgotpassword;
    LoginPresenter presenter;
    Button btn_signin;
    String device_imei = "";
    ImageView iv_bg;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(LoginActivity.this);

        initUI();

        requestPermission();

        morePermission();
    }


    private void initUI() {
        et_username = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        tv_createaccount = findViewById(R.id.login_tv_createaccount);
        btn_signin = findViewById(R.id.login_btn_signin);
        tv_forgotpassword = findViewById(R.id.login_tv_forgotpassword);
        iv_bg = findViewById(R.id.iv_bg);
    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

//                            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//                            device_imei=telephonyManager.getDeviceId();

                            device_imei = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

                            String email = SP.getStringPreference(LoginActivity.this, SP.email);
                            String password = SP.getStringPreference(LoginActivity.this, SP.password);

                            Toast.makeText(LoginActivity.this, device_imei, Toast.LENGTH_SHORT).show();


                            if (email != null && password != null) {
                                iv_bg.setVisibility(View.VISIBLE);
                                presenter.login(email, password);
                            }
                        }

                        // check for permanent denial of any permission
                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(LoginActivity.this, "Please allow all permission to continue", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }


    public void morePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(this)) {
                    Alert.showError(LoginActivity.this, "If you won't allow this permission, App lock may nor work properly");
                }
            }
        }
    }


    public void loginClicked(View view) {
        String email = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (validateEveryField(email, password)) {
            SP.setStringPreference(this, SP.password, password);
            SP.setStringPreference(this, SP.prev_sim_count, String.valueOf(SharedMethods.getAvailableSimCount(this)));

            presenter.login(email, password);
        }
    }


    private boolean validateEveryField(String email, String password) {
//        if (!Validator.isEmailValid(this, email)) {
        if (email.isEmpty()) {
            Alert.showError(this, "Please enter email/phone no.");
            return false;

        } else if (!Validator.isPasswordValid(this, password)) {
            return false;
        }

        return true;
    }


    public void createAccount(View view) {
        Intent createAccountIntent = new Intent(this, RegistrationActivity.class);
        startActivity(createAccountIntent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotActivity.class);
        startActivity(intent);
    }
}
