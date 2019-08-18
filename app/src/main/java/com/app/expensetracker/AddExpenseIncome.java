package com.app.expensetracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.expensetracker.adapter.CategoryAdapter;
import com.app.expensetracker.adapter.MonthAdapter;
import com.app.expensetracker.adapter.TransExpAdapter;
import com.app.expensetracker.adapter.TransactionAdapter;
import com.app.expensetracker.database.DatabaseHelper;
import com.app.expensetracker.model.CategoryModel;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.model.TransactionType;
import com.app.expensetracker.utility.BaseActivity;
import com.app.expensetracker.utility.Utils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.apache.poi.hpsf.Util;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AddExpenseIncome extends BaseActivity implements View.OnClickListener, CategoryAdapter.GetMonthFromAdapter {

    private static final String TAG = "AddExpenseIncome";
    Context context;
    DatabaseHelper db;
    List<CategoryModel> listcat = new ArrayList<>();
    List<TransactionType> list_transtype = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    RecyclerView cat_recyclerview;
    Calendar myCalendar = Calendar.getInstance();
    String cat_id = "";

    LinearLayout ll_monthView;
    TextView sp_month;
    String transaction_type = "";
    ImageView btn_back, btn_menu;

    /////calculator view
    EditText et_memo;
    TextView et_sum;
    TextView tv_7;
    TextView tv_8;
    TextView tv_9;
    TextView tv_date;
    TextView tv_4;
    TextView tv_5;
    TextView tv_6;
    TextView tv_plus;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;
    TextView tv_minus;
    TextView tv_decimal;
    TextView tv_0;
    ImageView tv_clr;
    ImageView iv_equals;
    LinearLayout ll_submit;
    String current_date = "";
    //////////////////////////////

    List<ExcelDataModel> list_from_transtype = new ArrayList<>();

    String trans_id = "";
    String from = "";

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        ll_monthView.setOnClickListener(this);

        if (from.equalsIgnoreCase("Dashboard")) {
            listcat.clear();
            list_transtype.clear();
            list_transtype = db.GetAllTransactionType();

            if (list_transtype.size() > 0) {
                sp_month.setText(list_transtype.get(0).getTrans_type());
                transaction_type = list_transtype.get(0).getTrans_type();
                loadCategories(list_transtype.get(0).getTrans_type());
            }

        } else if (from.equalsIgnoreCase("ExpenseDetails")) {
            trans_id = getIntent().getStringExtra("trans_id");
            list_from_transtype.clear();
            list_from_transtype = db.GetTransactionById(trans_id);
            listcat.clear();
            list_transtype.clear();
            list_transtype = db.GetAllTransactionType();
            cat_id = list_from_transtype.get(0).getCat_id();
            current_date = list_from_transtype.get(0).getDate();
            //Utils.ShowToast(context, cat_id);
            if (list_transtype.size() > 0) {
                sp_month.setText(list_from_transtype.get(0).getIncome_Expenses());
                transaction_type = list_from_transtype.get(0).getIncome_Expenses();
                loadCategories(list_from_transtype.get(0).getIncome_Expenses());
            }

            setCalculatorData();
        }
        calculator();


    }

    private void setCalculatorData() {
        et_memo.setText(list_from_transtype.get(0).getMemo());
        et_sum.setText(list_from_transtype.get(0).getAmount());
        tv_date.setText(list_from_transtype.get(0).getDate());
    }

    private void loadCategories(String trans_type) {
        listcat.clear();
        listcat = db.GetAllCatgories(trans_type);
        if (listcat.size() > 0) {
            categoryAdapter = new CategoryAdapter(context, listcat, this, cat_id);
            cat_recyclerview.setAdapter(categoryAdapter);
        }
    }


    @Override
    protected void InitResources() {
        from = getIntent().getStringExtra("from");
        current_date = Utils.getCurrentDate();
        cat_recyclerview = findViewById(R.id.cat_recyclerview);
        cat_recyclerview.setLayoutManager(new GridLayoutManager(context, 4));

        ll_monthView = findViewById(R.id.ll_monthView);
        sp_month = findViewById(R.id.sp_month);

        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        //calculator
        et_memo = findViewById(R.id.et_memo);
        et_sum = findViewById(R.id.et_sum);
        ll_submit = findViewById(R.id.ll_submit);
        tv_7 = findViewById(R.id.tv_7);
        tv_8 = findViewById(R.id.tv_8);
        tv_9 = findViewById(R.id.tv_9);
        tv_date = findViewById(R.id.tv_date);
        tv_4 = findViewById(R.id.tv_4);
        tv_5 = findViewById(R.id.tv_5);
        tv_6 = findViewById(R.id.tv_6);
        tv_plus = findViewById(R.id.tv_plus);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        tv_0 = findViewById(R.id.tv_0);

        tv_minus = findViewById(R.id.tv_minus);
        tv_decimal = findViewById(R.id.tv_decimal);
        tv_clr = findViewById(R.id.tv_clr);
        iv_equals = findViewById(R.id.iv_equals);


    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_addexpense;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == ll_monthView) {
            if (list_transtype.size() > 0) {
                PopupMenu popup = new PopupMenu(context, ll_monthView);
                for (int i = 0; i < list_transtype.size(); i++) {
                    popup.getMenu().add(list_transtype.get(i).getTrans_type());
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        cat_id = "";
                        transaction_type = item.getTitle().toString();
                        loadCategories(transaction_type);
                        sp_month.setText(transaction_type);
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        } else if (v == btn_back) {
            finish();
        }
    }


    @Override
    public void returnedMonth(String cat_id) {
        this.cat_id = cat_id;
        if (listcat.size() > 0) {
            categoryAdapter = new CategoryAdapter(context, listcat, this, cat_id);
            cat_recyclerview.setAdapter(categoryAdapter);
        }
    }


    public void calculator() {
        tv_date.setText(current_date);
        et_sum = findViewById(R.id.et_sum);

        tv_7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // String sum_val=et_sum.getText().toString().trim();
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }
                et_sum.append("7");
            }
        });
        tv_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("8");
            }
        });
        tv_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("9");
            }
        });
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //  dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();

            }
        });
        tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("4");
            }
        });
        tv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("5");
            }
        });
        tv_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("6");
            }
        });
        tv_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                if (et_sum.getText().toString().equalsIgnoreCase("-")) {
                    et_sum.setText("+");
                }

                if (et_sum.getText().length() > 0) {
                    Character value = et_sum.getText().toString().charAt(et_sum.getText().toString().length() - 1);

                    if (value.toString().equalsIgnoreCase("+")) {
                        return;
                    }
                    if (value.toString().equalsIgnoreCase("-")) {
                        return;
                    }

                    et_sum.append("+");

                    try {
                        if (et_sum.getText().toString().contains("+")) {
                            String val[] = et_sum.getText().toString().split("\\+");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) + Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                if (et_sum.getText().toString().equalsIgnoreCase("+")) {
                    et_sum.setText("-");
                }

                if (et_sum.getText().toString().length() > 0) {
                    Character value = et_sum.getText().toString().charAt(et_sum.getText().toString().length() - 1);

                    if (value.toString().equalsIgnoreCase("+")) {
                        return;
                    }
                    if (value.toString().equalsIgnoreCase("-")) {
                        return;
                    }


                    et_sum.append("-");

                    try {
                        if (et_sum.getText().toString().contains("-")) {
                            String val[] = et_sum.getText().toString().split("\\-");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) - Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        }

                    } catch (Exception e) {

                    }
                }


            }
        });
        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("1");
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("2");
            }
        });
        tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }
                et_sum.append("3");
            }
        });

        tv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_sum.append("0");
            }
        });
        tv_decimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_sum.getText().toString().contains(".")) {
                    et_sum.append(".");
                }
            }
        });

        tv_clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = et_sum.getText().toString();
                if (!value.isEmpty()) {
                    //if (!value.equalsIgnoreCase("0")) {
                    value = value.substring(0, value.length() - 1);
                    if (!value.isEmpty()) {
                        et_sum.setText(value);
                    } else {
                        et_sum.setText("0");
                    }
                } else {
                    et_sum.setText("0");
                }
            }
        });

        ll_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cat_id.isEmpty()) {
                    Utils.ShowToast(context, "Select a category");
                    return;
                }
                if (et_memo.getText().toString().isEmpty()) {
                    Utils.ShowToast(context, "Enter Memo");
                    return;
                }
                if (et_sum.getText().toString().isEmpty()) {
                    Utils.ShowToast(context, "Enter Expense/Income Amount");
                    return;
                }

                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setDate(tv_date.getText().toString().trim());
                excelDataModel.setIncome_Expenses(transaction_type);
                excelDataModel.setCat_id(cat_id);
                excelDataModel.setMemo(et_memo.getText().toString().trim());
                excelDataModel.setAmount(et_sum.getText().toString().trim());
                if (!current_date.isEmpty()) {
                    String d[] = current_date.split("-");
                    String mon = d[1];
                    excelDataModel.setMonth(mon);
                }
                if (from.equalsIgnoreCase("Dashboard")) {
                    db.AddTransactionDataTO_TransactionTable(excelDataModel);
                } else if (from.equalsIgnoreCase("ExpenseDetails")) {
                    db.UpdateTransactionDataTO_TransactionTable(excelDataModel, trans_id);
                }
                finish();
            }
        });
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
            //String startDate = dayOfMonth + "/" + month1 + "/" + years;
            String startDate = years + "-" + month1 + "-" + dayOfMonth;
            tv_date.setText(startDate);
        }
    };
}
