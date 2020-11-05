package com.app.theshineindia.location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.app.theshineindia.R;
import com.app.theshineindia.services.SingleService;
import com.app.theshineindia.utils.SP;

public class LocationTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test);

        TextView tv_lat = findViewById(R.id.tv_lat);
        TextView tv_long = findViewById(R.id.tv_long);

        tv_lat.setText("Latitude : " + SP.getStringPreference(this, SP.last_latitude));
        tv_long.setText("Longitude : " + SP.getStringPreference(this, SP.last_longitude));
    }
}
