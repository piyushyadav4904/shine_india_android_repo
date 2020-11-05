package com.app.theshineindia.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.changepassword.ChangePasswordActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;
import com.app.theshineindia.utils.Validator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Button btn_changepassword;
    TextView tv_plan, tv_activation_date, tv_expiry_date, tv_edit, tv_username, tv_mobile;
    EditText et_email, et_wa_no;
    Button btn_update;
    boolean isEditModeOn;
    List<View> all_view = new ArrayList<>();
    LinearLayout linear_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // set Toolbar
        setToolBar();

        initUI();

        setData();

        all_view = getAllChildren(linear_details);
        changeEditMode();
    }


    private void setToolBar() {
        findViewById(R.id.ib_tracker_back).setOnClickListener(view -> onBackPressed());
    }


    private void initUI() {
        btn_changepassword = findViewById(R.id.profile_btn_changepassword);
        tv_plan = findViewById(R.id.tv_plan);
        tv_activation_date = findViewById(R.id.tv_activation_date);
        tv_expiry_date = findViewById(R.id.tv_expiry_date);
        et_email = findViewById(R.id.et_email);
        et_wa_no = findViewById(R.id.et_wa_no);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_username = findViewById(R.id.tv_username);
        linear_details = findViewById(R.id.linear_details);
        tv_edit = findViewById(R.id.tv_edit);
        btn_update = findViewById(R.id.btn_update);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void changePassword(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }


    private void setData() {
        String user_data = SP.getStringPreference(this, SP.user_data);
        if (user_data == null) return;

        try {
            JSONObject jsonObject = new JSONObject(user_data);
            tv_username.setText(jsonObject.getString("name"));
            tv_mobile.setText(jsonObject.getString("mobile"));
            et_wa_no.setText(jsonObject.getString("alternat_mobile"));
            et_email.setText(jsonObject.getString("email"));
            tv_plan.setText(jsonObject.getString("plan_name"));
            tv_activation_date.setText(jsonObject.getString("activation_date"));
            tv_expiry_date.setText(jsonObject.getString("expiry_date"));

        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }

    public void onEditModeClicked(View view) {
        changeEditMode();
    }

    private void changeEditMode() {
        if (isEditModeOn) { // edit mode on
            Toast.makeText(this, "Edit mode on", Toast.LENGTH_SHORT).show();

            isEditModeOn = false;
            tv_edit.setText("EDIT");
            btn_update.setVisibility(View.VISIBLE);

            for (int i = 0; i < all_view.size(); i++) {
                if (all_view.get(i) instanceof EditText) {
                    EditText editText = (EditText) all_view.get(i);
                    editText.setEnabled(true);
                }
            }
        } else { // edit mode off -- read mode on
            Toast.makeText(this, "Read mode on", Toast.LENGTH_SHORT).show();

            isEditModeOn = true;
            tv_edit.setText("READ");
            btn_update.setVisibility(View.GONE);

            for (int i = 0; i < all_view.size(); i++) {
                if (all_view.get(i) instanceof EditText) {
                    EditText editText = (EditText) all_view.get(i);
                    editText.setEnabled(false);
                    editText.setTextColor(getResources().getColor(R.color.darkGray));
                }
            }
        }
    }

    private List<View> getAllChildren(View v) {
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            //Do not add any parents, just add child elements
            result.addAll(getAllChildren(child));
        }
        return result;
    }

    public void updateProfile(View view) {
        if (validateEveryField()) {
            new ProfilePresenter(this).requestUpload();
        }
    }

    private boolean validateEveryField() {
        if (!Validator.isEmailValid(this, et_email.getText().toString().trim())) {
            return false;
        } else if (!Validator.isMobileValid(this, et_wa_no.getText().toString().trim())) {
            return false;
        }
        return true;
    }
}
