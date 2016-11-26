package com.gjorgjimarkov.caffeineperday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by gjorgjim on 6/6/16.
 */
public class MyExpendableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ParentDrink> parentDrinksList;
    private ArrayList<ParentDrink> originalList;

    public MyExpendableListAdapter(Context context, ArrayList<ParentDrink> originalList) {
        this.context = context;
        this.parentDrinksList = new ArrayList<>();
        this.parentDrinksList = originalList;
        this.originalList = new ArrayList<>();
        this.originalList = originalList;
    }

    @Override
    public int getGroupCount() {
        return parentDrinksList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentDrinksList.get(groupPosition).getArray().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentDrinksList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentDrinksList.get(groupPosition).getArray().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentDrink parentDrink = (ParentDrink) getGroup(groupPosition);

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_list_layout, null);
        }
        TextView name = (TextView)convertView.findViewById(R.id.parentName);
        name.setText(parentDrink.getName().toString());

        ImageView arrowImg = (ImageView)convertView.findViewById(R.id.arrowImage);
        if(isExpanded) {
            arrowImg.setImageResource(R.drawable.ic_arrow_drop_up_black_48dp);

        } else {
            arrowImg.setImageResource(R.drawable.ic_arrow_drop_down_black_48dp);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Drinks currDrink = (Drinks) getChild(groupPosition, childPosition);

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_list_layout, null);
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.drinkImage);
        image.setImageResource(currDrink.getId());

        TextView name = (TextView)convertView.findViewById(R.id.nameText);
        name.setText(currDrink.getName());

        TextView quantity = (TextView)convertView.findViewById(R.id.quantityText);
        DecimalFormat df = new DecimalFormat("#.00");
        double floz = Integer.parseInt(currDrink.getMl().substring(0,currDrink.getMl().length()-2)) * 0.0338135;
        quantity.setText(currDrink.getMl() + " / " + df.format(floz) + "fl oz");

        TextView numberOf = (TextView)convertView.findViewById(R.id.numberOf);
        numberOf.setText("x" + currDrink.getNumber());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean filterData(String query) {
        query = query.toLowerCase();
        parentDrinksList = new ArrayList<>();

        if(query.isEmpty()) {
            parentDrinksList = originalList;
        } else {
            for (ParentDrink parentDrink : originalList) {
                ArrayList<Drinks> childList = parentDrink.getArray();
                ArrayList<Drinks> newlist = new ArrayList<>();

                for (Drinks drink : childList) {
                    if(drink.getName().toLowerCase().contains(query)) {
                        newlist.add(drink);
                    }
                }
                if (newlist.size() > 0 ) {
                    ParentDrink newParentDrink = new ParentDrink(parentDrink.getName(), newlist);
                    parentDrinksList.add(newParentDrink);
                }
            }
        }
        notifyDataSetChanged();
        if(parentDrinksList.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }
}
