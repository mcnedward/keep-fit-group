package com.keepfit.app.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.keepfit.app.view.DataFolderListView;
import com.keepfit.app.view.ResultView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFolderAdapter extends BaseExpandableListAdapter {
    private static String TAG = "DataFolderAdapter";

    private Context context;

    private List<DataFolder> groups;
    private List<List<DataFile>> children;

    public DataFolderAdapter(List<DataFolder> groups, Context context) {
        this.context = context;
        this.groups = groups;
        children = new ArrayList<>();
    }

    public void addGroup(DataFolder group) {
        groups.add(group);
    }

    public DataFolderListView getGenericView(DataFolder dataFolder) {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 98);

        TextView tv = new TextView(this.context);
        tv.setLayoutParams(lp);
        tv.setMinimumWidth(550); // Sets the width of the text view for the list
        // tv.setLongClickable(true);
        tv.setTextSize(20);
        tv.setTypeface(null, Typeface.BOLD);

        // Center the text vertically
        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        tv.setPadding(45, 0, 0, 0);

        DataFolderListView artistAlbumListView = new DataFolderListView(dataFolder, context);

        return artistAlbumListView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        DataFolderListView artistView = getGenericView(groups.get(groupPosition));
        return artistView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ResultView(getChild(groupPosition, childPosition), context);
        }
        return convertView;
    }

    public void setGroups(List<DataFolder> groups) {
        this.groups = groups;
        for (DataFolder folder : groups) {
            children.add(folder.getFiles());
        }
        notifyDataSetChanged();
    }

    public void reset() {
        groups = new ArrayList<>();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public DataFolder getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public DataFile getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Change this?
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Change this?
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
