package com.app.expensetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.expensetracker.database.DatabaseHelper;
import com.app.expensetracker.model.CategoryModel;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.model.TransactionType;
import com.app.expensetracker.utility.BaseActivity;
import com.app.expensetracker.utility.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseDetails extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ExpenseDetails";
    Context context;
    DatabaseHelper db;
    List<ExcelDataModel> list_transtype = new ArrayList<>();
    String cat_id = "";
    ImageView iv_dropdown;
    TextView sp_month;
    ImageView btn_back, btn_menu;

    TextView tv_category;
    TextView tv_exp_type;
    TextView tv_amt;
    TextView tv_date;
    TextView tv_memo;
    FloatingActionButton fab_edit;
    String trans_id;
    ImageView img_delete;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        list_transtype.clear();
        list_transtype = db.GetTransactionById(trans_id);

        if (list_transtype.size() > 0) {
            setData();
        }
    }

    private void setData() {
        tv_category.setText(list_transtype.get(0).getCategory());
        tv_exp_type.setText(list_transtype.get(0).getIncome_Expenses());
        tv_amt.setText(list_transtype.get(0).getAmount());
        tv_date.setText(list_transtype.get(0).getDate());
        tv_memo.setText(list_transtype.get(0).getMemo());
    }


    @Override
    protected void InitResources() {
        trans_id = getIntent().getStringExtra("trans_id");

        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Details");
        fab_edit = findViewById(R.id.fab_edit);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        iv_dropdown.setVisibility(View.GONE);
        fab_edit = findViewById(R.id.fab_edit);
        tv_category = findViewById(R.id.tv_category);
        tv_exp_type = findViewById(R.id.tv_exp_type);
        tv_amt = findViewById(R.id.tv_amt);
        tv_date = findViewById(R.id.tv_date);
        tv_memo = findViewById(R.id.tv_memo);
        img_delete = findViewById(R.id.img_delete);
        img_delete.setVisibility(View.VISIBLE);
        fab_edit.setOnClickListener(this);
        img_delete.setOnClickListener(this);

    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_details;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == img_delete) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Are you sure to delete?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            db.deleteTrasactionDataRow(trans_id);
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else if (v == fab_edit) {
            Intent i = new Intent(context, AddExpenseIncome.class);
            i.putExtra("from", TAG);
            i.putExtra("trans_id", trans_id);
            startActivity(i);
            finish();
        }
    }


}
