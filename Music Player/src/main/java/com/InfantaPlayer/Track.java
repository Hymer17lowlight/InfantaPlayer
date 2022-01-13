package com.InfantaPlayer;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Track {
    /* properties' fields */
    private StringProperty id;
    private StringProperty song;
    private StringProperty artist;
    private StringProperty album;
    private StringProperty length;
    private StringProperty duration;
    private StringProperty url;
    private Image image;

    /* constructors */
    public Track() {}

    public Track(String url) {
        this.url = new SimpleStringProperty(url);
    }

    public Track(String id, String artist, String song, String length, String duration, String album, String url) {
        this.id = new SimpleStringProperty(id);
        this.artist = new SimpleStringProperty(artist);
        this.song = new SimpleStringProperty(song);
        this.length = new SimpleStringProperty(length);
        this.duration = new SimpleStringProperty(duration);
        this.album = new SimpleStringProperty(album);
        this.url = new SimpleStringProperty(url);
    }

    /* properties' methods */
    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public String getSong() {
        return song.get();
    }

    public void setSong(String song) {
        this.song.set(song);
    }

    public StringProperty songProperty() {
        return song;
    }

    public String getDuration() {
        return length.get();
    }

    public void setDuration(String duration) {
        this.length.set(duration);
    }

    public StringProperty durationProperty() {
        return length;
    }

    public String getRate() {
        return duration.get();
    }

    public StringProperty rateProperty() {
        return duration;
    }

    public void setRate(String rate) {
        this.duration.set(rate);
    }

    public String getFormat() {
        return album.get();
    }

    public StringProperty formatProperty() {
        return album;
    }

    public void setFormat(String format) {
        this.album.set(format);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

