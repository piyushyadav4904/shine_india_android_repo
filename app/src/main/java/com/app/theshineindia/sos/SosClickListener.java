package com.app.theshineindia.sos;

public interface SosClickListener {
    void onStatusClicked(String sos_id, String status);

    void onDeleteClicked(String sos_id);
}
