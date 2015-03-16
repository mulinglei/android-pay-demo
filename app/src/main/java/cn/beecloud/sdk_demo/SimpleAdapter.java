//
//	MainActivity.java
//	BeeCloud SDK DEMO
//  Zhu Chenglin
//
//	Copyright (c) 2014 BeeCloud Inc. All rights reserved.
package cn.beecloud.sdk_demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public SimpleAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.textView.setText(context.getString(R.string.google_plus_title));
                viewHolder.imageView.setImageResource(R.drawable.ic_google_plus_icon);
                break;
            case 1:
                viewHolder.textView.setText(context.getString(R.string.google_maps_title));
                viewHolder.imageView.setImageResource(R.drawable.ic_google_maps_icon);
                break;
            default:
                viewHolder.textView.setText(context.getString(R.string.google_messenger_title));
                viewHolder.imageView.setImageResource(R.drawable.ic_google_messenger_icon);
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
