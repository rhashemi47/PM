package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Setting extends Activity {
	ListView lst_data;
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	Typeface FontMitra;
	String ActivityGo;
	Handler mHandler;
	Button btnAdd;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.links);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try
		{
			ActivityGo = getIntent().getStringExtra("ActivityGo").toString();
		}
		catch (Exception e) {
			ActivityGo = "0";
		}

		FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
		
		
		lst_data=(ListView)findViewById(R.id.ListLink);
		btnAdd=(Button) findViewById(R.id.btnAdd);
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
        try
        {
        	FillData();
        }
        catch (Exception e) {
        	Toast.makeText(getApplicationContext(), "اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید", Toast.LENGTH_LONG).show();
		}

		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LoadActivity(EditLink.class, "", "");
			}
		});

		mHandler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
							Thread.sleep(500); // every 60 seconds
							mHandler.post(new Runnable() {

								public String LastHamyarUserServiceCode;

								@Override
								public void run() {
									FillData();
								}
							});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}).start();
	}
	
	
	@SuppressLint("Range")
	private void FillData()
	{
    	
    	String Title,Code,Def,Url;
		
		ArrayList<HashMap<String, String>> DataLList;
		DataLList = new ArrayList<HashMap<String, String>>();
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from Links", null);

		if(cursors.getCount() > 0)
		{
			
			for (int i = 0; i < cursors.getCount(); i++) {
				cursors.moveToNext();
				HashMap<String, String> map = new HashMap<String, String>();
				
				Code = cursors.getString(cursors.getColumnIndex("id"));
				Title = cursors.getString(cursors.getColumnIndex("Title"));
				Def = cursors.getString(cursors.getColumnIndex("Def"));
				Url = cursors.getString(cursors.getColumnIndex("Url"));

				map.put("Code", Code);
				map.put("Title", Title);
				map.put("Def", Def);
				map.put("Url", Url);
				DataLList.add(map);
	
			}
			adapter = new AdapterListLink(this, DataLList);
			lst_data.setAdapter(adapter);
		}
		else
		{
			HashMap<String, String> map = new HashMap<String, String>();
			adapter = new AdapterListLink(this, DataLList);
			lst_data.setAdapter(adapter);
			//Toast.makeText(getApplicationContext(), "اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید", Toast.LENGTH_LONG).show();
		}
		cursors.close();
		db.close();
				dbh.close();
	}
	
	protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.drawable.activity_open_scale,R.drawable.activity_close_translate);
	  }

	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(this,Cls);
		intent.putExtra(VariableName, VariableValue);
		startActivity(intent);
		this.finish();
	}
	

	private void ExitApplication()
	{
		//Exit All Activity And Kill Application
		 Builder alertbox = new Builder(com.npat.pm.Setting.this);
	       // set the message to display
	       alertbox.setMessage("آیا می خواهید از برنامه خارج شوید ؟");
	       
	       // set a negative/no button and create a listener
	       alertbox.setPositiveButton("خیر", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	               arg0.dismiss();
	           }
	       });
	       
	       // set a positive/yes button and create a listener
	       alertbox.setNegativeButton("بلی", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	        	   //Declare Object From Get Internet Connection Status For Check Internet Status
	        	  System.exit(0);
	   			  arg0.dismiss();
	   			  
	           }
	       });
	      
	       alertbox.show();
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )  {
	    if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
	    	if(ActivityGo.compareTo("MainMenu")==0) {
				LoadActivity(MainActivity.class, "", "");
			}
	    	else
			{
				LoadActivity(Login.class, "", "");
			}
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}
}
