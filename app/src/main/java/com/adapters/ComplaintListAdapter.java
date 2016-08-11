package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.data.tickets.Complaint;
import com.trail.octo.R;

import java.util.ArrayList;

public class ComplaintListAdapter extends BaseAdapter{
    Context context;
    ArrayList<Complaint> complaintArrayList;
    public ComplaintListAdapter(Context context,ArrayList<Complaint> complaintArrayList){
        this.context = context;
        this.complaintArrayList = complaintArrayList;
    }
    @Override
    public int getCount() {
        return complaintArrayList.size();
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
        imageView.setVisibility(View.INVISIBLE);
        textView.setText(complaintArrayList.get(position).getQuery_text()+" "+complaintArrayList.get(position).getStatus());
        return view;
    }
    public void dataSetChanges(ArrayList<Complaint> complaintArrayList){
        this.complaintArrayList = complaintArrayList;
        this.notifyDataSetChanged();
    }

}
