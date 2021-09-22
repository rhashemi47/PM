package com.npat.pm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class RequestList extends AppCompatActivity {

    ListView lstRequest;
    ImageView imgQRcode;
    private PreviewView previewView;
    ListAdapter adapter;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    ImageView imgExit;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_request);
        lstRequest = (ListView) findViewById(R.id.lstRequest);
        imgQRcode = (ImageView) findViewById(R.id.imgQRcode);
        previewView = findViewById(R.id.activity_main_previewView);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
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
        FillData();
        imgQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(GetBarcode.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
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

        String Subject,Code;

        ArrayList<HashMap<String, String>> DataList;
        DataList = new ArrayList<HashMap<String, String>>();
        Cursor cursors;
        db = dbh.getReadableDatabase();
        cursors = db.rawQuery("select * from Request Order by Code ASC", null);

        if(cursors.getCount() > 0)
        {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                try {
                    HashMap<String, String> map = new HashMap<String, String>();

                    Code = cursors.getString(cursors.getColumnIndex("Code"));
                    Subject = cursors.getString(cursors.getColumnIndex("Subject"));

                    map.put("Code", Code);
                    map.put("Subject", Subject);
                    map.put("Mobile", Mobile);
                    map.put("Usercode", Usercode);
                    map.put("Personcode", Personcode);
                    DataList.add(map);
                }
                catch (Exception ex)
                {

                }

            }
            adapter = new AdapterListRequest(this, DataList);
            lstRequest.setAdapter(adapter);
        }
        else
        {
            HashMap<String, String> map = new HashMap<String, String>();
            adapter = new AdapterListMyWorks(this, DataList);
            lstRequest.setAdapter(adapter);
            //Toast.makeText(getApplicationContext(), "اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید", Toast.LENGTH_LONG).show();
        }
        cursors.close();
        db.close();
				dbh.close();
    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            return true;
        }

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