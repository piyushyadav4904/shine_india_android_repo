package com.app.theshineindia.changepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;

import android.view.View;

import com.app.theshineindia.R;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.Validator;

public class ChangePasswordActivity extends AppCompatActivity {
    AppCompatEditText et_old_password, et_new_password, et_conf_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // set Toolbar
        setToolBar();

        initUI();
    }

    private void setToolBar() {
        findViewById(R.id.ib_changepassword_back).setOnClickListener(view -> onBackPressed());
    }


    private void initUI() {
        et_old_password = findViewById(R.id.et_old_password);
        et_new_password = findViewById(R.id.et_new_password);
        et_conf_password = findViewById(R.id.et_confirm_password);
    }


    public void onChangePasswordClicked(View view) {
        if (validateEveryField()) {
            new ChangePasswordPresenter(ChangePasswordActivity.this).requestCP();
        }
    }

    private boolean validateEveryField() {
        if (!Validator.isPasswordValid(this, et_old_password.getText().toString().trim())) {
            Alert.showError(this, "Old password : not valid");
            return false;

        } else if (!Validator.isPasswordValid(this, et_new_password.getText().toString().trim())) {
            Alert.showError(this, "New password : not valid");
            return false;

        } else if (!Validator.isPasswordValid(this, et_conf_password.getText().toString().trim())) {
            Alert.showError(this, "Confirm new password : not valid");
            return false;

        } else if (!et_new_password.getText().toString().trim().equals(et_conf_password.getText().toString().trim())) {
            Alert.showError(this, "Password and confirm password should match");
            return false;
        }
        return true;
    }


}
