package com.npat.pm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class Login extends Activity {

	DatabaseHelper dbh;
	SQLiteDatabase db;
	InternetConnection IC;
	EditText EditTextMobile;
	Button btnLogin;
	ImageView imgSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        Typeface FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        EditTextMobile = (EditText)findViewById(R.id.EditTextMobile);
        btnLogin = (Button)findViewById(R.id.btnLogin);
		imgSetting = (ImageView) findViewById(R.id.imgSetting);
        EditTextMobile.setTypeface(FontMitra);
        btnLogin.setTypeface(FontMitra);
        
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

		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from Links WHERE Def=1", null);

		if(cursors.getCount() > 0) {

			for (int i = 0; i < cursors.getCount(); i++) {
				cursors.moveToNext();
				@SuppressLint("Range") String URL ="http://" + cursors.getString(cursors.getColumnIndex("Url")) +"/Projects/RasadPM/WebService/PMWS.asmx";
				PublicVariable.URL = URL;
			}
		}
		db.close();
				dbh.close();
		cursors.close();
		imgSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LoadActivity(Setting.class, "", "");
			}
		});
        btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(IC.isConnectingToInternet()==true)
				{
					if(EditTextMobile.getText().length() > 0)
					{
						WsLogin L = new WsLogin(com.npat.pm.Login.this, EditTextMobile.getText().toString());
						L.AsyncExecute();

					}
					else
					{
						Toast.makeText(getApplicationContext(), "لطفا شماره همراه را وارد کنید", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "شما به اینترنت دسترسی ندارید", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (ActivityCompat.checkSelfPermission(com.npat.pm.Login.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(com.npat.pm.Login.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
		}

    }
	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(this,Cls);
		intent.putExtra(VariableName, VariableValue);
		startActivity(intent);
		this.finish();
	}
    @SuppressLint("Range")
	public String CheckHasData(String TableName)
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select count(*)c from "+TableName, null);
		cursors.moveToNext();
		String Count= cursors.getString(cursors.getColumnIndex("c"));
		cursors.close();
		db.close();
				dbh.close();
	    return Count;
	}



}
