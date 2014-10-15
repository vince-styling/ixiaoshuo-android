package com.vincestyling.ixiaoshuo.view;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationAdapter<T> extends BaseAdapter {
    private ArrayList<T> data;

    public PaginationAdapter() {
        data = new ArrayList<T>();
    }

    public PaginationAdapter(int capacity) {
        data = new ArrayList<T>(capacity);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public boolean hasItems() {
        return data != null && !data.isEmpty();
    }

    @Override
    public T getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    public T getFirstItem() {
        return hasItems() ? data.get(0) : null;
    }

    public T getLastItem() {
        return hasItems() ? data.get(data.size() - 1) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(List<T> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void addLast(List<T> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void addLast(List<T> items, boolean redrawList) {
        data.addAll(items);
        if (redrawList) notifyDataSetChanged();
    }

    public void addFirst(List<T> items) {
        data.addAll(0, items);
        notifyDataSetChanged();
    }

    public void addFirst(List<T> items, boolean redrawList) {
        data.addAll(0, items);
        if (redrawList) notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

}
