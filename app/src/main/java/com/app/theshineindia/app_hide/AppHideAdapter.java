package com.app.theshineindia.app_hide;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.device_scan.App;
import com.app.theshineindia.utils.SP;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AppHideAdapter extends RecyclerView.Adapter<AppHideAdapter.MyViewHolder> {
    private List<App> app_list;
    private Context context1;
    private ArrayList<String> hidden_app_list;

    AppHideAdapter(List<App> list, Context context) {
        this.app_list = list;
        context1 = context;
        hidden_app_list = SP.getArrayList(context, SP.hidden_app_list);
    }

    @NonNull
    @Override
    public AppHideAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_app_hide, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return app_list.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView_App_Name, textView_App_Package_Name;
        Switch switch_hide;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            textView_App_Name = view.findViewById(R.id.Apk_Name);
            textView_App_Package_Name = view.findViewById(R.id.Apk_Package_Name);
            switch_hide = view.findViewById(R.id.switch_hide);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AppHideAdapter.MyViewHolder viewHolder, int i) {
        try {
            final App app = app_list.get(i);

            viewHolder.textView_App_Name.setText(app.getName());
            viewHolder.textView_App_Package_Name.setText(app.getPackage_name());
            viewHolder.imageView.setImageDrawable(app.getIcon());
            viewHolder.switch_hide.setChecked(app.isHidden());

            viewHolder.switch_hide.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    if (isChecked) {
                        hidden_app_list.add(app.getPackage_name());
                        app_list.get(i).setHidden(true);

                    } else {
                        app_list.get(i).setHidden(false);
                        if (hidden_app_list != null)
                            hidden_app_list.remove(app.getPackage_name());
                    }

                    SP.saveArrayList(context1, SP.hidden_app_list, hidden_app_list);
                    Log.d("1111", "hidden_app_list: " + SP.getArrayList(context1, SP.hidden_app_list));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void fn_hideicon() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
//        builder.setTitle("Important!");
//        builder.setMessage("To launch the app again, dial phone number 1234567890");
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                context1.getPackageManager().setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME,
//                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                        PackageManager.DONT_KILL_APP);
//            }
//        });
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
//        builder.show();
//    }
//
//    private void fn_unhide() {
//        PackageManager p = context1.getPackageManager();
//        ComponentName componentName = new ComponentName(this, com.deepshikha.hideappicon.MainActivity.class);
//        p.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//    }
}
