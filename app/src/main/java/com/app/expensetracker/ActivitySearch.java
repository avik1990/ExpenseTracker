package com.app.expensetracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.expensetracker.adapter.TransactionAdapter;
import com.app.expensetracker.database.DatabaseHelper;
import com.app.expensetracker.model.CategoryModel;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.model.TransactionType;
import com.app.expensetracker.utility.BaseActivity;
import com.app.expensetracker.utility.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivitySearch extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivitySearch";
    Context context;
    DatabaseHelper db;
    String cat_id = "";

    LinearLayout ll_monthView;
    TextView sp_month;
    ImageView btn_back, btn_menu;

    EditText tv_search_text;
    Spinner sp_category, sp_transtype;
    Button btn_fromdata, btn_todata, btnSearch;
    List<ExcelDataModel> list_categoryData = new ArrayList<>();
    Calendar myCalendar = Calendar.getInstance();
    String startDate = "", endDate = "";
    String search_text = "", st_sp_cat = "", st_sp_transtype = "";
    String from_date = "", to_date = "";
    TransactionAdapter transactionAdapter;
    List<ExcelDataModel> list_search_data = new ArrayList<>();
    RecyclerView cat_recyclerview;
    ImageView iv_dropdown;
    TextView tv_search;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        list_categoryData = db.GetAllCAtegoryData();

        if (list_categoryData.size() > 0) {
            String category_arr[] = new String[list_categoryData.size() + 1];
            category_arr[0] = "All";
            for (int i = 0; i < list_categoryData.size(); i++) {
                category_arr[i + 1] = list_categoryData.get(i).getCategory();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_arr);            //Specify the layout to use when the list of choices appear
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_category.setAdapter(adapter);

            sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    st_sp_cat = sp_category.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }

        sp_transtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                st_sp_transtype = sp_transtype.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        // startDate = Utils.getCurrentDate();
        // endDate = Utils.getCurrentDate();
    }


    @Override
    protected void InitResources() {
        cat_recyclerview = findViewById(R.id.cat_recyclerview);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        tv_search = findViewById(R.id.tv_search);
        cat_recyclerview.setLayoutManager(new LinearLayoutManager(context));
        ll_monthView = findViewById(R.id.ll_monthView);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Search");
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        tv_search_text = findViewById(R.id.tv_search_text);
        sp_category = findViewById(R.id.sp_category);
        sp_transtype = findViewById(R.id.sp_transtype);
        btn_fromdata = findViewById(R.id.btn_fromdata);
        btn_todata = findViewById(R.id.btn_todata);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(this);
        btn_fromdata.setOnClickListener(this);
        btn_todata.setOnClickListener(this);
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == ll_monthView) {
        } else if (v == btn_back) {
            finish();
        } else if (v == btn_todata) {
            DatePickerDialog todateDialog = new DatePickerDialog(context, todate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            try {
                String st_fromdate = btn_fromdata.getText().toString().replace("From : ", "").trim();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(st_fromdate);
                    long millis = date.getTime();
                    todateDialog.getDatePicker().setMinDate(millis);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            todateDialog.show();

        } else if (v == btn_fromdata) {
            DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        } else if (v == btnSearch) {
            search_text = tv_search_text.getText().toString().trim();
            Log.d("CatData", search_text + " " + st_sp_cat + " " + st_sp_transtype + " " + startDate + " " + endDate);
            list_search_data.clear();
            list_search_data = db.GetSearchData(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate);
            if (list_search_data.size() > 0) {
                tv_search.setVisibility(View.VISIBLE);
                cat_recyclerview.setVisibility(View.VISIBLE);
                transactionAdapter = new TransactionAdapter(context, list_search_data);
                cat_recyclerview.setAdapter(transactionAdapter);
            } else {
                tv_search.setVisibility(View.GONE);
                cat_recyclerview.setVisibility(View.GONE);
                Utils.ShowToast(context, "No data available");
            }
        }
    }

    DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }
            startDate = years + "-" + month1 + "-" + dayOfMonth;
            btn_fromdata.setText("From : " + startDate);
        }
    };

    DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }
            endDate = years + "-" + month1 + "-" + dayOfMonth;
            btn_todata.setText("To : " + endDate);
        }
    };
}
