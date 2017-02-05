package com.thryv.bible.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thryv.bible.R;
import com.thryv.bible.models.Verse;
import com.thryv.bible.views.BottomNavViewHolder;
import com.thryv.bible.views.VerseViewHolder;

import java.util.List;

/**
 * Created by ell on 10/12/16.
 */

public class VerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Verse> verses;
    private View.OnClickListener nextChapterOnClickListener;
    private View.OnClickListener previousChapterOnClickListener;
    private VerseViewHolder.OnVerseLongClickListener verseLongClickListener;

    public VerseAdapter(List<Verse> verses,
                        View.OnClickListener previousChapterOnClickListener,
                        View.OnClickListener nextChapterOnClickListener,
                        VerseViewHolder.OnVerseLongClickListener verseLongClickListener) {
        this.verses = verses;
        this.nextChapterOnClickListener = nextChapterOnClickListener;
        this.previousChapterOnClickListener = previousChapterOnClickListener;
        this.verseLongClickListener = verseLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0){
            View view = inflater.inflate(R.layout.item_verse, parent, false);
            return new VerseViewHolder(view, verseLongClickListener);
        }else {
            View view = inflater.inflate(R.layout.item_bottom_nav, parent, false);
            return new BottomNavViewHolder(view,
                    previousChapterOnClickListener,
                    nextChapterOnClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != verses.size()){
            ((VerseViewHolder)holder).bindVerse(verses.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return verses.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == verses.size() ? 1 : 0;
    }
}
