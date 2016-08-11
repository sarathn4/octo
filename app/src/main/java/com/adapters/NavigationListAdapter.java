package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trail.octo.R;


public class NavigationListAdapter extends BaseAdapter{
    String[] list_items;
    Context context;
    int[] nav_items;
    public NavigationListAdapter(Context context){
        this.context = context;
        list_items = context.getResources().getStringArray(R.array.navigation_list);
        nav_items = new int[list_items.length];
        nav_items[0] = R.drawable.icon_home;
        nav_items[1] = R.drawable.icon_editprofile;
        nav_items[2] = R.drawable.icon_upload;
        nav_items[3] = R.drawable.icon_settings;
        nav_items[4] = R.drawable.icon_feedback;
        nav_items[5] = R.drawable.icon_share;
        nav_items[6] = R.drawable.icon_contact;
        nav_items[7] = R.drawable.icon_logout;
    }
    @Override
    public int getCount() {
        return list_items.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.navigation_list_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.nav_item_icon);
        TextView textView = (TextView) view.findViewById(R.id.navigation_textView1);
        imageView.setImageResource(nav_items[position]);
        textView.setText(list_items[position]);
        return view;
    }
}
