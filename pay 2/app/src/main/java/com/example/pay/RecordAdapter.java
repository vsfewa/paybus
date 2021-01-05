package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends ArrayAdapter {

    private final int resourceId;

    public RecordAdapter(Context context, int textViewResourceId, List<Record> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = (Record) getItem(position); // 获取当前项的实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView location = (TextView) view.findViewById(R.id.location);//获取该布局内的文本视图
        time.setText(record.getTime());
        location.setText(record.getLocation());
        return view;
    }
}
