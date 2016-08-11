package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trail.octo.R;

public class NavigationExpandableListAdapter extends BaseExpandableListAdapter{

    Context context;
    String[] groupItems;
    int[] nav_items;
    public NavigationExpandableListAdapter(Context context){
        this.context = context;
        groupItems = context.getResources().getStringArray(R.array.navigation_list);
        nav_items = new int[groupItems.length];
        nav_items[0] = R.drawable.icon_home;
        nav_items[1] = R.drawable.icon_editprofile;
        nav_items[2] = R.drawable.icon_upload;
        nav_items[3] = R.drawable.icon_settings;
        nav_items[4] = R.drawable.icon_feedback;
        nav_items[5] = R.drawable.icon_mobile;
        nav_items[6] = R.drawable.icon_feedback;
        nav_items[7] = R.drawable.icon_share;
        nav_items[8] = R.drawable.icon_contact;
        nav_items[9] = R.drawable.icon_logout;
    }
    @Override
    public int getGroupCount() {
        return groupItems.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition==6)
            return 2;
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.navigation_list_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.nav_item_icon);
        TextView textView = (TextView) view.findViewById(R.id.navigation_textView1);
        imageView.setImageResource(nav_items[groupPosition]);
        textView.setText(groupItems[groupPosition]);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(groupPosition == 6) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.navigation_list_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.nav_item_icon);
            TextView textView = (TextView) view.findViewById(R.id.navigation_textView1);
            imageView.setVisibility(View.INVISIBLE);
            if(childPosition==0)
                textView.setText("New Complaint");
            else if(childPosition == 1)
                textView.setText("Complaint Status");
            return view;
        }
        else
            return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
