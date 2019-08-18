package com.app.expensetracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.expensetracker.ExpenseDetails;
import com.app.expensetracker.R;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.utility.Utils;

import java.util.List;

/**
 * Created by Avik on 2/4/2019.
 */

public class TransExpAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<ExcelDataModel> list_shareofshelf;

    public TransExpAdapter(Context context, List<ExcelDataModel> list_shareofshelf) {
        this.context = context;
        this.list_shareofshelf = list_shareofshelf;

        // list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount()

        /*for (int i = 0; i < list_shareofshelf.size(); i++) {
            for(int j=0;j<list_shareofshelf.get(i).get)
        }*/


    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.list_shareofshelf.get(listPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    //@SuppressLint("SetTextI18n")
    @Override
    public View getChildView(final int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cview = layoutInflater.inflate(R.layout.list_child_sos, null);
        double st_expenses = 0, st_income = 0;

        RelativeLayout child_container = cview.findViewById(R.id.child_container);
        TextView tv_category = cview.findViewById(R.id.tv_category);
        TextView tv_expense = cview.findViewById(R.id.tv_expense);
        ImageView iv_icon = cview.findViewById(R.id.iv_icon);


        if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getIncome_Expenses().equalsIgnoreCase("Expenses")) {
            tv_expense.setText("-" + list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount());
            // iv_icon.setBackgroundResource(R.drawable.ic_inc_inc);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_exp));

            if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount().isEmpty()) {
                //st_expenses += Integer.parseInt(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount());
                //Log.d("Summation", "" + st_expenses);
            }
        } else {
            tv_expense.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount());
            //iv_icon.setBackgroundResource(R.drawable.ic_inc_inc);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inc_inc));
            if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount().isEmpty()) {
                //   st_income += Integer.parseInt(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount());
                //  Log.d("Summation ", ":" + st_income);
            }
        }

        for (int i = 0; i < list_shareofshelf.get(listPosition).getGroupFocAll().size(); i++) {
            // Log.d("AMOUNTS", list_shareofshelf.get(listPosition).getGroupFocAll().get(i).getAmount());
            try {
                if (list_shareofshelf.get(listPosition).getGroupFocAll().get(i).getIncome_Expenses().equalsIgnoreCase("Expenses")) {
                    st_expenses += Double.parseDouble(list_shareofshelf.get(listPosition).getGroupFocAll().get(i).getAmount());
                } else {
                    st_income += Double.parseDouble(list_shareofshelf.get(listPosition).getGroupFocAll().get(i).getAmount());
                }
            } catch (Exception e) {
            }
        }

        child_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
                    //Utils.ShowToast(context, list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId());
                    Intent i = new Intent(context, ExpenseDetails.class);
                    i.putExtra("trans_id", list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId());
                    context.startActivity(i);
                }
            }
        });

        if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
            iv_icon.setVisibility(View.GONE);
            iv_icon.setImageDrawable(null);

            if (st_income != 0.0) {
                tv_category.setText("Expenses: " + st_expenses);
            } else {
                tv_category.setText("Expenses: " + st_expenses + "    Income:    " + st_income);
            }

            Log.d("st_income", "" + st_income);

            tv_expense.setTextColor(Color.parseColor("#000000"));
            tv_expense.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        } else {
            if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo().isEmpty()) {
                tv_category.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo());
            } else {
                tv_category.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());
            }
        }

        Log.d("listPosition", String.valueOf(expandedListPosition + " " + (list_shareofshelf.get(listPosition).getGroupFocAll().size() - 1)));

        if (expandedListPosition == list_shareofshelf.get(listPosition).getGroupFocAll().size() - 1) {
            child_container.setBackgroundResource(R.drawable.card_bottom_round);
        }

        return cview;
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return list_shareofshelf.get(listPosition).getGroupFocAll().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return list_shareofshelf.get(listPosition).getGroupFocAll();
    }

    @Override
    public int getGroupCount() {
        return list_shareofshelf.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_parent_sos, null);
        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(listPosition);
        /*if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_parent_sos, null);
        }*/
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        LinearLayout parent_container = (LinearLayout) view.findViewById(R.id.parent_container);
        ImageView actionIcon = (ImageView) view.findViewById(R.id.actionIcon);

        listTitleTextView.setText(Utils.getFormattedDate(list_shareofshelf.get(listPosition).getDate()));

        /*if (isExpanded) {
            actionIcon.setImageResource(R.drawable.ic_collapse);
        } else {
            actionIcon.setImageResource(R.drawable.ic_expand);
        }
*/
       /* if (isExpanded) {
            view.setPadding(0, 0, 0, 0);
            int[] attrs = new int[]{R.drawable.card_top_round};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            parent_container.setBackgroundResource(backgroundResource);
        } else {
            view.setPadding(0, 0, 0, 10);
            int[] attrs = new int[]{R.drawable.card_full_round};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            parent_container.setBackgroundResource(backgroundResource);
        }*/
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


}
