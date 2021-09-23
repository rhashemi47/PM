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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ReportDuty extends AppCompatActivity {


    TextView txtCode;
    TextView txtSubject;
    TextView WorkType;
    TextView Location;
    TextView txtRade;
    TextView txtDescription;
    TextView txtRequestType;
    TextView txtInsertUser;
    TextView txtStatus;
    ImageView imgWork;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String WorkCode;
    ImageView imgExit;
    TextView txtInsertDateOrder;
    TextView txtHamkarSendDate;
    LinearLayout LinearStatusDesc;
    LinearLayout LinearReportImage;
    LinearLayout LinearHamkarName;
    LinearLayout LinearHamkarSendDate;
    ImageView imgReport;
    TextView txtStatusDesc;
    TextView txtHamkarName;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_report_duty);
        txtCode=(TextView) findViewById(R.id.txtCode);
        txtSubject=(TextView) findViewById(R.id.txtSubject);
        WorkType=(TextView) findViewById(R.id.txtWorkType);
        Location=(TextView) findViewById(R.id.txtLocation);
        txtRade=(TextView) findViewById(R.id.txtRade);
        txtDescription= (TextView) findViewById(R.id.txtDescription);
        txtRequestType=(TextView) findViewById(R.id.txtRequestType);
        txtStatus=(TextView) findViewById(R.id.txtStatus);
        txtInsertUser=(TextView) findViewById(R.id.txtInsertUser);
        imgWork=(ImageView) findViewById(R.id.imgWork);
        LinearStatusDesc=(LinearLayout) findViewById(R.id.LinearStatusDesc);
        LinearReportImage=(LinearLayout) findViewById(R.id.LinearReportImage);
        LinearHamkarName=(LinearLayout) findViewById(R.id.LinearHamkarName);
        LinearHamkarSendDate=(LinearLayout) findViewById(R.id.LinearHamkarSendDate);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        imgReport = (ImageView) findViewById(R.id.imgReport);
        txtInsertDateOrder=(TextView) findViewById(R.id.txtInsertDateOrder);
        txtStatusDesc=(TextView) findViewById(R.id.txtStatusDesc);
        txtHamkarSendDate=(TextView) findViewById(R.id.txtHamkarSendDate);
        txtHamkarName=(TextView) findViewById(R.id.txtHamkarName);


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
            WorkCode = getIntent().getStringExtra("Code").toString();
        }
        catch (Exception e) {
            WorkCode = "0";
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
        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("select * from MyWorkStatusReport WHERE Code= '" + WorkCode + "'  Order by Code ASC", null);
        if(cursors.getCount() > 0) {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();

                txtCode.setText(cursors.getString(cursors.getColumnIndex("Code")));
                txtSubject.setText(cursors.getString(cursors.getColumnIndex("Subject")));
                WorkType.setText(cursors.getString(cursors.getColumnIndex("WorkType")));
                Location.setText(cursors.getString(cursors.getColumnIndex("Location")));
                txtRade.setText(cursors.getString(cursors.getColumnIndex("Rade")));
                txtDescription.setText(cursors.getString(cursors.getColumnIndex("Description")));
                txtStatus.setText(cursors.getString(cursors.getColumnIndex("Status")));
                txtInsertUser.setText(cursors.getString(cursors.getColumnIndex("InsertUser")));
                String StrBmp = cursors.getString(cursors.getColumnIndex("Pic"));
                if(StrBmp.compareTo("ERROR") != 0) {
                    Bitmap bmp = ImageConvertor.Base64ToBitmap(StrBmp);
                    imgWork.setImageBitmap(bmp);
                }
                if(cursors.getString(cursors.getColumnIndex("StatusDesc"))!= null)
                {
                    if(cursors.getString(cursors.getColumnIndex("StatusDesc")).compareTo("0") != 0) {
                        String s =cursors.getString(cursors.getColumnIndex("StatusDesc"));
                        LinearStatusDesc.setVisibility(View.VISIBLE);
                        txtStatusDesc.setText(cursors.getString(cursors.getColumnIndex("StatusDesc")));
                    }
                    else
                    {
                        LinearStatusDesc.setVisibility(View.GONE);
                    }
                }
                else
                {
                    LinearStatusDesc.setVisibility(View.GONE);
                }
                if(cursors.getString(cursors.getColumnIndex("StatusInsertUser"))!= null)
                {
                    if(cursors.getString(cursors.getColumnIndex("StatusInsertUser")).compareTo("0") != 0) {
                        LinearHamkarName.setVisibility(View.VISIBLE);
                        txtHamkarName.setText(cursors.getString(cursors.getColumnIndex("StatusInsertUser")));
                    }
                    else
                    {
                        LinearHamkarName.setVisibility(View.GONE);
                    }
                }
                else
                {
                    LinearHamkarName.setVisibility(View.GONE);
                }
                if(cursors.getString(cursors.getColumnIndex("StatusInsertDate"))!= null)
                {
                    if(cursors.getString(cursors.getColumnIndex("StatusInsertDate")).compareTo("0") != 0) {
                        LinearHamkarSendDate.setVisibility(View.VISIBLE);
                        txtHamkarSendDate.setText(cursors.getString(cursors.getColumnIndex("StatusInsertDate")));
                    }
                    else
                    {
                        LinearHamkarSendDate.setVisibility(View.GONE);
                    }
                }
                else
                {
                    LinearHamkarSendDate.setVisibility(View.GONE);
                }
                if(cursors.getString(cursors.getColumnIndex("PicReport"))!= null)
                {
                    String StrBmpReport = cursors.getString(cursors.getColumnIndex("PicReport"));
                    if(StrBmpReport.compareTo("ERROR") != 0 && StrBmpReport.length()>10) {
                        Bitmap bmp = ImageConvertor.Base64ToBitmap(StrBmpReport);
                        imgReport.setImageBitmap(bmp);
                        LinearReportImage.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        LinearReportImage.setVisibility(View.GONE);
                    }
                }
                else
                {
                    LinearReportImage.setVisibility(View.GONE);
                }
            }
        }
        cursors.close();
        db.close();
				dbh.close();
        imgWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ShowImage.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "ReportDuty","Table", "MyWorkStatusReport");
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
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(ListReportDuty.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "MyWork","Table", "MyWorkStatusReport");
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
            ,String VariableName5,String VariableValue5
            ,String VariableName6,String VariableValue6
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        intent.putExtra(VariableName5, VariableValue5);
        intent.putExtra(VariableName6, VariableValue6);
        startActivity(intent);
        this.finish();
    }
}