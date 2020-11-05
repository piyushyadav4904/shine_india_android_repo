package com.app.theshineindia.device_scan;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.utils.Animator;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {
    List<App> list;
    Context context1;

    public AppListAdapter(List<App> list, Context context) {
        this.list = list;
        context1 = context;
    }

    @NonNull
    @Override
    public AppListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_app_list, viewGroup, false);
        return new MyViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView_App_Name, textView_App_Package_Name, tv_status;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.card_view);
            imageView = view.findViewById(R.id.imageview);
            textView_App_Name = view.findViewById(R.id.Apk_Name);
            textView_App_Package_Name = view.findViewById(R.id.Apk_Package_Name);
            tv_status = view.findViewById(R.id.tv_status);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.MyViewHolder viewHolder, int i) {
        try {
            final App app = list.get(i);

            viewHolder.textView_App_Name.setText(app.getName());
            viewHolder.textView_App_Package_Name.setText(app.getPackage_name());
            viewHolder.tv_status.setText(app.getStatus());
            viewHolder.imageView.setImageDrawable(app.getIcon());

            if (app.getStatus().equalsIgnoreCase("System app")) {
                viewHolder.tv_status.setTextColor(context1.getResources().getColor(R.color.blue));
            } else if (app.getStatus().equalsIgnoreCase("Secure")) {
                viewHolder.tv_status.setTextColor(context1.getResources().getColor(R.color.green));
            } else if (app.getStatus().equalsIgnoreCase("High risk")) {
                viewHolder.tv_status.setTextColor(context1.getResources().getColor(R.color.red));
            }

            //Adding click listener on CardView to open clicked application directly from here .
            viewHolder.cardView.setOnClickListener(view -> {
                Animator.buttonAnim(context1, view);
                openAppInfo(app.getPackage_name());
//                Intent intent = context1.getPackageManager().getLaunchIntentForPackage(app.getPackage_name());
//                if (intent != null) {
//                    context1.startActivity(intent);
//                } else {
//                    Toast.makeText(context1, app.getName() + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
//                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAppInfo(String package_name) {
        try {    //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + package_name));
            context1.startActivity(intent);

        } catch (ActivityNotFoundException e) {     //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context1.startActivity(intent);
        }
    }


}
