package com.app.theshineindia.sos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.app.theshineindia.R;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.DELETE;

public class SOSAdapter extends RecyclerView.Adapter<SOSAdapter.MyViewHolder> {
    private ArrayList<Contact> list;
    private SosClickListener clickListener;

    SOSAdapter(ArrayList<Contact> list, SosClickListener clickListener) {
        this.list = list;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SOSAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emergency_number_item, viewGroup, false);
        return new MyViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_num;
        Switch switch_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.emergencynum_item_name);
            tv_num = itemView.findViewById(R.id.emergencynum_item_num);
            switch_status = itemView.findViewById(R.id.switch_status);

            itemView.setOnLongClickListener(v -> {
                deleteContactAlert(itemView.getContext(), list.get(getLayoutPosition()).getId(), getLayoutPosition());
                return true;
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SOSAdapter.MyViewHolder myViewHolder, int i) {
        try {
            myViewHolder.tv_name.setText(list.get(i).getName());
            myViewHolder.tv_num.setText(list.get(i).getNum());

            if (list.get(i).getStatus().equals("Active")) {
                myViewHolder.switch_status.setChecked(true);
            }

            myViewHolder.switch_status.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    clickListener.onStatusClicked(list.get(i).getId(), "Active");
                } else {
                    clickListener.onStatusClicked(list.get(i).getId(), "Inactive");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deleteContactAlert(Context context, String sos_id, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete !!!")
                .setMessage("Are you sure, want to delete this contact?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            clickListener.onDeleteClicked(sos_id);
            list.remove(pos);
            notifyDataSetChanged();
        });

        builder.setNegativeButton("Not now", (dialog, which) -> dialog.dismiss());

        builder.create();
        builder.show();
    }

}
