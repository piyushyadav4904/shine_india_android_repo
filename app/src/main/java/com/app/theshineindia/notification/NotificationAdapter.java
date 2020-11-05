package com.app.theshineindia.notification;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.theshineindia.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<Notification> list;
    private ClickListener clickListener;

    public NotificationAdapter(ArrayList<Notification> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationViewHolder viewHolder, int position) {
        Notification notification = list.get(position);
        viewHolder.tv_date.setText(notification.getDate());
        viewHolder.tv_message.setText(notification.getMessage());

        if (!notification.getImage().isEmpty()) {
            viewHolder.iv_notification.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.iv_notification.getContext())
                    .load(notification.getImage())
                    //.placeholder(R.mipmap.birpro)
                    .override(500, 500) // resizing
                    .into(viewHolder.iv_notification);
        }else {
            viewHolder.iv_notification.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_date, tv_message;
        ImageView iv_notification;


        private NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_message = itemView.findViewById(R.id.tv_message);
            iv_notification = itemView.findViewById(R.id.iv_notification);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }
        }
    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
