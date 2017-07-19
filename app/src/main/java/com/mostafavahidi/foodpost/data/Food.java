package com.mostafavahidi.foodpost.data;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Mostafa on 7/3/2017.
 */

public class Food {
    private String imageUrl;
    private String name;
    private String email;
    private String foodDesc;
    private Bitmap photo;
    private String text;
    private List<Tag> tags;


    public Food(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean hasTag(String string) {
        for (Tag tag : tags) {
            if (tag.getText().equals(string)){
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Food)) return false;

        Food question = (Food) o;

        if (getName() != null ? !getName().equals(question.getName()) : question.getName() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(question.getEmail()) : question.getEmail() != null)
            return false;
        if (getFoodDesc() != null ? !getFoodDesc().equals(question.getFoodDesc()) : question.getFoodDesc() != null)
            return false;
        if (getText() != null ? !getText().equals(question.getText()) : question.getText() != null)
            return false;
        return getTags() != null ? getTags().equals(question.getTags()) : question.getTags() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getFoodDesc() != null ? getFoodDesc().hashCode() : 0);
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        result = 31 * result + (getTags() != null ? getTags().hashCode() : 0);
        return result;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
