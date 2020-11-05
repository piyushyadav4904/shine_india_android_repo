package com.app.theshineindia.sos;

public class Contact {

    private String name, num, id, status;

    public Contact(String name, String num, String id, String status) {
        this.name = name;
        this.num = num;
        this.id = id;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
