package com.app.theshineindia.sim_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.sos.Contact;
import com.app.theshineindia.sos.SosClickListener;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList<Contact> list = new ArrayList<>();
    private SimTrackerClickListener clickListener;

    public interface SimTrackerClickListener {
        void onDeleteClicked(int position);
    }

    RecyclerViewAdapter(SimTrackerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sim_tracker_contact_number_item, viewGroup, false);
        return new MyViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_num;
        ImageView deleteIcon;
//        Switch switch_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.emergencynum_item_name);
            tv_num = itemView.findViewById(R.id.emergencynum_item_num);
//            switch_status = itemView.findViewById(R.id.switch_status);

            deleteIcon = itemView.findViewById(R.id.deleteIcon);


            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContactAlert(itemView.getContext(), list.get(getLayoutPosition()).getId(), getLayoutPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder myViewHolder, int i) {
        try {
            myViewHolder.tv_name.setText(list.get(i).getName());
            myViewHolder.tv_num.setText(list.get(i).getNum());

            /*if (list.get(i).getStatus().equals("Active")) {
                myViewHolder.switch_status.setChecked(true);
            }

            myViewHolder.switch_status.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    clickListener.onStatusClicked(list.get(i).getId(), "Active");
                } else {
                    clickListener.onStatusClicked(list.get(i).getId(), "Inactive");
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deleteContactAlert(Context context, String sos_id, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete !!!")
                .setMessage("Are you sure, want to delete this contact?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            list.remove(pos);
            clickListener.onDeleteClicked(pos);
            notifyDataSetChanged();
        });

        builder.setNegativeButton("Not now", (dialog, which) -> dialog.dismiss());

        builder.create();
        builder.show();
    }


    public void setList(ArrayList<Contact> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<Contact> getList() {
        return list;
    }
}
