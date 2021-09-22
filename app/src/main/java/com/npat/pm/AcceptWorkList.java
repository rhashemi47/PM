package com.npat.pm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AcceptWorkList extends AppCompatActivity {
    ListView lstAcceptWork;
    ListAdapter adapter;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    ImageView imgExit;
    long starttime;
    long endtime;
    private boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_work_list);
        lstAcceptWork = (ListView) findViewById(R.id.lstAcceptWork);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        try
        {
            Mobile = getIntent().getStringExtra("Mobile").toString();
        }
        catch (Exception e) {
            Mobile = "0";
        }
        try
        {
            Usercode = getIntent().getStringExtra("Usercode").toString();
        }
        catch (Exception e) {
            Usercode = "0";
        }
        try
        {
            Personcode = getIntent().getStringExtra("Personcode").toString();
        }
        catch (Exception e) {
            Personcode = "0";
        }
        dbh=new DatabaseHelper(getApplicationContext());
        try {

            dbh.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            dbh.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;
        }
        if(db != null) {
            if (db.isOpen()) {
                db.close();
				dbh.close();
            }
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = 0;
//            }
//        }, 2000);
        FillData();
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout();
                finish();
            }
        });
    }

    @SuppressLint("Range")
    private void FillData()
    {

        String Subject,Code,RequestType,WorkType;

        ArrayList<HashMap<String, String>> DataList;
        DataList = new ArrayList<HashMap<String, String>>();

        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("select * from AcceptWork  Order by Code ASC", null);

        if(cursors.getCount() > 0)
        {

            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                try {
                    HashMap<String, String> map = new HashMap<String, String>();

                    Code = cursors.getString(cursors.getColumnIndex("Code"));
                    Subject = cursors.getString(cursors.getColumnIndex("Subject"));
                    RequestType = cursors.getString(cursors.getColumnIndex("RequestType"));
                    WorkType = cursors.getString(cursors.getColumnIndex("WorkType"));

                    map.put("Code", Code);
                    map.put("Subject", Subject);
                    map.put("Mobile", Mobile);
                    map.put("Usercode", Usercode);
                    map.put("Personcode", Personcode);
                    map.put("RequestType", RequestType);
                    map.put("WorkType", WorkType);
                    DataList.add(map);
                }
                catch (Exception ex)
                {

                }

            }
            adapter = new AdapterListWorks(this, DataList);
            lstAcceptWork.setAdapter(adapter);
        }
        else
        {
            HashMap<String, String> map = new HashMap<String, String>();
            adapter = new AdapterListWorks(this, DataList);
            lstAcceptWork.setAdapter(adapter);
            //Toast.makeText(getApplicationContext(), "اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید", Toast.LENGTH_LONG).show();
        }
        cursors.close();
        db.close();
		dbh.close();
    }
//    @Override
//    public void onBackPressed() {
//        this.doubleBackToExitPressedOnce += 1;
//        if (doubleBackToExitPressedOnce > 1) {
//            super.onBackPressed();
//            finish();
//            return;
//        }
//        else
//        {
//            LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
//        }
//    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if  ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            return true;
        }
        else if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0 ) {
            finish();
             return true;
        }
//        if  ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
//            if (!doubleBackToExitPressedOnce) {
//                starttime = System.currentTimeMillis();
//                //Log.d("Main Java Log", "Start time : " + String.valueOf(starttime));
//                doubleBackToExitPressedOnce = true;
//                return true;
//            } else {
//                endtime = System.currentTimeMillis();
//                //Log.d("Main Java Log", "End time : " + String.valueOf(endtime));
//                if (endtime - starttime <= 500) { // time interval for second click
//                    doubleBackToExitPressedOnce = false;
//                    event.startTracking();
//                    finish();
//                    return true;
//                }
//                else
//                {
//                    LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
//                }
//            }
//        }
        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        startActivity(intent);
        this.finish();
    }
}