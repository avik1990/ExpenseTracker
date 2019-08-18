package com.app.expensetracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.expensetracker.R;
import com.app.expensetracker.model.ExcelDataModel;

import java.util.List;

/**
 * Created by Avik on 11-01-2017.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<ExcelDataModel> countryList;
    Context mContext;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category, tv_expenditure;

        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);
            tv_expenditure = (TextView) view.findViewById(R.id.tv_expenditure);
        }
    }

    public TransactionAdapter(Context mContext, List<ExcelDataModel> countryList) {
        this.mContext = mContext;
        this.countryList = countryList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ExcelDataModel c = countryList.get(position);

        if (!c.getMemo().isEmpty()) {
            holder.tv_category.setText(c.getMemo());
        } else {
            holder.tv_category.setText(c.getCategory());
        }

        if (c.getIncome_Expenses().equalsIgnoreCase("Expenses")) {
            holder.tv_expenditure.setText("-" + c.getAmount());
        } else {
            holder.tv_expenditure.setText(c.getAmount());
        }


    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transactions, parent, false);
        return new MyViewHolder(v);
    }
}
