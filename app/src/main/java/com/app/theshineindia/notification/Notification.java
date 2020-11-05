package com.app.theshineindia.notification;

public class Notification {
    private String notification_id;
    private String message;
    private String image;
    private String date;

    public Notification(String notification_id, String message, String image, String date) {
        this.notification_id = notification_id;
        this.message = message;
        this.image = image;
        this.date = date;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }
}
