package com.keepfit.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.keepfit.app.R;
import com.keepfit.app.view.StringView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/23/2016.
 */
public class SimpleStringAdapter extends ArrayAdapter<String> {
    private static final String TAG = "StringListAdapter";

    private List<String> groups;
    protected Context context;
    protected LayoutInflater inflater;

    public SimpleStringAdapter(Context context) {
        this(new ArrayList<String>(), context);
    }

    public SimpleStringAdapter(List<String> groups, Context context) {
        super(context, R.layout.string_list_item);
        this.groups = groups;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new StringView(getItem(position), context);
        }
        StringView view = (StringView) convertView;
        view.update(groups.get(position), this);
        return view;
    }

    public void addString(String String) {
        groups.add(String);
        notifyDataSetChanged();
    }

    public void addStrings(List<String> Strings) {
        groups.addAll(Strings);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public String getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    public void reset() {
        groups = new ArrayList<>();
    }

}