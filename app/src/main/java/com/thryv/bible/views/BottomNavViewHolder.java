package com.thryv.bible.views;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.thryv.bible.R;

/**
 * Created by ell on 12/18/16.
 */

public class BottomNavViewHolder extends RecyclerView.ViewHolder {

    public BottomNavViewHolder(View itemView, View.OnClickListener previousClickListener,
                               View.OnClickListener nextClickListener) {
        super(itemView);

        itemView.findViewById(R.id.previous).setOnClickListener(previousClickListener);
        itemView.findViewById(R.id.next).setOnClickListener(nextClickListener);
    }
}
