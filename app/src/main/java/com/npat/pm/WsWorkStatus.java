package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;

public class WsWorkStatus {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String UserCode;
	private String WorkCode;
	private String Status;
	private String HamkarCode;
	private String Description;
	private String Mobile;
	private String Personcode;
	private String ImgReport;
	private boolean CuShowDialog=true;
	//Contractor
	@SuppressLint("Range")
	public WsWorkStatus(Activity activity, String WorkCode, String UserCode, String Status, String HamkarCode, String Description, String Mobile, String Personcode, String ImgReport) {
		this.activity = activity;
		this.WorkCode = WorkCode;
		this.UserCode = UserCode;
		this.Status = Status;
		this.HamkarCode = HamkarCode;
		this.Description = Description;
		this.Mobile = Mobile;
		this.Personcode = Personcode;
		this.ImgReport = ImgReport;
		dbh=new DatabaseHelper(this.activity.getApplicationContext());
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

		if(ImgReport.compareTo(" ") == 0 )
		{
			db=dbh.getReadableDatabase();
			String query = "SELECT * FROM TempPic WHERE Code='" + this.WorkCode + "' ORDER BY Code DESC";
			Cursor cursor= db.rawQuery(query,null);
			if(cursor.getCount()>0)
			{
				cursor.moveToNext();
				this.ImgReport = cursor.getString(cursor.getColumnIndex("Pic"));
			}
			db.close();
		}
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();
	}
	
	public void AsyncExecute()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				AsyncCallWS task = new AsyncCallWS(this.activity);
				task.execute();
			}	
			 catch (Exception e) {
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(this.activity.getApplicationContext(), "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
		}
	}
	
	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWS(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    this.dialog.setCanceledOnTouchOutside(false);
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		CallWsMethod("WorkStatus");
        	}
	    	catch (Exception e) {
	    		result = e.getMessage().toString();
			}
	        return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
        	if(result == null)
        	{
	            if(WsResponse.toString().compareTo("ER") == 0)
	            {
	            	Toast.makeText(this.activity.getApplicationContext(), "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
	            }
	            else if(WsResponse.toString().compareTo("0") == 0)
	            {
	            	Toast.makeText(this.activity.getApplicationContext(), "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
	            }
	            else
	            {
	            	InsertDataFromWsToDb(WsResponse);
	            }
        	}
        	else
        	{
        		//Toast.makeText(this.activity, "ط®ط·ط§ ط¯ط± ط§طھطµط§ظ„ ط¨ظ‡ ط³ط±ظˆط±", Toast.LENGTH_SHORT).show();
        	}
            try
            {
            	if (this.dialog.isShowing()) {
            		this.dialog.dismiss();
            	}
            }
            catch (Exception e) {}
        }
 
        @Override
        protected void onPreExecute() {
        	if(CuShowDialog)
        	{
        		this.dialog.setMessage("در حال پردازش");
        		this.dialog.show();
        	}
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

	
	public void CallWsMethod(String METHOD_NAME) {
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    //****************************************************
	    PropertyInfo WorkCodePI = new PropertyInfo();
	    //Set Name
		WorkCodePI.setName("WorkCode");
	    //Set Value
		WorkCodePI.setValue(this.WorkCode);
	    //Set dataType
		WorkCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(WorkCodePI);
	    //****************************************************
	    PropertyInfo UserCodePI = new PropertyInfo();
	    //Set Name
		UserCodePI.setName("UserCode");
	    //Set Value
		UserCodePI.setValue(this.UserCode);
	    //Set dataType
		UserCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserCodePI);
	    //****************************************************
	    PropertyInfo StatusPI = new PropertyInfo();
	    //Set Name
		StatusPI.setName("Status");
	    //Set Value
		StatusPI.setValue(this.Status);
	    //Set dataType
		StatusPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(StatusPI);
	    //****************************************************
	    PropertyInfo HamkarCodePI = new PropertyInfo();
	    //Set Name
		HamkarCodePI.setName("HamkarCode");
	    //Set Value
		HamkarCodePI.setValue(this.HamkarCode);
	    //Set dataType
		HamkarCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(HamkarCodePI);
	    //****************************************************
	    PropertyInfo DescriptionPI = new PropertyInfo();
	    //Set Name
		DescriptionPI.setName("Description");
	    //Set Value
		DescriptionPI.setValue(this.Description);
	    //Set dataType
		DescriptionPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(DescriptionPI);
	    //****************************************************
	    PropertyInfo ImgReportPI = new PropertyInfo();
	    //Set Name
		ImgReportPI.setName("Pic");
	    //Set Value
		ImgReportPI.setValue(this.ImgReport);
	    //Set dataType
		ImgReportPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(ImgReportPI);
	    //Create envelope
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	            SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    //Set output SOAP object
	    envelope.setOutputSoapObject(request);
	    //Create HTTP call object
	    HttpTransportSE androidHttpTransport = new HttpTransportSE(PV.URL);
	    try {
	        //Invoke web service
	        androidHttpTransport.call("http://tempuri.org/"+METHOD_NAME, envelope);
	        //Get the response
	        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
	        //Assign it to FinalResultForCheck static variable
	        WsResponse = response.toString();	
	        if(WsResponse == null) WsResponse="ER";
	    } catch (Exception e) {
	    	WsResponse = "ER";
	    	e.printStackTrace();
	    }
	}
	
	
	public void InsertDataFromWsToDb(String AllRecord)
    {
		db=dbh.getWritableDatabase();
		String query="Delete FROM MyWork WHERE Code='" + WorkCode + "'";
		try {
			db.execSQL(query);
		}
		catch (Exception e)
		{

		}
		db.close();
		dbh.close();
		PublicUpdate.Update(true,false,false,activity,UserCode,Personcode);
		LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", UserCode,"Personcode", Personcode);
    }
	public void LoadActivity(Class<?> Cls,String VariableName1,String VariableValue1,
							 String VariableName2,String VariableValue2,
							 String VariableName3,String VariableValue3
	)
	{
		Intent intent = new Intent(activity,Cls);
		intent.putExtra(VariableName1, VariableValue1);
		intent.putExtra(VariableName2, VariableValue2);
		intent.putExtra(VariableName3, VariableValue3);
		activity.startActivity(intent);
	}
}
