package com.comics.lounge.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.comics.lounge.R;
import com.comics.lounge.modals.State;

import java.util.List;

public class StateAdapter extends BaseAdapter {
    private List<State> stateList;

    public StateAdapter(List<State> states) {
        this.stateList = states;
    }

    @Override
    public int getCount() {
        return stateList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, null);
        TextView txt = convertView.findViewById(R.id.tv_country);
        txt.setText(stateList.get(position).getName());
        return convertView;
    }
}
