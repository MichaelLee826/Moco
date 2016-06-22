package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-04-23-0023.
 */
public class Adapter_MyAgency extends BaseAdapter {
    public List<String> agencyList;
    Context context;

    public class ViewHolder{
        TextView agencyNameTextView = null;
    }

    public Adapter_MyAgency(Context context, List<String> agencyList){
        this.context = context;
        this.agencyList = agencyList;
    }

    @Override
    public int getCount() {
        return agencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return agencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = agencyList.get(position).toString();
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.agencyitem, null);

            viewHolder = new ViewHolder();
            viewHolder.agencyNameTextView = (TextView)convertView.findViewById(R.id.AgencyItemAgencyNameTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.agencyNameTextView.setText(name);

        return convertView;
    }
}
