package com.app.theshineindia.notification;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.app.theshineindia.R;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView rv_notification;
    ArrayList<Notification> notification_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        // set Toolbar
        initUI();

        // set Toolbar
        setToolBar();

        setRecyclerView();

        //new NotificationPresenter(this).requestNotifications();

        setAdapter();
    }


    private void initUI() {
        rv_notification = findViewById(R.id.rv_notification);
    }

    private void setToolBar() {
        AppCompatTextView tv_tool_title = findViewById(R.id.tv_app);
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.title_notification));

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_notification.setLayoutManager(mLayoutManager);
    }

    public void setAdapter() {
        Notification notification = new Notification("1","This is test notification", "","9th september 2019");
        for (int i = 0; i < 20; i++) {
            notification_list.add(notification);
        }

        NotificationAdapter mAdapter = new NotificationAdapter(notification_list);
        rv_notification.setAdapter(mAdapter);
        //mAdapter.setClickListener(this);
    }
}
