package com.pinnackl.shoppinglist;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by htutuaku on 02/12/2016.
 */

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> Title;

    public CustomAdapter(Context context, ArrayList<String> names) {
        mContext = context;
        Title = names;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title;
        ImageView imgButton;
        imgButton = (ImageView) row.findViewById(R.id.imageButton);
        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title.get(position));
        imgButton.setImageDrawable(mContext.getDrawable((R.drawable.ic_delete)));

        return (row);
    }
}
