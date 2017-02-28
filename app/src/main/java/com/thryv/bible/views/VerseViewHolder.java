package com.thryv.bible.views;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.thryv.bible.R;
import com.thryv.bible.models.Verse;

/**
 * Created by ell on 10/12/16.
 */

public class VerseViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;
    private OnVerseLongClickListener verseLongClickListener;

    public VerseViewHolder(View itemView, OnVerseLongClickListener listener) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.tv_verse);
        verseLongClickListener = listener;
    }

    public void bindVerse(final Verse verse){
//        String styleText = "<head>\n" +
//                "<style type=\"text/css\">\n" +
//                "@font-face {\n" +
//                "    font-family: Lora;\n" +
//                "    src: url(\"file:///android_asset/fonts/Lora-Regular.ttf\")\n" +
//                "}\n" +
//                "body {\n" +
//                "    font-family: Lora;\n" +
//                "    font-size: medium;\n" +
//                "    text-align: justify;\n" +
//                "}\n" +
//                "</style>\n" +
//                "</head>";
//        textView.loadDataWithBaseURL("file:///android_asset/", "<html>" + styleText + "<body>" + verse.getTextForReading() + "</body></html>", "text/html", "utf-8", null);

        textView.setText(verse.getTextForReading());

        if (verseLongClickListener != null){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    verseLongClickListener.onVerseLongClicked(verse);
                    return false;
                }
            });
        }
    }

    public interface OnVerseLongClickListener {
        void onVerseLongClicked(Verse verse);
    }
}
