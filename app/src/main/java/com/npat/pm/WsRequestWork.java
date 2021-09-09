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

public class WsRequestWork {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String UserCode;
	private String Subject;
	private String LocationCode;
	private String RadeCode;
	private String RequestType;
	private String Description;
	private String Pic;
	private String PersonCode;
	private boolean CuShowDialog=true;
	//Contractor
	public WsRequestWork(Activity activity, String UserCode, String PersonCode, String Subject, String LocationCode, String RadeCode, String RequestType, String Description, String Pic) {
		this.activity = activity;
		this.UserCode=UserCode;
		this.Subject=Subject;
		this.LocationCode=LocationCode;
		this.RadeCode=RadeCode;
		this.RequestType=RequestType;
		this.Description=Description;
		this.PersonCode=PersonCode;
		this.Pic=Pic;
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();
		
		dbh=new DatabaseHelper(this.activity.getApplicationContext());
		try {

			dbh.createDataBase();

   		} catch (IOException ioe) {
			PublicVariable.buttonRequset=true;
   			throw new Error("Unable to create database");

   		}

   		try {

   			dbh.openDataBase();

   		} catch (SQLException sqle) {
			PublicVariable.buttonRequset=true;
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
				 PublicVariable.buttonRequset=true;
	            e.printStackTrace();
			 }
		}
		else
		{
			PublicVariable.buttonRequset=true;
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
        		CallWsMethod("RequestWork");
        	}
	    	catch (Exception e) {
				PublicVariable.buttonRequset=true;
	    		result = e.getMessage().toString();
			}
	        return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
        	if(result == null)
        	{
				PublicVariable.buttonRequset=true;
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
	    PropertyInfo SubjectPI = new PropertyInfo();
	    //Set Name
		SubjectPI.setName("Subject");
	    //Set Value
		SubjectPI.setValue(this.Subject);
	    //Set dataType
		SubjectPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(SubjectPI);
	    //****************************************************
	    PropertyInfo LocationCodePI = new PropertyInfo();
	    //Set Name
		LocationCodePI.setName("LocationCode");
	    //Set Value
		LocationCodePI.setValue(this.LocationCode);
	    //Set dataType
		LocationCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(LocationCodePI);
	    //****************************************************
	    PropertyInfo RadeCodePI = new PropertyInfo();
	    //Set Name
		RadeCodePI.setName("RadeCode");
	    //Set Value
		RadeCodePI.setValue(this.RadeCode);
	    //Set dataType
		RadeCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(RadeCodePI);
	    //****************************************************
	    PropertyInfo RequestTypePI = new PropertyInfo();
	    //Set Name
		RequestTypePI.setName("RequestType");
	    //Set Value
		RequestTypePI.setValue(this.RequestType);
	    //Set dataType
		RequestTypePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(RequestTypePI);
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
	    PropertyInfo PicPI = new PropertyInfo();
	    //Set Name
		PicPI.setName("Pic");
	    //Set Value
		PicPI.setValue(this.Pic);
	    //Set dataType
		PicPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PicPI);
	    //****************************************************
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
		Toast.makeText(this.activity.getApplicationContext(), "درخواست با موفقیت ثبت شد", Toast.LENGTH_LONG).show();
		PublicUpdate.Update(true,false,false,activity,UserCode,PersonCode);
		LoadActivity(MainActivity.class,"UserCode",UserCode,"","");
	}
	public void LoadActivity(Class<?> Cls,String VariableName1,String VariableValue1,
							 String VariableName2,String VariableValue2	)
	{
		Intent intent = new Intent(activity,Cls);
		intent.putExtra(VariableName1, VariableValue1);
		intent.putExtra(VariableName2, VariableValue2);
		activity.startActivity(intent);
	}
}
