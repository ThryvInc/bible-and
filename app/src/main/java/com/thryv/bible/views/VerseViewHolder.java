package com.thryv.bible.views;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.thryv.bible.R;
import com.thryv.bible.models.Verse;

/**
 * Created by ell on 10/12/16.
 */

public class VerseViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public VerseViewHolder(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.tv_verse);
    }

    public void bindVerse(Verse verse){
        String verseText = verse.getText();
        verseText = verseText.replaceAll("\\[[0-9]+\\]", "");
        verseText = verseText.replaceAll("\n", "<br>");
        if (verse.getVerseNumber() != 0){
            verseText = "<b>" + verse.getVerseNumber() + "</b>" + "&nbsp;&nbsp;&nbsp;&nbsp;" + verseText;
        }
        textView.setText(Html.fromHtml(verseText));
    }

}
