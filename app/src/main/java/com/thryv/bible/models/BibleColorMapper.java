package com.thryv.bible.models;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by ell on 4/20/17.
 */

public class BibleColorMapper  {
    Map<Integer, String> verseToCode;

    public void setVerseToCodeMap(Map<Integer, String> verseToCode) {
        this.verseToCode = verseToCode;
    }

    public String hexColorString(int verseNumber) {
        String colorCode = verseToCode.get(verseNumber);
        if (!TextUtils.isEmpty(colorCode)){
            switch (colorCode) {
                case "a":
                    return "#ff7777";
                case "b":
                    return "#7777ff";
                case "c":
                    return "#55ff55";
                case "d":
                    return "#ffff55";
            }
        }
        return null;
    }
}
