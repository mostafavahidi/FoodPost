package com.mostafavahidi.foodpost.data;

import android.support.annotation.NonNull;

import com.yalantis.filter.model.FilterModel;

/**
 * Created by Mostafa on 7/3/2017.
 */

public class Tag implements FilterModel {
    private String text;
    private int color;

    public Tag(String text, int color) {
        this.text = text;
        this.color = color;
    }

    public Tag(){

    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        if (getColor() != tag.getColor()) return false;
        return getText().equals(tag.getText());

    }

    @Override
    public int hashCode() {
        int result = getText().hashCode();
        result = 31 * result + getColor();
        return result;
    }
}
