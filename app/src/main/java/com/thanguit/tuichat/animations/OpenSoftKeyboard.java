package com.thanguit.tuichat.animations;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class OpenSoftKeyboard {
    private static OpenSoftKeyboard instance;

    public OpenSoftKeyboard() {
    }

    public static OpenSoftKeyboard getInstance() {
        if (instance == null) {
            synchronized (OpenSoftKeyboard.class) {
                if (instance == null) {
                    instance = new OpenSoftKeyboard();
                }
            }
        }
        return instance;
    }

    public void openSoftKeyboard(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
