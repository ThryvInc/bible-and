package com.thryv.bible.helpers;

import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ell on 3/31/16.
 */
public class ItemLongPressHelper {
    private Callback mCallback;
    private float xCoordinate;
    private float yCoordinate;

    public interface Callback {
        void onLongClick(RecyclerView.ViewHolder viewHolder, float xCoordinate, float yCoordinate);
    }

    public ItemLongPressHelper(Callback callback){
        mCallback = callback;
    }

    public void attachToViewHolder(final RecyclerView.ViewHolder viewHolder){
        viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                xCoordinate = event.getX() + v.getX();
                yCoordinate = event.getY() + v.getY();
                return false;
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCallback != null) mCallback.onLongClick(viewHolder, xCoordinate, yCoordinate);
                return true;
            }
        });
    }
}
