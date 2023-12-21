package models;

import java.net.URI;

public class itemsModel {
    private String title;
    private String year;
    private String author;
    private String description;
    private String key;

    private URI ItemImage;


    public itemsModel() {
    }

    public itemsModel(String title, String year, String author, String description,String key, URI itemImage) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.description = description;
        this.key = key;
        ItemImage = itemImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public URI getItemImage() {
        return ItemImage;
    }

    public void setItemImage(URI itemImage) {
        ItemImage = itemImage;
    }


}
