package com.app.theshineindia.utils;

import androidx.appcompat.app.AppCompatActivity;

import com.app.theshineindia.R;
import com.tapadoo.alerter.Alerter;

public class Alert {
    public static void showMessage(AppCompatActivity activity, String title, String message) {
        // If Alerter is already showing it will hide the alerter
        // and show again
        if (Alerter.isShowing()) {
            Alerter.hide();
        }
        Alerter.create(activity)
                .setTitle(title)
                .setText(message)
                .setIcon(R.drawable.launcher)
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(2000) //this method use to set the duration for the alerter (in milliseconds)
                .show();
    }

    public static void showMessage(AppCompatActivity activity, String message) {
        // If Alerter is already showing it will hide the alerter
        // and show again
        if (Alerter.isShowing()) {
            Alerter.hide();
        }
        Alerter.create(activity)
                .setText(message)
                .setIcon(R.mipmap.active)
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(2000) //this method use to set the duration for the alerter (in milliseconds)
                .show();
    }


    public static void showError(AppCompatActivity activity, String message) {
        // If Alerter is already showing it will hide the alerter
        // and show again
        if (Alerter.isShowing()) {
            Alerter.hide();
        }
        Alerter.create(activity)
                .setTitle("Error occurred")
                .setText(message)
                .setIcon(R.drawable.launcher)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(3000) //this method use to set the duration for the alerter (in milliseconds)
                .show();
    }

//    public void withIcon(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("Simple Alerter")
//                .setText("Alerter with Custom Icon")
//                .setIcon(android.R.drawable.ic_dialog_email)
//                .setIconColorFilter(0)
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setDuration(10000)
//                .show();
//    }
//
//    public void withOutTitle(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setText("Alerter Without Title")
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setDuration(10000)
//                .show();
//    }
//
//    public void withListener(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("New Message Received")
//                .setText("You Got 5 New Messages Received.\nClick to Open")
//                .setDuration(10000)
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(), "Clicked on Alerter", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .show();
//    }
//
//    public void withVerbose(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("5 New Messaged Received")
//                .setText("Hello!\n" +
//                        "How are you?\n" +
//                        "What you doing today?\n" +
//                        "Can we go for dinner today?\n" +
//                        "Call me when you are free")
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .show();
//    }
//
//    public void withAnim(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("Alerter")
//                .setText("Alerter with enter exit animation")
//                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
//                .setExitAnimation(R.anim.alerter_slide_out_to_right)
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setDuration(10000)
//                .show();
//    }
//
//    public void swipeDismiss(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("Alerter")
//                .setText("Swipe to Dismiss Alerter")
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setDuration(10000)
//                .enableSwipeToDismiss()
//                .enableIconPulse(true)
//                .show();
//    }
//
//    public void withProgress(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("Alerter")
//                .setText("Alert with Progress")
//                .setDuration(10000)
//                .enableProgress(true)
//                .setProgressColorRes(R.color.colorPrimary)
//                .show();
//    }
//
//    public void withButton(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//        Alerter.create(this)
//                .setTitle("Alerter")
//                .setText("Alerter With Buttons")
//                .addButton("Ok", R.style.AlertButton, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(), "Ok is Clicked", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addButton("Cancel", R.style.AlertButton, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(), "Cancel is Clicked", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .setDuration(10000)
//                .show();
//    }
//
//    public void customFont(View view) {
//        if (Alerter.isShowing()) {
//            Alerter.hide();
//        }
//
//        /*paste your font in assets/fonts path*/
//        Alerter.create(this)
//                .setTitle("Alerter")
//                .setTitleAppearance(R.style.AlertTextAppearance_Title)
//                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/samplefont.otf"))    // font path in assets
//                .setText("Alerter Appears Here")
//                .setTextAppearance(R.style.AlertTextAppearance_Text)
//                .setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/samplefont.otf"))
//                .setDuration(10000)
//                .setBackgroundColorRes(R.color.colorPrimary)
//                .show();
//    }
}
