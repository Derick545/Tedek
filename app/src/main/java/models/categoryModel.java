package models;

import android.net.Uri;

public class categoryModel {
    private String CategoryImages;
    private String Description;
    private String NumberOfItems;
    private String Title;

    public categoryModel() {
    }

    public categoryModel(String categoryImages, String description, String numberOfItems, String title) {
        CategoryImages = categoryImages;
        Description = description;
        NumberOfItems = numberOfItems;
        Title = title;
    }

    public String getCategoryImages() {
        return CategoryImages;
    }

    public void setCategoryImages(String categoryImages) {
        CategoryImages = categoryImages;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getNumberOfItems() {
        return NumberOfItems;
    }

    public void setNumberOfItems(String numberOfItems) {
        NumberOfItems = numberOfItems;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
