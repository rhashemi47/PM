package com.npat.pm;

import android.app.ProgressDialog;
import android.content.Context;
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

public class SyncCheckUserStatus {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
	InternetConnection IC;
	private Context activity;
	private String WsResponse;
	private String UserCode;
	private boolean CuShowDialog=true;
	private boolean toast;
	//Contractor
	public SyncCheckUserStatus(Context activity, String UserCode, boolean toast, boolean proccessbar) {
		this.activity = activity;
		this.UserCode = UserCode;
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();

		this.CuShowDialog=proccessbar;
		this.toast=toast;
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
				AsyncCallWS task = new AsyncCallWS(this.activity.getApplicationContext());
				task.execute();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			if(toast) {
				Toast.makeText(this.activity.getApplicationContext(), "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
			}

		}
	}

	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Context activity;

		public AsyncCallWS(Context activity) {
			this.activity = activity;
			this.dialog = new ProgressDialog(activity);
			this.dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				CallWsMethod("CheckUserStatus");
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
					if(toast) {
						Toast.makeText(this.activity.getApplicationContext(), "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
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
		db=dbh.getWritableDatabase();
		String query="UPDATE login SET Status = '" + WsResponse + "'";
		try {
			db.execSQL(query);
		}
		catch (Exception e)
		{

		}
		if(WsResponse.compareTo("0")==0)
		{
			LoadActivity(Login.class);
		}
		db.close();
		dbh.close();
	}
	public void LoadActivity(Class<?> Cls)
	{
		Intent intent = new Intent(activity,Cls);
		activity.startActivity(intent);
	}
}
