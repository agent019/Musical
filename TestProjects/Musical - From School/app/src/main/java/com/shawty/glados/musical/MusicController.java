package com.shawty.glados.musical;

import android.content.Context;
import android.view.KeyEvent;

public class MusicController extends android.widget.MediaController {
    public MusicController(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                ((MainActivity) getContext()).onBackPressed();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void hide() {

    }
}