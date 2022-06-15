package com.jk.codez.item;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jk.codez.R;


public class ItemClickSupport {
    private final RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                onItemClickListener.onItemClicked(recyclerView, holder.getAdapterPosition(), view);
            }
        }
    };
    private final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                return onItemLongClickListener.onItemLongClicked(recyclerView, holder.getAdapterPosition(), view);
            }
            return false;
        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setTag(R.id.item_click_support, this);
        final RecyclerView.OnChildAttachStateChangeListener attachListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if (onItemClickListener != null) {
                    view.setOnClickListener(onClickListener);
                }
                if (onItemLongClickListener != null) {
                    view.setOnLongClickListener(onLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
            }
        };
        this.recyclerView.addOnChildAttachStateChangeListener(attachListener);
    }

    public static ItemClickSupport addTo(@NonNull RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

//    public static ItemClickSupport removeFrom(@NonNull RecyclerView view) {
//        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
//        if (support != null) {
//            support.detach(view);
//        }
//        return support;
//    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
        return this;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
//        return this;
    }

//    private void detach(@NonNull RecyclerView view) {
//        view.removeOnChildAttachStateChangeListener(attachListener);
//        view.setTag(R.id.item_click_support, null);
//    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View view);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View view);
    }
}

