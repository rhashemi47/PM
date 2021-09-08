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

public class WsAcceptCode {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String CuMobile,CuAcceptCode;
	private String WsResponse; 
	private String UserCode;
	private String Personcode;
	//Contractor
	public WsAcceptCode(Activity activity, String Mobile, String UserCode, String Personcode, String AcceptCode) {
		this.activity = activity;
		this.CuMobile = Mobile;
		this.CuAcceptCode = AcceptCode;
		this.UserCode = UserCode;
		this.Personcode = Personcode;
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
				//Toast.makeText(this.activity.getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(this.activity.getApplicationContext(), "شما به اینترنت دسترسی ندارید", Toast.LENGTH_SHORT).show();
		}
	}
	
	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWS(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		CallWsMethod("UserLogin");
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
	            if(WsResponse.toString().compareTo("0") == 0)
	            {
	            	Toast.makeText(this.activity.getApplicationContext(), "کد تایید صحیح نمی باشد", Toast.LENGTH_LONG).show();
	            }
				else if(WsResponse.toString().compareTo("ER") == 0)
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
        		//Toast.makeText(this.activity, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
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
        		this.dialog.setMessage("در حال دریافت اطلاعات");
        		this.dialog.show();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
	
	public void CallWsMethod(String METHOD_NAME) {
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("Mobile");
	    //Set Value
	    UserPI.setValue(this.CuMobile);
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
		PropertyInfo UserCodePI = new PropertyInfo();
		UserCodePI.setName("UserCode");
	    //Set Value
		UserCodePI.setValue(this.UserCode);
	    //Set dataType
		UserCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserCodePI);
	    PropertyInfo AcceptCodePI = new PropertyInfo();
	    //Set Name
	    AcceptCodePI.setName("AcceptCode");
	    //Set Value
	    AcceptCodePI.setValue(this.CuAcceptCode);
	    //Set dataType
	    AcceptCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(AcceptCodePI);
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
		db = dbh.getWritableDatabase();
		db.execSQL("delete from Login");
		String query="INSERT INTO Login (Usercode,Personcode,Mobile,Status) VALUES('" + UserCode + "','" + Personcode + "' , '" + CuMobile + "','1')";
		try {
			db.execSQL(query);
		}
		catch (Exception e)
		{

		}
		db.close();
		dbh.close();
		PublicUpdate.Update(false,true,true,activity,UserCode,Personcode);
		/*WsAcceptWorkList LWsAcceptWorkList = new WsAcceptWorkList(activity, UserCode);
		LWsAcceptWorkList.AsyncExecute();
		WsMyWorkList LWsMyWorkList = new WsMyWorkList(activity, Personcode);
		LWsMyWorkList.AsyncExecute();
		WsGetLocation LWsGetLocation = new WsGetLocation(activity);
		LWsGetLocation.AsyncExecute();
		WsGetRade LWsGetRade = new WsGetRade(activity);
		LWsGetRade.AsyncExecute();
		WsGetMyWorkRequest LWsGetMyWorkRequest = new WsGetMyWorkRequest(activity,UserCode);
		LWsGetMyWorkRequest.AsyncExecute();*/
        LoadActivity(MainActivity.class,"UserCode",UserCode,"Personcode",Personcode);
    }

	public void LoadActivity(Class<?> Cls,String VariableName1,String VariableValue1,
							 String VariableName2,String VariableValue2
	)
	{
		Intent intent = new Intent(activity,Cls);
		intent.putExtra(VariableName1, VariableValue1);
		intent.putExtra(VariableName2, VariableValue2);
		activity.startActivity(intent);
	}
	
}
