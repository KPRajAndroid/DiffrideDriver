package com.diff.provider.Models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class Document {

    public String id;
    public String name;
    public String type;
    public String img;
    private Bitmap bitmap;
    private Drawable drawable;

    private String expdate;

    public Bitmap getBitmap() {
        return bitmap;
    }
    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", img='" + img + '\'' +
                ", bitmap=" + bitmap +
                ", expdate='" + expdate + '\'' +
                '}';
    }
}
