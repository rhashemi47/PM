package com.npat.pm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    SQLiteDatabase db;
    InternetConnection IC;
    Button btnAcceptWork;
    Button btnDuty;
    Button btnRequsetList;
    Button btnRequset;
    Button btnReportDuty;
    Button btnReportHamkarDuty;
    TextView txtUpdate;
    ImageView imgExit;
    ImageView imgUpdate;
    ImageView imgSetting;
    String Mobile;
    String Usercode;
    String Personcode;
    private String AppVersion;
    private boolean doubleBackToExitPressedOnce = false;
   public int MY_PERMISSIONS_REQUEST_READ_CONTACTS=124;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());

        btnAcceptWork = (Button) findViewById(R.id.btnAcceptWork);
        btnDuty = (Button) findViewById(R.id.btnDuty);
        btnRequsetList = (Button) findViewById(R.id.btnRequsetList);
        btnRequset = (Button) findViewById(R.id.btnRequset);
        btnReportDuty = (Button) findViewById(R.id.btnReportDuty);
        btnReportHamkarDuty = (Button) findViewById(R.id.btnReportHamkarDuty);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        imgUpdate = (ImageView) findViewById(R.id.imgUpdate);
        imgSetting = (ImageView) findViewById(R.id.imgSetting);
        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnAcceptWork));
        //*********************************************************
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnDuty));
        //*********************************************************
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnRequsetList));
        //*********************************************************
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnRequset));
        //*********************************************************
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnReportDuty));
        //*********************************************************
        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .repeat(1)
                .playOn(findViewById(R.id.btnReportHamkarDuty));
        //*********************************************************
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        if(version.length()>0) {
            AppVersion = version;
            WsDownLoadUpdate wsDownLoadUpdate=new WsDownLoadUpdate(MainActivity.this,AppVersion, PublicVariable.LinkFileTextCheckVersion,PublicVariable.DownloadAppUpdateLinkAPK);
            wsDownLoadUpdate.AsyncExecute();
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
        if(CheckHasData("login")==0)
        {
            stopService(new Intent(getBaseContext(), ServiceGetUpdate.class));
            LoadActivity(Login.class, "", "", "", "", "", "");
        }
        else
        {
            db = dbh.getReadableDatabase();
            Cursor cursors = db.rawQuery("select * from Links WHERE Def=1", null);
            if(cursors.getCount() > 0) {
                for (int i = 0; i < cursors.getCount(); i++) {
                    cursors.moveToNext();
                    @SuppressLint("Range") String URL ="http://" + cursors.getString(cursors.getColumnIndex("Url")) +"/Projects/RasadPM/WebService/PMWS.asmx";
                    PublicVariable.URL = URL;
                }
            }
            cursors.close();
            db.close();
			dbh.close();
            db = dbh.getReadableDatabase();
            cursors = db.rawQuery("select * from Login", null);
            if(cursors.getCount() > 0)
            {
                cursors.moveToNext();
                Mobile = cursors.getString(cursors.getColumnIndex("Mobile"));
                Usercode = cursors.getString(cursors.getColumnIndex("Usercode"));
                Personcode = cursors.getString(cursors.getColumnIndex("Personcode"));
                //new run service -- manage by OS Android

//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    MainActivity.this.startForegroundService(serviceIntent);
//                } else {
//                    startService(serviceIntent);
//                    ContextCompat.startForegroundService(getApplicationContext(),serviceIntent);
//                }
                Intent serviceIntent = new Intent(getApplicationContext(),ServiceGetUpdate.class);
                try {

                        this.startService(serviceIntent);
                }catch ( Exception e1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.startForegroundService(serviceIntent);
                    }else {
                       // Crashlytics.log("crash for first time, trying another.");
                        this.startService(serviceIntent);
                    }
                }

            }
            if(db != null) {
                if (db.isOpen()) {
                    cursors.close();
                    db.close();
				dbh.close();
                }
            }

//
        }

        btnAcceptWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(AcceptWorkList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });

        btnDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(MyWorkList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
        btnRequsetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(RequestList.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
        btnRequset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(Request.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
        btnReportDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(FilterReportDuty.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
        btnReportHamkarDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ReportHamkarDuty.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            }
        });
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout();
                finish();
            }
        });
        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicUpdate.Update(true,true,true,MainActivity.this,Usercode,Personcode);
            }
        });
        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicUpdate.Update(true,true,true,MainActivity.this,Usercode,Personcode);
            }
        });
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity2(Setting.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"ActivityGo","MainMenu");
            }
        });
    }
    @Override
    public void onBackPressed() {

            if (doubleBackToExitPressedOnce) {
//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//
//                startMain.addCategory(Intent.CATEGORY_HOME);
//
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //startActivity(startMain);

                finish();
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "جهت خروج از برنامه مجددا دکمه برگشت را لمس کنید", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
    public void LoadActivity2(Class<?> Cls
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
    }
    @SuppressLint("Range")
    public int CheckHasData(String TableName)
    {
        if(db != null)
        {
            if(!db.isOpen())
            {
                 db = dbh.getReadableDatabase();
            }

        }
        else
        {
            db = dbh.getReadableDatabase();
        }

        Cursor cursors = db.rawQuery("select * from " + TableName, null);
        if(cursors.getCount() > 0)
        {
            cursors.moveToNext();
            String Status = cursors.getString(cursors.getColumnIndex("Status"));
            if(Status.compareTo("0")==0 || Status.compareTo("")==0)
            {
                cursors.close();
                db.close();
				dbh.close();
                return 0;
            }
            else
            {
                cursors.close();
                db.close();
				dbh.close();
                return 1;
            }
        }
        else
        {
            cursors.close();
            db.close();
				dbh.close();
            return 0;
        }
    }

    public void Logout() {
        //Exit All Activity And Kill Application
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        // set the message to display
        alertbox.setMessage("آیا می خواهید از کاربری خارج شوید ؟");

        // set a negative/no button and create a listener
        alertbox.setPositiveButton("خیر", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        // set a positive/yes button and create a listener
        alertbox.setNegativeButton("بله", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                stopService(new Intent(getBaseContext(), ServiceGetUpdate.class));
                db = dbh.getWritableDatabase();
                db.execSQL("DELETE FROM AcceptWork");
                db.execSQL("DELETE FROM android_metadata");
                db.execSQL("DELETE FROM Hamkaran");
                //db.execSQL("DELETE FROM Links");
                db.execSQL("DELETE FROM Location");
                db.execSQL("DELETE FROM login");
                db.execSQL("DELETE FROM MyWork");
                db.execSQL("DELETE FROM Rade");
                db.execSQL("DELETE FROM RequsetType");
                db.execSQL("DELETE FROM settings");
                db.execSQL("DELETE FROM sqlite_sequence");
                if(db.isOpen()) {
                    db.close();
				dbh.close();
                }
                Intent startMain = new Intent(Intent.ACTION_MAIN);

                startMain.addCategory(Intent.CATEGORY_HOME);

                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(startMain);

                finish();
                arg0.dismiss();
            }
        });
        alertbox.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 124: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}