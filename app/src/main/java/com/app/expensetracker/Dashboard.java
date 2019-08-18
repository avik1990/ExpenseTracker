package com.app.expensetracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.expensetracker.adapter.MonthAdapter;
import com.app.expensetracker.adapter.TransExpAdapter;
import com.app.expensetracker.adapter.TransactionAdapter;
import com.app.expensetracker.database.DatabaseHelper;
import com.app.expensetracker.model.ExcelDataModel;
import com.app.expensetracker.utility.BaseActivity;
import com.app.expensetracker.utility.CheckForSDCard;
import com.app.expensetracker.utility.FileUtils;
import com.app.expensetracker.utility.PathUtil;
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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class Dashboard extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, MonthAdapter.GetMonthFromAdapter {

    private static final String TAG = "Dashboard";
    FloatingActionButton fab_add;
    LinearLayout ll_monthView;
    Context context;
    MonthAdapter monthAdapter;
    RecyclerView rl_recyclerview, transaction_recyclerview;
    RelativeLayout calenderview;
    DatabaseHelper db;
    List<ExcelDataModel> list_exceldata = new ArrayList<>();
    List<ExcelDataModel> list_exceldataCat = new ArrayList<>();
    List<ExcelDataModel> list_categoryData = new ArrayList<>();
    List<ExcelDataModel> list_temp_categoryData_withID = new ArrayList<>();
    List<ExcelDataModel> list_final_transaction_data = new ArrayList<>();
    TransactionAdapter transactionAdapter;
    ProgressDialog p;
    TextView tv_expenses, tv_income;
    NavigationView navigationView;
    Intent intent;
    TransExpAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    RelativeLayout rl_calender_mainview;
    TextView sp_month, tv_currentyear;
    String current_month = "";
    String current_year = "";
    ImageView btn_menu;
    DrawerLayout drawer;
    ImageView tv_year_back;
    ImageView tv_year_forward;
    String st_year = "";

    SimpleDateFormat dateFormat;
    Calendar cal;
    String mCurrentPhotoPath;
    public final String downloadDirectory = "TransExpense";
    File apkStorage = null;
    File outputFile = null;

    ImageView img_search;
    TextView tv_balance;
    double st_income = 0, st_expense = 0;
    File originalFile;

    //create Excel Sheet


    @Override
    protected void InitListner() {
        context = this;
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        cal = Calendar.getInstance();

        if (new CheckForSDCard().isSDCardPresent()) {
            apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + downloadDirectory);
        } else {
            Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
        }

        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }


        db = new DatabaseHelper(context);
        fab_add.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);

        current_year = Utils.getCurrentYear();
        current_month = current_year + "-" + Utils.getCurrentMonthsIndex(Utils.getCurrentMonths());
        //Log.d("CurrentMonth", current_month);

        try {
            db.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read Data from Assets Folder
    private void readdatafromExcelData() {
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel sheet
            myInput = assetManager.open("expense_data.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            //textView.append("\n");
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            date = myCell.toString();
                        } else if (colno == 1) {
                            Income_Expenses = myCell.toString();
                        } else if (colno == 2) {
                            Category = myCell.toString();
                        } else if (colno == 3) {
                            Memo = myCell.toString();
                        } else if (colno == 4) {
                            Amount = myCell.toString();
                        }
                        colno++;
                        //  Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount));
                    //  Log.d("ExcelData", date + " " + Income_Expenses + " " + Category + " " + Memo + " " + Amount + "\n");
                    // textView.append(sno + " -- " + date + "  -- " + det + "\n");
                }
                rowno++;
            }
        } catch (
                Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    protected void InitResources() {
        drawer = findViewById(R.id.drawer_layout);
        btn_menu = findViewById(R.id.btn_menu);
        tv_balance = findViewById(R.id.tv_balance);
        ///year
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_year_back = findViewById(R.id.tv_year_back);
        tv_year_forward = findViewById(R.id.tv_year_forward);
        img_search = findViewById(R.id.img_search);
        img_search.setVisibility(View.VISIBLE);
        st_year = Utils.getCurrentYear();
        tv_currentyear.setText(st_year);
        tv_year_back.setOnClickListener(this);
        tv_year_forward.setOnClickListener(this);
        ////
        img_search.setOnClickListener(this);
        rl_calender_mainview = findViewById(R.id.rl_calender_mainview);
        rl_calender_mainview.setOnClickListener(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        expandableListView = findViewById(R.id.expandableListView);
        rl_recyclerview = findViewById(R.id.rl_recyclerview);
        tv_expenses = findViewById(R.id.tv_expenses);
        tv_income = findViewById(R.id.tv_income);
        fab_add = findViewById(R.id.fab_add);
        ll_monthView = findViewById(R.id.ll_monthView);
        transaction_recyclerview = findViewById(R.id.transaction_recyclerview);
        rl_recyclerview.setLayoutManager(new GridLayoutManager(context, 6));
        transaction_recyclerview.setLayoutManager(new LinearLayoutManager(context));
        calenderview = findViewById(R.id.calenderview);
        sp_month = findViewById(R.id.sp_month);

        sp_month.setText(Utils.getCurrentMonths());
        tv_currentyear.setText(Utils.getCurrentYear());
        btn_menu.setOnClickListener(this);
    }

    @Override
    protected void InitPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    //if (!Utils.getIsExcelReadDone(context))
                    //  new AsyncTaskExample().execute();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .onSameThread()
                .check();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main_inc;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == fab_add) {
            Intent i = new Intent(context, AddExpenseIncome.class);
            i.putExtra("from", TAG);
            startActivity(i);
        } else if (v == ll_monthView) {
            fab_add.setVisibility(View.GONE);
            calenderview.setVisibility(View.VISIBLE);
            monthAdapter = new MonthAdapter(context, Utils.getAllMonths(), this, current_month);
            rl_recyclerview.setAdapter(monthAdapter);
        } else if (v == rl_calender_mainview) {
            fab_add.setVisibility(View.VISIBLE);
            calenderview.setVisibility(View.GONE);
        } else if (v == btn_menu) {
            drawer.openDrawer(Gravity.START); //Edit Gravity.START need API 14
        } else if (v == tv_year_back) {
            int year = Integer.parseInt(st_year);
            year--;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = st_year;
        } else if (v == tv_year_forward) {
            int year = Integer.parseInt(st_year);
            year++;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = st_year;
        } else if (v == img_search) {
            Intent i = new Intent(context, ActivitySearch.class);
            startActivity(i);
        }
    }


    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();
            list_exceldata.clear();
            importExcelData(originalFile);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_exceldata.size() > 0) {
                db.truncateTempTable();
                db.InsertExcelDataTO_TempTable(list_exceldata);
            }

            new AsyncTaskInsertToCategoryTbl().execute();
        }
    }

    private class AsyncTaskInsertToCategoryTbl extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_exceldataCat.clear();
            list_exceldataCat = db.getDistinctCategory();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_exceldataCat.size() > 0) {
                db.InsertCategoryData(list_exceldataCat);
                new AsyncTaskUpdateCategoryTempTable().execute();
            }
            // p.dismiss();
        }
    }

    private class AsyncTaskUpdateCategoryTempTable extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_categoryData = db.GetAllCAtegoryData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            List<String> list_cats_local = new ArrayList<>();
            List<String> list_ids_local = new ArrayList<>();
            List<String> list_category_data = new ArrayList<>();
            if (list_categoryData.size() > 0) {
                list_cats_local.clear();
                list_ids_local.clear();
                for (int i = 0; i < list_categoryData.size(); i++) {
                    Log.d("DATA", list_categoryData.get(i).getCategory());
                    list_cats_local.add(list_categoryData.get(i).getCategory());
                    list_ids_local.add(list_categoryData.get(i).getId());
                }

                for (int j = 0; j < list_exceldata.size(); j++) {
                    list_category_data.add(list_exceldata.get(j).getCategory());
                }

                for (int k = 0; k < list_category_data.size(); k++) {
                    String catdata = list_category_data.get(k);
                    if (list_cats_local.contains(catdata)) {
                        String id = list_ids_local.get(list_cats_local.indexOf(catdata));
                        Log.d("IDSSSSSS", k + " : " + id);
                        db.UpdateTempTransactionTable(catdata, id);
                    }
                }

                new AsyncTaskInsertDataToTransactionTable().execute();
                //p.dismiss();
            }
        }
    }

    private class AsyncTaskInsertDataToTransactionTable extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_temp_categoryData_withID = db.getAllTempDataWIthID();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_temp_categoryData_withID.size() > 0) {
                db.truncateTransactionTable();
                db.InsertDataTO_TransactionTable(list_temp_categoryData_withID);
                new AsyncTaskGetTransactionData().execute();
            }
            // p.dismiss();
        }
    }


    private class AsyncTaskGetTransactionData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_final_transaction_data = db.getTransactionDetails(current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_final_transaction_data.size() > 0) {
                // transactionAdapter = new TransactionAdapter(context, list_final_transaction_data);
                // transaction_recyclerview.setAdapter(transactionAdapter);
                expandableListAdapter = new TransExpAdapter(context, list_final_transaction_data);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setVisibility(View.VISIBLE);
                new AsyncTaskSummationExpenses().execute();
            } else {
                tv_income.setText("0");
                tv_expenses.setText("0");
                tv_balance.setText("0");
                p.dismiss();
                expandableListView.setVisibility(View.GONE);
            }
        }
    }

    private class AsyncTaskSummationExpenses extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            st_income = 0;
            st_income = db.GetExpensesData("Expenses", current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv_expenses.setText(String.valueOf(new DecimalFormat("##.##").format(st_income)).replace("-", "").trim());
            new AsyncTaskIncomeExpenses().execute();
        }
    }

    private class AsyncTaskIncomeExpenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            st_expense = 0;
            st_expense = db.GetExpensesData("Income", current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Utils.setIsExcelReadDone(context, true);
            tv_income.setText(String.valueOf(new DecimalFormat("##.##").format(st_expense)).replace("-", "").trim());
            if (st_income < st_expense) {
                tv_balance.setText(String.valueOf(new DecimalFormat("##.##").format(st_income - st_expense)));
            } else {
                tv_balance.setText(String.valueOf(new DecimalFormat("##.##").format((st_income - st_expense))));
            }
            p.dismiss();
        }
    }

   /* public static String DoubleFormat(double number) {
        DecimalFormat df = new DecimalFormat("##.##");
        //df.setRoundingMode(RoundingMode.HALF_UP);
       // String formattedValue = df.format(number);
       // formattedValue = formattedValue.replaceAll("^(?=0(\\.0*)?$)", "");
        return formattedValue;
    }*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        openNavDrawer(id, context);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openNavDrawer(int id, final Context mContext) {
        if (id == R.id.nav_import) {
            /*Intent intent = new Intent()
                    .setType("file/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 7);*/

            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);
            // intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 7);
        }
        if (id == R.id.nav_export) {
            new AsyncTaskExportData().execute();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri uri = data.getData();
                        //Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
                        originalFile = new File(FileUtils.getRealPath(this, uri));
                        Log.d("FilePATHS", uri.toString());

                        new AsyncTaskExample().execute();

                        //openFile(context, originalFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public void openFile(Context context, File url) throws IOException {
        // Create URI
        // File file = url;
        //   Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public String returnedMonth(String month) {
        current_month = current_year + "-" + month;
        sp_month.setText(Utils.getCurrentMonths(month));
        fab_add.setVisibility(View.VISIBLE);
        calenderview.setVisibility(View.GONE);
        new AsyncTaskGetTransactionData().execute();
        return month;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //Utils.ShowToast(context, current_month);
            new AsyncTaskGetTransactionDataonResume().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class AsyncTaskGetTransactionDataonResume extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                list_final_transaction_data = db.getTransactionDetails(current_month);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_final_transaction_data.size() > 0) {
                expandableListAdapter = new TransExpAdapter(context, list_final_transaction_data);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setVisibility(View.VISIBLE);
                new AsyncTaskSummationExpenses().execute();
            } else {
                expandableListView.setVisibility(View.GONE);
            }
        }
    }

   /* public void ExportExcelData() {
        List<ExcelDataModel> list_final_transaction_data = new ArrayList<>();
        list_final_transaction_data.clear();
        //List<ExcelDataModel> list = ReadExcelFileToList.readExcelData("Sample.xlsx");
        list_final_transaction_data = db.getTransactionDetails(current_month);
        try {
            writeCountryListToFile("Expense_tracker_export.xls", list_final_transaction_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public void writeCountryListToFile(String fileName, List<ExcelDataModel> countryList) throws Exception {
        Workbook workbook = null;

        if (fileName.endsWith("xlsx")) {
        } else if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new Exception("invalid file name, should be xls or xlsx");
        }

        Sheet sheet = workbook.createSheet("Expense_tracker_export");

        Iterator<ExcelDataModel> iterator = countryList.iterator();

        int rowIndex = 0;
        while (iterator.hasNext()) {
            ExcelDataModel country = iterator.next();
            Row row = sheet.createRow(rowIndex++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(country.getCategory());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(country.getIncome_Expenses());
        }
        String filePath = getFilesDir().getPath().toString() + "/Expense_tracker_export.xls";
        File f = new File(filePath);
        //lets write the excel data to file now
        FileOutputStream fos = new FileOutputStream(f);
        workbook.write(fos);
        fos.close();
        System.out.println(fileName + " written successfully");
    }


    private void importExcelData(File file) {
        try {
            //InputStream myInput;
            // initialize asset manager
            //AssetManager assetManager = getAssets();
            //  open excel sheet
            //myInput = assetManager.open("expense_data.xls");
            // Create a POI File System object
            InputStream inputStream = new FileInputStream(file);

            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            //textView.append("\n");
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            date = myCell.toString();
                        } else if (colno == 1) {
                            Income_Expenses = myCell.toString();
                        } else if (colno == 2) {
                            Category = myCell.toString();
                        } else if (colno == 3) {
                            Memo = myCell.toString();
                        } else if (colno == 4) {
                            Amount = myCell.toString();
                        }
                        colno++;
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount));
                    //Log.d("ExcelData", date + " " + Income_Expenses + " " + Category + " " + Memo + " " + Amount + "\n");
                    // textView.append(sno + " -- " + date + "  -- " + det + "\n");
                }
                rowno++;
            }
        } catch (
                Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }
/*data.put("1", new Object[]{ "Date", "Income/Expense", "Category","Memo","Amount" });
        data.put("2", new Object[]{ 1, "Pankaj", "Kumar" });*/

    public void ExportToExcelSheet() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        Map<String, Object[]> data = new TreeMap<>();
        data.put("1", new Object[]{"Date", "Income/Expenses", "Category", "Memo", "Amount"});
        //data.put(String.valueOf(2), new Object[]{1, "Pankaj", "Kumar"});
        List<ExcelDataModel> a = new ArrayList<>();
        a.clear();
        a = db.GetAllFinalTransactionData();

        if (a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                data.put(String.valueOf(i + 2), new Object[]{a.get(i).getDate(), a.get(i).getIncome_Expenses(), a.get(i).getCategory(), a.get(i).getMemo(), a.get(i).getAmount()});
            }


            Set<String> keyset = data.keySet();
            int rownum = 0;
            for (String key : keyset) {
                // this creates a new row in the sheet
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    // this line creates a cell in the next column of that row
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String)
                        cell.setCellValue((String) obj);
                    else if (obj instanceof Integer)
                        cell.setCellValue((Integer) obj);
                }
            }

            outputFile = new File(apkStorage, dateFormat.format(cal.getTime()) + ".xls");//Create Output file in Main File

            try {
                FileOutputStream out = new FileOutputStream(outputFile);
                workbook.write(out);
                out.close();
                //Create New File if not present
                /*FileOutputStream fos = new FileOutputStream(outputFile);
                  fos.close();*/
                p.dismiss();
                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        //pDialog.dismiss();
                        try {
                            Utils.shareAll(context, outputFile);
                            // openFile(context, outputFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 6000);

            } catch (Exception e) {

            }
        } else {
            Utils.ShowToast(context, "No data for Export");
        }
    }


    private class AsyncTaskExportData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ExportToExcelSheet();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // p.dismiss();

        }
    }


}