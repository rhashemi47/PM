package com.npat.pm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Size;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MyWorkList extends AppCompatActivity {

    ListView lstMyWork;
    ImageView imgQRcode;
    private PreviewView previewView;
    ListAdapter adapter;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String QrCodeStr= "";
    ImageView imgExit;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mywork);
        lstMyWork = (ListView) findViewById(R.id.lstMyWork);
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
        try
        {
            QrCodeStr = getIntent().getStringExtra("QrCode").toString();
        }
        catch (Exception e) {
            QrCodeStr = "";
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

        String Subject,Code,StrRequestType;

        ArrayList<HashMap<String, String>> DataList;
        DataList = new ArrayList<HashMap<String, String>>();
        Cursor cursors;
        db = dbh.getReadableDatabase();
        if(QrCodeStr == "")
        {
            cursors = db.rawQuery("select * from MyWork where Status2 in (1,3)  Order by RequestType Desc", null);
        }
        else
        {
            cursors = db.rawQuery("select * from MyWork WHERE Code='" + QrCodeStr + "' AND STATUS2 in (1,3)  Order by RequestType Desc", null);
        }

        if(cursors.getCount() > 0)
        {

            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                try {

                    HashMap<String, String> map = new HashMap<String, String>();

                    Code = cursors.getString(cursors.getColumnIndex("Code"));
                    Subject = cursors.getString(cursors.getColumnIndex("Subject"));
                    StrRequestType = cursors.getString(cursors.getColumnIndex("RequestType"));

                    map.put("Code", Code);
                    map.put("Subject", Subject);
                    map.put("Mobile", Mobile);
                    map.put("Usercode", Usercode);
                    map.put("Personcode", Personcode);
                    map.put("RequestType", StrRequestType);
                    DataList.add(map);
                } catch (Exception ex)
                {

                }

            }
            adapter = new AdapterListMyWorks(this, DataList);
            lstMyWork.setAdapter(adapter);
        }
        else
        {
            HashMap<String, String> map = new HashMap<String, String>();
            adapter = new AdapterListMyWorks(this, DataList);
            lstMyWork.setAdapter(adapter);
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