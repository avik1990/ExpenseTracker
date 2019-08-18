package com.app.expensetracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.expensetracker.R;
import com.app.expensetracker.model.Months;
import com.app.expensetracker.utility.CircularTextView;
import com.app.expensetracker.utility.Utils;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MyViewHolder> {

    private List<Months> countryList;
    Context mContext;
    GetMonthFromAdapter getMonthFromAdapter;
    String currentmonth = "";

    public interface GetMonthFromAdapter {
        public String returnedMonth(String month);
    }


    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircularTextView tv_checfname;


        public MyViewHolder(View view) {
            super(view);
            tv_checfname = view.findViewById(R.id.tv_month);
        }
    }

    public MonthAdapter(Context mContext, List<Months> countryList, GetMonthFromAdapter getMonthFromAdapter, String currentmonth) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;

        if (!currentmonth.isEmpty()) {
            String mon[] = currentmonth.split("-");
            currentmonth = mon[1];
            this.currentmonth = currentmonth;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Months c = countryList.get(position);
        holder.tv_checfname.setText(c.getMonthname());

        if (Utils.getCurrentMonths(currentmonth).equalsIgnoreCase(c.getMonthname())) {
            holder.tv_checfname.setSolidColor("#FED852");
        }


        holder.tv_checfname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMonthFromAdapter.returnedMonth(Utils.getCurrentMonthsIndex(holder.tv_checfname.getText().toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size [" + countryList.size() + "]");
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_months, parent, false);
        return new MyViewHolder(v);
    }
}
