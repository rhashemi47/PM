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

public class GetWsGetMyWorkStatusReport {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String PersonCode;
	private String FromDate;
	private String ToDate;
	private String Mobile;
	private String Usercode;
	private boolean CuShowDialog=true;
	//Contractor
	public GetWsGetMyWorkStatusReport(Activity activity,
									   String PersonCode,
									   String FromDate,
									   String ToDate,
									   String Mobile,
									   String Usercode
									   ) {
		this.activity = activity;
		this.PersonCode = PersonCode;
		this.FromDate = FromDate;
		this.ToDate = ToDate;
		this.Mobile = Mobile;
		this.Usercode = Usercode;
		IC = new InternetConnection(this.activity);
		PV = new PublicVariable();
		PublicVariable.My_work = false;
		dbh=new DatabaseHelper(this.activity);
		try {

			dbh.createDataBase();

   		} catch (IOException ioe) {
			PublicVariable.My_work = true;
   			throw new Error("Unable to create database");

   		}

   		try {

   			dbh.openDataBase();

   		} catch (SQLException sqle) {
			PublicVariable.My_work = true;
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
				 PublicVariable.My_work = true;
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
        		CallWsMethod("MyWorkStatusReport");
        	}
	    	catch (Exception e) {
				PublicVariable.My_work = true;
	    		result = e.getMessage().toString();
			}
	        return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
			PublicVariable.My_work = true;
        	if(result == null)
        	{
	            if(WsResponse.toString().compareTo("ER") == 0)
	            {
	            	Toast.makeText(this.activity, "موردی یافت نشد", Toast.LENGTH_LONG).show();
	            }
	            else if(WsResponse.toString().compareTo("0") == 0)
	            {
	            	Toast.makeText(this.activity, "موردی یافت نشد", Toast.LENGTH_LONG).show();

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
	    PropertyInfo GuidPI = new PropertyInfo();
	    //Set Name
	    GuidPI.setName("PersonCode");
	    //Set Value
		GuidPI.setValue(this.PersonCode);
	    //Set dataType
		GuidPI.setType(String.class);
	    //Add the property to request object
		request.addProperty(GuidPI);
		PropertyInfo FromDatePI = new PropertyInfo();
		//Set Name
		FromDatePI.setName("FromDate");
		//Set Value
		FromDatePI.setValue(this.FromDate);
		//Set dataType
		FromDatePI.setType(String.class);
		//Add the property to request object
		request.addProperty(FromDatePI);
		PropertyInfo ToDatePI = new PropertyInfo();
		//Set Name
		ToDatePI.setName("ToDate");
		//Set Value
		ToDatePI.setValue(this.ToDate);
		//Set dataType
		ToDatePI.setType(String.class);
		//Add the property to request object
		request.addProperty(ToDatePI);
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
		res=WsResponse.split("@@");
		db=dbh.getWritableDatabase();
		db.execSQL("delete from MyWorkStatusReport");
		for(int i=0;i<res.length;i++){
			try {
					value=res[i].split("##");

				query="INSERT INTO MyWorkStatusReport (" +
						"Code" +
						",WorkType" +
						",Subject" +
						",Location" +
						",Rade" +
						",Status" +
						",Description" +
						",InsertUser" +
						",StatusDesc" +
						",StatusInsertUser" +
						",InsertDate" +
						",StatusInsertDate" +
						",Pic" +
						",PicReport" +
						") VALUES('"
						+ value[0] +
						"','" + value[1] +
						"','" + value[2] +
						"','" + value[3] +
						"','" + value[4] +
						"','" + value[5] +
						"','" + value[6] +
						"','" + value[7] +
						"','" + value[8] +
						"','" + value[9] +
						"','" + value[10] +
						"','" + value[11] +
						"','" + value[12] +
						"','" + value[13] +
						"')";
						db.execSQL(query);
			}
			catch (Exception e)
			{

			}
		}
		db.close();
		dbh.close();
		LoadActivity(ListReportDuty.class, "Mobile", Mobile,"Usercode",Usercode,"PersonCode",PersonCode);
    }
	public void LoadActivity(Class<?> Cls, String VariableName, String VariableValue, String VariableName2, String VariableValue2, String VariableName3, String VariableValue3)
	{
		Intent intent = new Intent(activity,Cls);
		intent.putExtra(VariableName, VariableValue);
		intent.putExtra(VariableName2, VariableValue2);
		intent.putExtra(VariableName3, VariableValue3);
		activity.startActivity(intent);
	}
}
