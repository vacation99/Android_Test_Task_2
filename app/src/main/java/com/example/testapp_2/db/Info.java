package com.example.testapp_2.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Info extends RealmObject {

    @PrimaryKey
    String name;
    String image;
    String viewers;
    String channels;

    public Info() {
    }

    public Info(String name, String image, String viewers, String channels) {
        this.name = name;
        this.image = image;
        this.viewers = viewers;
        this.channels = channels;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewers() {
        return viewers;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }
}