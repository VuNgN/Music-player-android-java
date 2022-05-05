package com.vungn.mymusicplayer2;

import java.io.Serializable;

public class Song implements Serializable {
    private String title;
    private String author;
    private int image;
    private int resource;

    public Song(String title, String author, int image, int resource) {
        this.title = title;
        this.author = author;
        this.image = image;
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
