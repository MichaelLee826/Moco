package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

public class Adapter_PopupPlus extends BaseAdapter {
    Context context;
    String[] strings;

    public Adapter_PopupPlus(Context context, String[] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.popup_window_plus, null);
        TextView textView = (TextView)view.findViewById(R.id.popup_window_TextView);
        String string = strings[position];
        textView.setText(string);
        return view;
    }
}