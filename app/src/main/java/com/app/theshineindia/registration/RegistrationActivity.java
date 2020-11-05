package com.app.theshineindia.registration;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.theshineindia.Plan.SelectPlanActivity;
import com.app.theshineindia.R;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.Animator;
import com.app.theshineindia.utils.Validator;


public class RegistrationActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    EditText et_email, et_mobile, et_password, et_conf_password, et_wa_no, et_plan, et_secret_code, et_username, et_vendor_id;
    CheckBox cb_terms;
    Button btn_submit;
    String plan_id = "";
    String imeiId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // set Toolbar
        setToolBar();

        initUI();
    }

    private void setToolBar() {
        findViewById(R.id.ib_plan_back).setOnClickListener(view -> onBackPressed());
    }

    private void initUI() {
        et_email = findViewById(R.id.et_email);
        et_mobile = findViewById(R.id.et_mobile);
        et_password = findViewById(R.id.et_password);
        et_conf_password = findViewById(R.id.et_conf_pass);
        et_plan = findViewById(R.id.et_plan);
        et_wa_no = findViewById(R.id.et_wa_no);
        et_secret_code = findViewById(R.id.et_secret_code);
        cb_terms = findViewById(R.id.cb_terms);
        btn_submit = findViewById(R.id.register_btn_submit);
        et_username = findViewById(R.id.et_username);
        et_vendor_id = findViewById(R.id.et_vendor_id);

        String imeiId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Toast.makeText(this, imeiId, Toast.LENGTH_SHORT).show();

    }


    public void viewPlan(View view) {
        Animator.buttonAnim(this, view);

        Intent intent = new Intent(this, SelectPlanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                plan_id = data.getStringExtra("plan_id");
                et_plan.setText(data.getStringExtra("plan_name"));
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void onRegistrationClick(View view) {
        if (validateEveryField()) {
            new RegistrationPresenter(RegistrationActivity.this).requestRegistration();
        }
    }

    private boolean validateEveryField() {

        if (!Validator.isNameValid(this, et_username.getText().toString().trim())) {
            return false;

        } else if (!Validator.isMobileValid(this, et_mobile.getText().toString().trim())) {
            return false;

        } else if (!Validator.isMobileValid(this, et_wa_no.getText().toString().trim())) {
            return false;

        } else if (!Validator.isEmailValid(this, et_email.getText().toString().trim())) {
            return false;

        } else if (!Validator.isPasswordValid(this, et_password.getText().toString().trim())) {
            return false;

        } else if (!Validator.isPasswordValid(this, et_conf_password.getText().toString().trim())) {
            return false;

        } else if (!et_password.getText().toString().trim().equals(et_conf_password.getText().toString().trim())) {
            Alert.showError(this, "Passwords and confirm password did not match");
            return false;

        } else if (plan_id == null || plan_id.isEmpty()) {
            Alert.showError(this, "Please select your plan");
            return false;

        } else if (et_secret_code.getText().toString().trim().length() != 6) {
            Alert.showError(this, "Please enter 6 digit secret code");
            return false;

        } else if (et_vendor_id.getText().toString().trim().isEmpty()) {
            Alert.showError(this, "Please enter the vendor id");
            return false;

        } else if (!cb_terms.isChecked()) {
            Alert.showError(this, "Please accept the terms and condition");
            return false;
        }

        return true;
    }

}