package com.jk.codez.item;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jk.codez.R;

import java.util.Arrays;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final List<Item> mItemList;
//    private int mSelected = -1;
    private final SelectListener mListener;
    boolean noDelete;

    @SuppressLint("NotifyDataSetChanged")
    public ItemAdapter(List<Item> f, @Nullable SelectListener l) {
        mItemList = f;
        mListener = l;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_view, viewGroup, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mItemList.get(i));
    }

    @Override
    public int getItemCount() { if (mItemList != null ) return mItemList.size(); else return 0; }

    public Item get(int index) { return mItemList.get(index); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_address;
        final TextView tv_codes;
        final TextView tv_notes;

        ViewHolder(View view) {
            super(view);
            tv_address = view.findViewById(R.id.tv_address);
            tv_codes = view.findViewById(R.id.tv_codes);
            tv_notes = view.findViewById(R.id.tv_notes);
        }

        void bind(@NonNull final Item i) {
            Integer num = i.getNumber();
            String addy = (num != null) ? num.toString() + " " + i.getStreet() : "No number";
            tv_address.setText(addy);
            String[] cdz = i.getCodes();
            tv_codes.setText((cdz.length > 0) ? Arrays.toString(cdz) : "No codes");
            tv_notes.setText(i.getNotes());
        }
    }

    public interface SelectListener {
        void selectCode(Item i);
    }
}
