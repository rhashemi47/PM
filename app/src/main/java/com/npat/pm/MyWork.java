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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MyWork extends AppCompatActivity {

    TextView txtCode;
    TextView txtSubject;
    TextView WorkType;
    TextView Location;
    TextView txtRade;
    TextView txtDescription;
    TextView txtRequestType;
    TextView txtStatus;
    TextView txtInsertUser;
    TextView txtStatusDesc;
    TextView txtInsertDateOrder;
    TextView txtHamkarName;
    TextView txtHamkarSendDate;
    ImageView imgWork;
    Button btnFinish;
    Button btnAssignment;
    Button btnImpossible;
    LinearLayout LinearStatusDesc;
    LinearLayout LinearReportImage;
    LinearLayout LinearHamkarName;
    LinearLayout LinearHamkarSendDate;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String WorkCode;
    String Status="";
    ImageView imgExit;
    ImageView imgReport;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_mywork);
         txtCode=(TextView) findViewById(R.id.txtCode);
         txtSubject=(TextView) findViewById(R.id.txtSubject);
         WorkType=(TextView) findViewById(R.id.txtWorkType);
         Location=(TextView) findViewById(R.id.txtLocation);
         txtRade=(TextView) findViewById(R.id.txtRade);
         txtDescription= (TextView) findViewById(R.id.txtDescription);
         txtRequestType=(TextView) findViewById(R.id.txtRequestType);
         txtStatus=(TextView) findViewById(R.id.txtStatus);
        txtHamkarName=(TextView) findViewById(R.id.txtHamkarName);
        txtInsertUser=(TextView) findViewById(R.id.txtInsertUser);
        txtInsertDateOrder=(TextView) findViewById(R.id.txtInsertDateOrder);
        txtStatusDesc=(TextView) findViewById(R.id.txtStatusDesc);
        txtHamkarSendDate=(TextView) findViewById(R.id.txtHamkarSendDate);
        btnFinish=(Button) findViewById(R.id.btnFinish);
        btnAssignment=(Button) findViewById(R.id.btnAssignment);
        btnImpossible=(Button) findViewById(R.id.btnImpossible);
        imgWork=(ImageView) findViewById(R.id.imgWork);
        LinearStatusDesc=(LinearLayout) findViewById(R.id.LinearStatusDesc);
        LinearReportImage=(LinearLayout) findViewById(R.id.LinearReportImage);
        LinearHamkarName=(LinearLayout) findViewById(R.id.LinearHamkarName);
        LinearHamkarSendDate=(LinearLayout) findViewById(R.id.LinearHamkarSendDate);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        imgReport = (ImageView) findViewById(R.id.imgReport);


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
        Cursor cursors = db.rawQuery("select * from MyWork WHERE Code= '" + WorkCode + "' Order by Code ASC", null);
        if(cursors.getCount() > 0) {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();

                txtCode.setText(cursors.getString(cursors.getColumnIndex("Code")));
                txtSubject.setText(cursors.getString(cursors.getColumnIndex("Subject")));
                WorkType.setText(cursors.getString(cursors.getColumnIndex("WorkType")));
                Location.setText(cursors.getString(cursors.getColumnIndex("Location")));
                txtRade.setText(cursors.getString(cursors.getColumnIndex("Rade")));
                txtDescription.setText(cursors.getString(cursors.getColumnIndex("Description")));
                Status = cursors.getString(cursors.getColumnIndex("Status2"));
                switch (Status)
                {
                    case "0":
                            txtRequestType.setText("پذیرش نشده");
                        break;
                        case "1":
                            txtRequestType.setText("پذیرش شده");
                        break;
                        case "2":
                            txtRequestType.setText("اتمام کار");
                        break;
                        case "3":
                            txtRequestType.setText("ارجاع به همکار");
                        break;
                        case "4":
                            txtRequestType.setText("در شرایط فعلی امکان پذیر نیست");
                        break;
                }
                txtStatus.setText(cursors.getString(cursors.getColumnIndex("Status")));
                txtInsertUser.setText(cursors.getString(cursors.getColumnIndex("InsertUser")));
                txtInsertDateOrder.setText(cursors.getString(cursors.getColumnIndex("InsertDate")));
                String StrBmp = cursors.getString(cursors.getColumnIndex("Pic"));
                if(StrBmp.compareTo("ERROR") != 0 && StrBmp.length()>10) {
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
        btnFinish.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
//                 WsWorkStatus LWorkStatus = new WsWorkStatus(MyWork.this,WorkCode,Usercode,"2","0",txtDescription.getText().toString(),Mobile,Personcode);
//                 LWorkStatus.AsyncExecute();
                 LoadActivity2(MyWorkReportDone.class, "Code", WorkCode,"Usercode", Usercode,"Description", txtDescription.getText().toString(),"Mobile", Mobile,"Personcode",Personcode,"Status","2");
             }
         });
        btnAssignment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               // LoadActivity2(ListHamkaran.class, "WorkCode", WorkCode,"Usercode", Usercode,"Description", txtDescription.getText().toString(),"Mobile", Mobile,"Personcode",Personcode);
                 LoadActivity2(MyWorkReportDone.class, "Code", WorkCode,"Usercode", Usercode,"Description", txtDescription.getText().toString(),"Mobile", Mobile,"Personcode",Personcode,"Status","3");
             }
         });
        btnImpossible.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
//                 WsWorkStatus LWorkStatus = new WsWorkStatus(MyWork.this,WorkCode,Usercode,"4","0",txtDescription.getText().toString(),Mobile,Personcode);
//                 LWorkStatus.AsyncExecute();
                 LoadActivity2(MyWorkReportDone.class, "Code", WorkCode,"Usercode", Usercode,"Description", txtDescription.getText().toString(),"Mobile", Mobile,"Personcode",Personcode,"Status","4");
             }
         });
        imgWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ShowImage.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "MyWork","Table", "MyWork");
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
            LoadActivity(MyWorkList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", txtCode.getText().toString(),"ActivityClass", "MyWork","Table", "MyWork");
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