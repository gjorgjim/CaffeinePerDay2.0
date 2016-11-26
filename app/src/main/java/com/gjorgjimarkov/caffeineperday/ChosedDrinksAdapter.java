package com.gjorgjimarkov.caffeineperday;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjorgjim on 6/17/16.
 */
public class ChosedDrinksAdapter extends BaseAdapter {
    Context context;
    List<Drinks> array;

    ChosedDrinksAdapter(Context context, List<Drinks> array) {
        this.context = context;
        this.array = new ArrayList<>();
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.chosed_drinks_layout,null);
        TextView title = (TextView)view.findViewById(R.id.chosedTitle);
        ImageView image = (ImageView)view.findViewById(R.id.chosedImage);
        TextView number = (TextView)view.findViewById(R.id.chosedNumberOf);
        title.setText(array.get(position).getName());
        image.setImageResource(array.get(position).getId());
        number.setText("x" + array.get(position).getNumber());
        return view;
    }
}
