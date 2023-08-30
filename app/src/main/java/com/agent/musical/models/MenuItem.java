package com.agent.musical.models;

import androidx.fragment.app.Fragment;

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
