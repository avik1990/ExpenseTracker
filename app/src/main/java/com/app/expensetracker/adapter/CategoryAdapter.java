package com.app.expensetracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.expensetracker.R;
import com.app.expensetracker.model.CategoryModel;
import com.app.expensetracker.model.Months;
import com.app.expensetracker.utility.CircularTextView;
import com.app.expensetracker.utility.Utils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryModel> countryList;
    Context mContext;
    GetMonthFromAdapter getMonthFromAdapter;
    String id = "";

    public interface GetMonthFromAdapter {
        public void returnedMonth(String month);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;
        ImageView iv_cat_icon;
        LinearLayout ll_background;

        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);
            iv_cat_icon = view.findViewById(R.id.iv_cat_icon);
            ll_background = view.findViewById(R.id.ll_background);
        }
    }

    public CategoryAdapter(Context mContext, List<CategoryModel> countryList, GetMonthFromAdapter getMonthFromAdapter, String id) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;
        this.id = id;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CategoryModel c = countryList.get(position);
        holder.tv_category.setText(c.getCat_name());
        /*if (Utils.getCurrentMonths(currentmonth).equalsIgnoreCase(c.getMonthname())) {
            holder.tv_checfname.setSolidColor("#FED852");
            //holder.tv_checfname.set
        }*/

        if (id.equalsIgnoreCase(c.getCat_id())) {
            holder.ll_background.setBackgroundResource(R.color.yellow);
        }


        holder.iv_cat_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMonthFromAdapter.returnedMonth(c.getCat_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        //Log.d("RV", "Item size [" + countryList.size() + "]");
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_categories, parent, false);
        return new MyViewHolder(v);
    }
}
