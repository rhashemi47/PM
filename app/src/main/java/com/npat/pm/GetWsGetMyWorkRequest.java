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

public class GetWsGetMyWorkRequest {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String UserCode;
	private boolean CuShowDialog=true;
	private boolean toast;
	private boolean notifi;
	//Contractor
	public GetWsGetMyWorkRequest(Activity activity, String UserCode,boolean toast,boolean proccessbar, boolean notifi) {
		this.activity = activity;
		this.UserCode=UserCode;
		IC = new InternetConnection(this.activity);
		PV = new PublicVariable();

		this.CuShowDialog=proccessbar;
		this.toast=toast;
		this.notifi=notifi;
		PublicVariable.RequestWork = false;
		dbh=new DatabaseHelper(this.activity);
		try {

			dbh.createDataBase();

   		} catch (IOException ioe) {
			PublicVariable.RequestWork = true;
   			throw new Error("Unable to create database");

   		}

   		try {

   			dbh.openDataBase();

   		} catch (SQLException sqle) {
			PublicVariable.RequestWork = true;
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
				 PublicVariable.RequestWork = true;
	            e.printStackTrace();
			 }
		}
		else
		{
			//Toast.makeText(this.activity, "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
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
        		CallWsMethod("GetMyWorkRequest");
        	}
	    	catch (Exception e) {
				PublicVariable.RequestWork = true;
	    		result = e.getMessage().toString();
			}
	        return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
			PublicVariable.RequestWork = true;
        	if(result == null)
        	{
	            if(WsResponse.toString().compareTo("ER") == 0)
	            {
					if(toast) {
						Toast.makeText(this.activity, "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
					}
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
	    PropertyInfo UserCodePI = new PropertyInfo();
	    //Set Name
		UserCodePI.setName("UserCode");
	    //Set Value
		UserCodePI.setValue(this.UserCode);
	    //Set dataType
		UserCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserCodePI);

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
		String[] res;
		String[] value;
		String query=null;
		db=dbh.getWritableDatabase();
		res=WsResponse.split("@@");
		db.execSQL("delete from Request");
		if(WsResponse.compareTo("0")!=0) {
			for (int i = 0; i < res.length; i++) {
				try {
				value = res[i].split("##");
				query = "INSERT INTO Request (" +
						"Code" +
						",WorkType" +
						",Subject" +
						",Location" +
						",Rade" +
						",Description" +
						",Status" +
						",Pic" +
						") VALUES('"
						+ value[0] +
						"','" + value[1] +
						"','" + value[2] +
						"','" + value[3] +
						"','" + value[4] +
						"','" + value[5] +
						"','" + value[6] +
						"','" + value[7] +
						"')";
					db.execSQL(query);
				}
				catch (Exception e)
				{

				}
			}
		}
		db.close();
				dbh.close();
	}
}
