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
    Context context;
    private List<String> agencyList;
    private List<String> dateList;
    private List<String> textList;
    private List<String> statusList;

    public class ViewHolder{
        TextView agencyNameTextView;
        TextView dateTextView;
        TextView textTextView;
        TextView statusTextView;
    }

    public Adapter_MyAgency(Context context, List<String> agencyList, List<String> dateList, List<String> textList, List<String> statusList){
        this.context = context;
        this.agencyList = agencyList;
        this.dateList = dateList;
        this.textList = textList;
        this.statusList = statusList;
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
        String name = agencyList.get(position);
        String date = dateList.get(position);
        String text = textList.get(position);
        String status = statusList.get(position);
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.myagency_item, null);

            viewHolder = new ViewHolder();
            viewHolder.agencyNameTextView = (TextView)convertView.findViewById(R.id.AgencyItemAgencyNameTextView);
            viewHolder.dateTextView = (TextView)convertView.findViewById(R.id.AgencyItemDateTextView);
            viewHolder.textTextView = (TextView)convertView.findViewById(R.id.AgencyItemTextTextView);
            viewHolder.statusTextView = (TextView)convertView.findViewById(R.id.AgencyItemStatusTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.agencyNameTextView.setText(name);
        viewHolder.dateTextView.setText(date);
        viewHolder.textTextView.setText(text);
        viewHolder.statusTextView.setText(status);

        return convertView;
    }
}
