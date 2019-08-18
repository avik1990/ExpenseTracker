package com.app.expensetracker.model;

import java.util.List;

public class ExcelDataModel {

    public static String expense_type = "expense_type";
    public static String category = "tcategory";
    public static String TABLE_TEMP_NAME = "tbl_transaction_temp";
    public static String TABLE_TRANSACTION = "tbl_transactions";
    public static String TABLE_CATEGORY = "tbl_category";
    public static String COLUMN_AMOUNT = "amount";
    public List<ExcelDataModel> GroupFocAll;
    public static String COLUMN_MONTH = "month";


    String month = "";
    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", id = "",cat_id;
    int exp_amt = 0, inc_amt = 0;

    public ExcelDataModel() {
    }

    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount) {
        this.date = date;
        Income_Expenses = income_Expenses;
        Category = category;
        Memo = memo;
        Amount = amount;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getExp_amt() {
        return exp_amt;
    }

    public void setExp_amt(int exp_amt) {
        this.exp_amt = exp_amt;
    }

    public int getInc_amt() {
        return inc_amt;
    }

    public void setInc_amt(int inc_amt) {
        this.inc_amt = inc_amt;
    }

    public List<ExcelDataModel> getGroupFocAll() {
        return GroupFocAll;
    }

    public void setGroupFocAll(List<ExcelDataModel> groupFocAll) {
        GroupFocAll = groupFocAll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIncome_Expenses() {
        return Income_Expenses;
    }

    public void setIncome_Expenses(String income_Expenses) {
        Income_Expenses = income_Expenses;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
