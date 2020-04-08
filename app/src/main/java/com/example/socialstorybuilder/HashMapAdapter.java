package com.example.socialstorybuilder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HashMapAdapter extends BaseAdapter {
    private final ArrayList mData;

    public HashMapAdapter() {
        this.mData = new ArrayList();
    }

    public HashMapAdapter(@NonNull Map<String, String> map) {
        this.mData = new ArrayList();
        this.mData.addAll(map.entrySet());
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<Object, Object> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return position;
    }

    public String getKey(int position) {
        return getItem(position).getKey().toString();
    }
    public String getValue(int position) {
        return getItem(position).getValue().toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            result = convertView;
        }


        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(android.R.id.text1)).setText(getValue(position));


        return result;
    }

    public void refresh(@NonNull Map<String, String> map)
    {
        this.mData.clear();
        this.mData.addAll(map.entrySet());
        notifyDataSetChanged();
    }
}
