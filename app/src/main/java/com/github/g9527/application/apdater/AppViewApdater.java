package com.github.g9527.application.apdater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.g9527.application.R;
import com.github.g9527.application.core.AppViewEntity;

import java.util.List;

public class AppViewApdater extends BaseAdapter {

    private Context context;
    private List<AppViewEntity> datas;


    public AppViewApdater(Context context, List<AppViewEntity> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_view, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.app_view_img);
            holder.textView = convertView.findViewById(R.id.app_view_label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;
        imageView.setImageResource(datas.get(position).getImgId());
        textView.setText(datas.get(position).getLabel());
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
