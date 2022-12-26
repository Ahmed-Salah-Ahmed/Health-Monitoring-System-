package com.example.healthcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> headerItem;
    private HashMap<String, List<String>> childItem;


    public CustomExpandableListViewAdapter(Context context, List<String> headerItem, HashMap<String, List<String>> childItem) {
        this.context = context;
        this.headerItem = headerItem;
        this.childItem = childItem;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItem.get(headerItem.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandedlist_item, null);
        }
        TextView tv = convertView.findViewById(R.id.expandedListItem);
        tv.setText(childText);

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItem.get(headerItem.get(groupPosition)).size(); }
    @Override
    public Object getGroup(int groupPosition) {
        return headerItem.get(groupPosition);
    }
    @Override
    public int getGroupCount() {
        return headerItem.size();
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView tv = convertView.findViewById(R.id.listTitle);
        tv.setText(headerTitle);
        return convertView;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}