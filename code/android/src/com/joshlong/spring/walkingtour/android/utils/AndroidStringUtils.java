package com.joshlong.spring.walkingtour.android.utils;

import android.text.Editable;
import android.widget.EditText;

/**
 * simple utilities for dealing with {@link String} instances in Android, which <EM>hates</EM> them. a lot.
 */
public class AndroidStringUtils {
    static public String stringValueFor(Editable editable) {
        char[] cs = new char[editable.length()];
        editable.getChars(0, editable.length(), cs, 0);
        return new String(cs);
    }

    static public String stringValueFor(EditText editable) {
        return stringValueFor(editable.getText());
    }
}
