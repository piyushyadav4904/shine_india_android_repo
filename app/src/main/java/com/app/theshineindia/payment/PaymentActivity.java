package com.app.theshineindia.payment;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.theshineindia.R;

public class PaymentActivity extends AppCompatActivity {

    ConstraintLayout layoutBottomSheet;
    Button btn_paynow;

    BottomSheetBehavior sheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setToolbar();

        initUI();
        bottomSheetListner();



    }

    private void bottomSheetListner() {



        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });



    }

    private void initUI() {

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        btn_paynow = findViewById(R.id.payment_btn_paynow);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
    }

    private void setToolbar() {

        findViewById(R.id.ib_payment_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void payNow(View view) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            btnBottomSheet.setText("Expand sheet");
        }

    }
}
