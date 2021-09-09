package com.npat.pm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ListHamkaran extends AppCompatActivity {

    String WorkCode;
    String Usercode;
    String Personcode;
    String Mobile;
    String Description;
    String DescriptionReport;
    String ImgReport;
    SearchView SearchHamkar;
    ListView lstHamkaran;
    ListAdapter adapter;
    DatabaseHelper dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_hamkaran);

        lstHamkaran = (ListView) findViewById(R.id.lstHamkaran);
        try
        {
            WorkCode = getIntent().getStringExtra("WorkCode").toString();
        }
        catch (Exception e) {
            WorkCode = "0";
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
        try
        {
            Description = getIntent().getStringExtra("Description").toString();
        }
        catch (Exception e) {
            Description = "0";
        }
        try
        {
            Mobile = getIntent().getStringExtra("Mobile").toString();
        }
        catch (Exception e) {
            Mobile = "0";
        }
        try
        {
            DescriptionReport = getIntent().getStringExtra("DescriptionReport").toString();
        }
        catch (Exception e) {
            DescriptionReport = "";
        }
        try
        {
            ImgReport = getIntent().getStringExtra("ImgReport").toString();
        }
        catch (Exception e) {
            ImgReport = " ";
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

        SearchHamkar = (SearchView) findViewById(R.id.SearchHamkar);


        FillData("");

        SearchHamkar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                FillData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                FillData(s);
                return false;
            }
        });
    }

    @SuppressLint("Range")
    private void FillData(String NameKamkar)
    {

        String Name,Code;
        Cursor cursors;
        ArrayList<HashMap<String, String>> DataList;
        DataList = new ArrayList<HashMap<String, String>>();

        db = dbh.getReadableDatabase();
        if(NameKamkar.compareTo("")==0)
        {
            cursors = db.rawQuery("select * from Hamkaran  Order by Code ASC", null);
        }
        else
        {
            cursors = db.rawQuery("select * from Hamkaran Where Name Like '%" + NameKamkar + "%' Order by Code ASC", null);
        }

        if(cursors.getCount() > 0)
        {

            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                try {
                    HashMap<String, String> map = new HashMap<String, String>();

                    Code = cursors.getString(cursors.getColumnIndex("Code"));
                    Name = cursors.getString(cursors.getColumnIndex("Name"));

                    map.put("Code", Code);
                    map.put("Name", Name);
                    map.put("UserCode", Usercode);
                    map.put("WorkCode", WorkCode);
                    map.put("Description", Description);
                    map.put("DescriptionReport", DescriptionReport);
                    map.put("ImgReport", ImgReport);
                    map.put("Mobile", Mobile);
                    map.put("Personcode", Personcode);
                    DataList.add(map);
                }
                catch (Exception ex)
                {

                }
            }
            adapter = new AdapterListHamkaran(this, DataList);
            lstHamkaran.setAdapter(adapter);
        }
        else
        {
            HashMap<String, String> map = new HashMap<String, String>();
            adapter = new AdapterListHamkaran(this, DataList);
            lstHamkaran.setAdapter(adapter);
            //Toast.makeText(getApplicationContext(), "اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید", Toast.LENGTH_LONG).show();
        }
        cursors.close();
        db.close();
		dbh.close();
    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(MyWork.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", WorkCode);
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        startActivity(intent);
        this.finish();
    }
}