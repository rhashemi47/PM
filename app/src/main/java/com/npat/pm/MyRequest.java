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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MyRequest extends AppCompatActivity {

    TextView txtCode;
    TextView txtSubject;
    TextView WorkType;
    TextView Location;
    TextView txtRade;
    TextView txtDescription;
    TextView txtRequestType;
    TextView txtStatus;
    TextView txtInsertDateOrder;
    ImageView imgWork;
    ImageView imgExit;

    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String WorkCode;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_myrequest);
         txtCode=(TextView) findViewById(R.id.txtCode);
         txtSubject=(TextView) findViewById(R.id.txtSubject);
         WorkType=(TextView) findViewById(R.id.txtWorkType);
         Location=(TextView) findViewById(R.id.txtLocation);
         txtRade=(TextView) findViewById(R.id.txtRade);
         txtDescription= (TextView) findViewById(R.id.txtDescription);
         txtRequestType=(TextView) findViewById(R.id.txtRequestType);
         txtStatus=(TextView) findViewById(R.id.txtStatus);
        txtInsertDateOrder=(TextView) findViewById(R.id.txtInsertDateOrder);
         imgWork=(ImageView) findViewById(R.id.imgWork);
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
        Cursor cursors = db.rawQuery("select * from Request WHERE Code= '" + WorkCode + "' Order by Code ASC", null);
        if(cursors.getCount() > 0) {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();

                txtCode.setText(cursors.getString(cursors.getColumnIndex("Code")));
                txtInsertDateOrder.setText(cursors.getString(cursors.getColumnIndex("InsertDate")));
                txtSubject.setText(cursors.getString(cursors.getColumnIndex("Subject")));
                WorkType.setText(cursors.getString(cursors.getColumnIndex("WorkType")));
                Location.setText(cursors.getString(cursors.getColumnIndex("Location")));
                txtRade.setText(cursors.getString(cursors.getColumnIndex("Rade")));
                txtDescription.setText(cursors.getString(cursors.getColumnIndex("Description")));
                txtStatus.setText(cursors.getString(cursors.getColumnIndex("Status")));
                String StrBmp = cursors.getString(cursors.getColumnIndex("Pic"));
                if(StrBmp.compareTo("ERROR") != 0 && StrBmp.length()>10) {
                    Bitmap bmp = ImageConvertor.Base64ToBitmap(StrBmp);
                    imgWork.setImageBitmap(bmp);
                }
            }
        }

        cursors.close();
        db.close();
		dbh.close();
        imgWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ShowImage.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "MyRequest","Table", "Request");
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
            LoadActivity(RequestList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "MyRequest","Table", "Request");
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
    public void LoadActivity2(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
            ,String VariableName5,String VariableValue5
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        intent.putExtra(VariableName5, VariableValue5);
        startActivity(intent);
        this.finish();
    }
}