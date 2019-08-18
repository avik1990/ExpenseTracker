package com.app.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.expensetracker.model.CategoryModel;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.model.Months;
import com.app.expensetracker.model.TransactionType;
import com.app.expensetracker.utility.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME = "expenses.db";// Database name
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private static final String DATABASE_ALTER_TEAM = "ALTER TABLE "
            + ExcelDataModel.TABLE_TRANSACTION + " ADD COLUMN " + ExcelDataModel.COLUMN_MONTH + " string;";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 2);// 1? Its database Version
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DATABASE_ALTER_TEAM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onCreate(db);

    }

    public void InsertExcelDataTO_TempTable(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("tdate", listexceldata.get(i).getDate());
            values.put("ttrans_type", listexceldata.get(i).getIncome_Expenses());
            values.put("tcategory", listexceldata.get(i).getCategory());
            values.put("tmemo", listexceldata.get(i).getMemo());
            values.put("tamount", listexceldata.get(i).getAmount());
            db.insert(ExcelDataModel.TABLE_TEMP_NAME, null, values);
        }
        db.close();
    }

    public void truncateTempTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TEMP_NAME);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }


    public void truncateTransactionTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TRANSACTION);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public List<ExcelDataModel> getDistinctCategory() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ExcelDataModel.TABLE_TEMP_NAME + " GROUP by " + ExcelDataModel.category;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setCategory(cursor.getString(cursor.getColumnIndex("tcategory")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("ttrans_type")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }

    public void InsertCategoryData(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < listexceldata.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("cat_name", listexceldata.get(i).getCategory());
                values.put("trans_type", listexceldata.get(i).getIncome_Expenses());
                db.insert(ExcelDataModel.TABLE_CATEGORY, null, values);
            }
        } catch (Exception e) {

        }
        db.close();
    }

    public void UpdateCategoryTable(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();

        List<ExcelDataModel> listdata = new ArrayList<>();
        listdata.clear();

        for (int i = 0; i < listexceldata.size(); i++) {
            listdata = getAllToDosByTag(listexceldata.get(i).getIncome_Expenses());
            ContentValues values = new ContentValues();
            values.put("trans_type_id", String.valueOf(listdata.get(i).getId()));
            //values.put("trans_type", listexceldata.get(i).getIncome_Expenses());
            db.update(ExcelDataModel.TABLE_CATEGORY, values, "trans_type=" + listdata.get(i).getIncome_Expenses(), null);
        }

       /* for (int i = 0; i < listexceldata.size(); i++) {
        }*/
        db.close();
    }


    public List<ExcelDataModel> getAllToDosByTag(String tag_name) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "SELECT  trans_type_id from tbl_transaction_type where trans_type='" + tag_name + "'";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("trans_type_id")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
            } while (cursor.moveToNext());
        }
        return listdata;
    }


    public List<ExcelDataModel> GetAllCAtegoryData() {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_category";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        return listdata;
    }

    public void UpdateTempTransactionTable(String cat, String cat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cat_id", Integer.parseInt(cat_id));
        //db.update(ExcelDataModel.TABLE_TEMP_NAME, values, "ttrans_type="'" + tag_name + "'", null);
        db.update(ExcelDataModel.TABLE_TEMP_NAME, values, "tcategory='" + cat + "'", null);
        db.close();
    }

    public List<ExcelDataModel> getAllTempDataWIthID() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transaction_temp";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("tdate")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("ttrans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("tcategory")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("tmemo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("tamount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }


    public void InsertDataTO_TransactionTable(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("date", listexceldata.get(i).getDate());
            values.put("trans_type", listexceldata.get(i).getIncome_Expenses());
            values.put("cat_id", listexceldata.get(i).getId());
            values.put("memo", listexceldata.get(i).getMemo());
            values.put("amount", listexceldata.get(i).getAmount().replace("-", ""));

            if (!listexceldata.get(i).getDate().isEmpty()) {
                String d[] = listexceldata.get(i).getDate().split("-");
                String mon = d[1];
                values.put("month", mon);
            } else {
                values.put("month", "");
            }


            db.insert("tbl_transactions", null, values);
        }
        db.close();
    }


    public List<ExcelDataModel> GetAllFinalTransactionData() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }

    public double GetExpensesData(String transtype, String current_month) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and date like '%" + current_month + "%'";

        Log.d("query32112121", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(cursor.getColumnIndex("Total"));
            return total;
        }
        return 0;
    }


    public List<ExcelDataModel> GetAllFinalTransactionDataUsingDate() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }

    public List<ExcelDataModel> getTransactionDetails(String current_month) {
        List<ExcelDataModel> listProd = new ArrayList<>();
        Log.d("CurrentMonthDB", current_month);
        // Utils.listProd_inc.clear();
        //  Utils.listProd1_exp.clear();

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "select distinct date from tbl_transactions where date like '%" + current_month + "%'";
            Log.d("FirstQuery", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String date = c.getString(c.getColumnIndex("date"));
                    String selectQuery1 = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id and date='" + date + "'";
                    Log.d("SecondQuery", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        ExcelDataModel pp_dummy = new ExcelDataModel();
                        pp_dummy.setId("");
                        subCat_list.add(pp_dummy);
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                            prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                            prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                            prod_subcat.setAmount(cursor2.getString(cursor2.getColumnIndex("amount")));
                            prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));

                            /*if (prod_subcat.getIncome_Expenses().equals("Expenses")) {
                                income_amt += Integer.parseInt(prod_subcat.getAmount());
                                prod_subcat.setExp_amt(income_amt);
                                Utils.listProd1_exp.add(income_amt);
                            } else {
                                expense_amt += Integer.parseInt(prod_subcat.getAmount());
                                prod_subcat.setExp_amt(expense_amt);
                                Utils.listProd_inc.add(expense_amt);
                            }*/
                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());
                        foc_cat.setDate(date);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<CategoryModel> GetAllCatgories(String type) {
        List<CategoryModel> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_category where trans_type='" + type + "'";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CategoryModel excelDataModel = new CategoryModel();
                excelDataModel.setCat_id(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCat_name(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setTrans_type(cursor.getString(cursor.getColumnIndex("trans_type")));
                excelDataModel.setCat_iconname(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        return listdata;
    }

    public List<TransactionType> GetAllTransactionType() {
        List<TransactionType> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transaction_type ORDER by trans_type ASC";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionType excelDataModel = new TransactionType();
                excelDataModel.setTrans_id(cursor.getString(cursor.getColumnIndex("trans_type_id")));
                excelDataModel.setTrans_type(cursor.getString(cursor.getColumnIndex("trans_type")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        return listdata;
    }


    public long AddTransactionDataTO_TransactionTable(ExcelDataModel listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", listexceldata.getDate());
        values.put("trans_type", listexceldata.getIncome_Expenses());
        values.put("cat_id", listexceldata.getCat_id());
        values.put("memo", listexceldata.getMemo());
        values.put("amount", listexceldata.getAmount().replace("-", ""));
        values.put("month", listexceldata.getMonth());
        long id = db.insert("tbl_transactions", null, values);
        db.close();
        return id;
    }

    public List<ExcelDataModel> GetTransactionById(String id) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        //String selectQuery = "select * from tbl_transactions where trans_id='" + id + "'";
        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id and a.trans_id='" + id + "'";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                prod_subcat.setAmount(cursor2.getString(cursor2.getColumnIndex("amount")));
                prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                prod_subcat.setCat_id(cursor2.getString(cursor2.getColumnIndex("cat_id")));

                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        return listdata;
    }

    public void deleteTrasactionDataRow(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TRANSACTION + " WHERE trans_id='" + value + "'");
        db.close();
    }

    public long UpdateTransactionDataTO_TransactionTable(ExcelDataModel listexceldata, String trans_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", listexceldata.getDate());
        values.put("trans_type", listexceldata.getIncome_Expenses());
        values.put("cat_id", listexceldata.getCat_id());
        values.put("memo", listexceldata.getMemo());
        values.put("amount", listexceldata.getAmount().replace("-", ""));
        values.put("month", listexceldata.getMonth());
        //  long id = db.insert("tbl_transactions", null, values);
        long id = db.update("tbl_transactions", values, "trans_id=" + trans_id, null);

        db.close();

        return id;
    }

    public List<ExcelDataModel> GetSearchData(String search_text, String st_cat, String trans_type, String from_date, String to_date) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String where = "";
        if (search_text.length() > 0) where += " AND memo LIKE '%" + search_text + "%'";
        if (st_cat.length() > 0) where += " AND cat_name='" + st_cat + "'";
        if (trans_type.length() > 0) where += " AND b.trans_type='" + trans_type + "'";
        if (from_date.length() > 0) where += " AND date='" + from_date + "'";
        if (to_date.length() > 0) where += " AND date='" + to_date + "'";

        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id" + where;

        Log.e("SearchQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                prod_subcat.setAmount(cursor2.getString(cursor2.getColumnIndex("amount")));
                prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                prod_subcat.setCat_id(cursor2.getString(cursor2.getColumnIndex("cat_id")));
                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        return listdata;
    }

}