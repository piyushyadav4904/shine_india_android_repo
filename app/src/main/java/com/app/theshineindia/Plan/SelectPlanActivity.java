package com.app.theshineindia.Plan;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.sos.SOSAdapter;
import com.app.theshineindia.utils.Animator;

import java.util.ArrayList;

public class SelectPlanActivity extends AppCompatActivity {

    LinearLayout LL_plan_list;
    ArrayList<Plan> plan_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_plan);
        setToolBar();

        initUI();

        new PlanPresenter(this).requestPlanList();
    }

    private void initUI() {
        LL_plan_list = findViewById(R.id.LL_plan_list);
    }


    private void setToolBar() {
        findViewById(R.id.ib_plan_back).setOnClickListener(view -> onBackPressed());
    }


    public void setPlanList() {
        if (plan_list.size() == 0) {
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout mainLayout = findViewById(R.id.LL_plan_list);
        mainLayout.removeAllViews();

        for (Plan plan : plan_list) {
            View myLayout = inflater.inflate(R.layout.row_plan_list, mainLayout, false);

            TextView tv_plane = myLayout.findViewById(R.id.tv_plane);
            tv_plane.setText(plan.getName());

            myLayout.setOnClickListener(v -> {
                Animator.buttonAnim(this, myLayout);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("plan_name", plan.getName());
                returnIntent.putExtra("plan_id", plan.getId());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            });

            // add our custom layout to the main layout
            mainLayout.addView(myLayout);
        }
    }
}
