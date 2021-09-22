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

public class AcceptWork extends AppCompatActivity {

    TextView txtCode;
    TextView txtSubject;
    TextView WorkType;
    TextView Location;
    TextView txtRade;
    TextView txtDescription;
    TextView txtRequestType;
    TextView txtInsertUser;
    Button btnSelectWork;
    ImageView imgWork;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    InternetConnection IC;
    String Mobile;
    String Usercode;
    String Personcode;
    String WorkCode;
    ImageView imgExit;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_accept_work);
        imgExit = (ImageView) findViewById(R.id.imgExit);
         txtCode=(TextView) findViewById(R.id.txtCode);
         txtSubject=(TextView) findViewById(R.id.txtSubject);
         WorkType=(TextView) findViewById(R.id.txtWorkType);
         Location=(TextView) findViewById(R.id.txtLocation);
         txtRade=(TextView) findViewById(R.id.txtRade);
         txtDescription= (TextView) findViewById(R.id.txtDescription);
         txtInsertUser= (TextView) findViewById(R.id.txtInsertUser);
         txtRequestType=(TextView) findViewById(R.id.txtRequestType);
         btnSelectWork=(Button) findViewById(R.id.btnSelectWork);
         imgWork=(ImageView) findViewById(R.id.imgWork);


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
        Cursor cursors = db.rawQuery("select * from AcceptWork WHERE Code= " + WorkCode + "  Order by Code ASC", null);
        if(cursors.getCount() > 0) {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();

                txtCode.setText(cursors.getString(cursors.getColumnIndex("Code")));
                txtInsertUser.setText(cursors.getString(cursors.getColumnIndex("InsertUser")));
                txtSubject.setText(cursors.getString(cursors.getColumnIndex("Subject")));
                WorkType.setText(cursors.getString(cursors.getColumnIndex("WorkType")));
                Location.setText(cursors.getString(cursors.getColumnIndex("Location")));
                txtRade.setText(cursors.getString(cursors.getColumnIndex("Rade")));
                txtDescription.setText(cursors.getString(cursors.getColumnIndex("Description")));
                txtRequestType.setText(cursors.getString(cursors.getColumnIndex("RequestType")));
                String bitStr=cursors.getString(cursors.getColumnIndex("Pic"));
                if(bitStr != null && bitStr.compareTo("ERROR") != 0 && bitStr.length()>10) {
                    Bitmap bitmap = ImageConvertor.Base64ToBitmap(bitStr);
                    imgWork.setImageBitmap(bitmap);
                }
            }
        }
        cursors.close();
        db.close();
				dbh.close();
         btnSelectWork.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 WsAcceptWork LAcceptWork = new WsAcceptWork(AcceptWork.this,Personcode, Usercode,WorkCode);
                 LAcceptWork.AsyncExecute();
             }
         });
        imgWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ShowImage.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "AcceptWork","Table", "AcceptWork");
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
            LoadActivity(AcceptWorkList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "AcceptWork","Table", "AcceptWork");
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