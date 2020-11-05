package com.app.theshineindia.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.theshineindia.R;
import com.app.theshineindia.registration.RegistrationActivity;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.Validator;

public class ForgotActivity extends AppCompatActivity {
    EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        // set Toolbar
        setToolBar();

        initUI();
    }

    private void setToolBar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }


    private void initUI() {
        et_email = findViewById(R.id.et_email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void onSendClick(View view) {
        if (validateEveryField()) {
            new ForgotPresenter(ForgotActivity.this).requestForgotPassword();
        }
    }

    private boolean validateEveryField() {
        return Validator.isEmailValid(this, et_email.getText().toString().trim());
    }
}

