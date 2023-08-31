package com.shawty.glados.musical;

import android.app.Fragment;

/**
 * Created by GLaDOS on 5/19/2016.
 */
public class MenuItem {
    private String text;
    private Fragment f;

    public MenuItem(String text, Fragment f) {
        this.text = text;
        this.f = f;
    }

    public String getText() {
        return this.text;
    }

    public Fragment getFragment() {
        return this.f;
    }
}
