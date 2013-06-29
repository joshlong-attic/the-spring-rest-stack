package com.joshlong.spring.walkingtour.android.view.support;

import android.view.*;
import android.widget.BaseAdapter;

import java.util.List;

/**
 *
 * @author Josh Long
 * @param <T>
 */
public abstract class AbstractListAdapter<T> extends BaseAdapter {

    private List<T> rows;

    public AbstractListAdapter(List<T> rows) {
        this.rows = rows;
    }

    public int getCount() {
        return rows == null ? 0 : rows.size();
    }

    public T getItem(int position) {
        return rows.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * build a UI representing a view for a given entity, T.
     */
    abstract public View getView(int position, View convertView, ViewGroup parent);


}