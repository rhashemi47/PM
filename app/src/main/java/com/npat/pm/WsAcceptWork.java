package com.npat.pm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class WsAcceptWork {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String UserCode;
	private String WorkCode;
	private String PersonCode;
	private boolean CuShowDialog=true;
	//Contractor
	public WsAcceptWork(Activity activity, String PersonCode, String UserCode, String WorkCode) {
		this.activity = activity;
		this.UserCode = UserCode;
		this.WorkCode = WorkCode;
		this.PersonCode = PersonCode;
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();
		
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
		    this.dialog = new ProgressDialog(activity);		    		    this.dialog.setCanceledOnTouchOutside(false);
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		CallWsMethod("AcceptWork");
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
	            else if(WsResponse.toString().compareTo("2") == 0)
	            {
	            	Toast.makeText(this.activity.getApplicationContext(), "این کار قبلا پذیرش شده است", Toast.LENGTH_LONG).show();

	            }
	            else if(WsResponse.toString().compareTo("1") == 0)
	            {
	            	Toast.makeText(this.activity.getApplicationContext(), "این کار انتخاب شد", Toast.LENGTH_LONG).show();
					InsertDataFromWsToDb(WsResponse);
	            }
	            else
	            {
					Toast.makeText(this.activity.getApplicationContext(), "ادر انتخاب کار خطایی پیش آمده", Toast.LENGTH_LONG).show();
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
	    PropertyInfo UserCodePI = new PropertyInfo();
	    //Set Name
		UserCodePI.setName("UserCode");
	    //Set Value
		UserCodePI.setValue(this.UserCode);
	    //Set dataType
		UserCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserCodePI);
		//*******************************************
		PropertyInfo WorkCodePI = new PropertyInfo();
		//Set Name
		WorkCodePI.setName("WorkCode");
		//Set Value
		WorkCodePI.setValue(this.WorkCode);
		//Set dataType
		WorkCodePI.setType(String.class);
		//Add the property to request object
		request.addProperty(WorkCodePI);
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
		try {
			db.execSQL("delete from AcceptWork where Code='" + WorkCode + "'" );
		}
		catch (Exception e)
		{

		}
		db.close();
		dbh.close();
		PublicUpdate.Update(true,false,false,activity,UserCode,PersonCode);
		LoadActivity(MainActivity.class, "Mobile", "0","Usercode", UserCode,"Personcode", PersonCode);
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
