package com.npat.pm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ShowImage extends AppCompatActivity {

    ImageView ImgWorkLarg;
    private PreviewView previewView;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String Code;
    String ActivityClass;
    String Table;
    String ImageReport;
    String Status;
    String DescriptionReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_work);
        ImgWorkLarg = (ImageView) findViewById(R.id.ImgWorkLarg);
        try
        {
            Code = getIntent().getStringExtra("Code").toString();
        }
        catch (Exception e) {
            Code = "0";
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
            ActivityClass = getIntent().getStringExtra("ActivityClass").toString();
        }
        catch (Exception e) {
            ActivityClass = "";
        }
        try
        {
            Table = getIntent().getStringExtra("Table").toString();
        }
        catch (Exception e) {
            Table = "";
        }
        try
        {
            ImageReport = getIntent().getStringExtra("ImageReport").toString();
        }
        catch (Exception e) {
            ImageReport = "ERROR";
        }
        try
        {
            Status = getIntent().getStringExtra("Status").toString();
        }
        catch (Exception e) {
            Status = "";
        }
        try
        {
            DescriptionReport = getIntent().getStringExtra("DescriptionReport").toString();
        }
        catch (Exception e) {
            DescriptionReport = "";
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
        if(Table.compareTo("")!=0) {
            Cursor cursors;
            db = dbh.getReadableDatabase();
            cursors = db.rawQuery("select * from " + Table + " WHERE Code = '" + Code + "' Order by Code ASC", null);
            if (cursors.getCount() > 0) {
                cursors.moveToNext();
                @SuppressLint("Range") String Pic = cursors.getString(cursors.getColumnIndex("Pic"));
                if (Pic.compareTo("ERROR") != 0 && Pic.length()>10) {
                    Bitmap bitmap = ImageConvertor.Base64ToBitmap(Pic);
                    ImgWorkLarg.setImageBitmap(bitmap);
                }
            }
            cursors.close();
            db.close();
				dbh.close();
        }
        else
        {
            if (ImageReport.compareTo("ERROR") > 0) {
                Bitmap bitmap = ImageConvertor.Base64ToBitmap(ImageReport);
                ImgWorkLarg.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            switch (ActivityClass)
            {
                case "AcceptWork":
                    LoadActivity(AcceptWork.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", Code);
                    break;
                case "MyWork":
                    LoadActivity(MyWork.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode, "Code", Code);
                    break;
                case "ReportOtherWorkStatus":
                    LoadActivity(ReportOtherWorkStatus.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", Code);
                    break;
                case "ReportDuty":
                    LoadActivity(ReportDuty.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode, "Code", Code);
                    break;
                case "MyWorkReportDone":
                    LoadActivity2(MyWorkReportDone.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode, "Code", Code, "base64Str", ImageReport, "Status", Status, "DescriptionReport", DescriptionReport);
                    break;
                case "MyRequest":
                    LoadActivity2(MyRequest.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode, "Code", Code, "base64Str", ImageReport, "Status", Status, "DescriptionReport", DescriptionReport);
                    break;
            }
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
    public void LoadActivity2(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
            ,String VariableName5,String VariableValue5
            ,String VariableName6,String VariableValue6
            ,String VariableName7,String VariableValue7
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        intent.putExtra(VariableName5, VariableValue5);
        intent.putExtra(VariableName6, VariableValue6);
        intent.putExtra(VariableName7, VariableValue7);
        startActivity(intent);
        this.finish();
    }
}