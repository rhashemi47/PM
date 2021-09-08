package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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

public class GetWsMyWorkList {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
	InternetConnection IC;
	private Activity activity;
	private String WsResponse;
	private String PersonCode;
	private boolean CuShowDialog = true;
	private boolean toast;
	private boolean notifi;

	//Contractor
	public GetWsMyWorkList(Activity activity, String PersonCode, boolean toast, boolean proccessbar, boolean notifi) {
		this.activity = activity;
		this.PersonCode = PersonCode;
		IC = new InternetConnection(this.activity);
		PV = new PublicVariable();
		PublicVariable.Accept_work = false;
		this.CuShowDialog = proccessbar;
		this.toast = toast;
		this.notifi = notifi;
		dbh = new DatabaseHelper(this.activity);
		try {

			dbh.createDataBase();

		} catch (IOException ioe) {
			PublicVariable.Accept_work = true;
			throw new Error("Unable to create database");

		}

		try {

			dbh.openDataBase();

		} catch (SQLException sqle) {
			PublicVariable.Accept_work = true;
			throw sqle;
		}
	}

	public void AsyncExecute() {
		if (IC.isConnectingToInternet() == true) {
			try {
				AsyncCallWS task = new AsyncCallWS(this.activity);
				task.execute();
			} catch (Exception e) {
				PublicVariable.Accept_work = true;
				e.printStackTrace();
			}
		} else {
			//Toast.makeText(this.activity, "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
		}
	}

	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;

		public AsyncCallWS(Activity activity) {
			this.activity = activity;
			this.dialog = new ProgressDialog(this.activity);
			this.dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				CallWsMethod("MyWorkList");
			} catch (Exception e) {
				PublicVariable.Accept_work = true;
				result = e.getMessage().toString();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			PublicVariable.Accept_work = true;
			if (result == null) {
				if (WsResponse.toString().compareTo("ER") == 0) {
					if (toast) {
						Toast.makeText(this.activity, "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
					}
				} else {
					InsertDataFromWsToDb(WsResponse);
				}
			} else {
				//Toast.makeText(this.activity, "ط®ط·ط§ ط¯ط± ط§طھطµط§ظ„ ط¨ظ‡ ط³ط±ظˆط±", Toast.LENGTH_SHORT).show();
			}
			try {
				if (this.dialog.isShowing()) {
					this.dialog.dismiss();
				}
			} catch (Exception e) {
			}
		}

		@Override
		protected void onPreExecute() {
			if (CuShowDialog) {
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
		//db.execSQL("delete from MyWork");
		if(WsResponse.compareTo("0")!=0) {
			for (int i = 0; i < res.length; i++) {
				try {
					value = res[i].split("##");
					boolean check = checkCode(value[0]);
					if (!check) {
						query = "INSERT INTO MyWork (" +
								"Code" +
								",WorkType" +
								",Subject" +
								",Location" +
								",Rade" +
								",Description" +
								",Status" +
								",Status2" +
								",InsertUser" +
								",StatusDesc" +
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
								"','" + value[8] +
								"','" + value[9] +
								"','" + value[10] +
								"')";
						db.execSQL(query);
						if(notifi) {
							NotificationClass notifi = new NotificationClass();
							notifi.Notificationm(this.activity, activity.getString(R.string.app_name), "کد سرویس: " + value[0] + "\n" + "عنوان:" + value[2], value[0], i, MyWorkList.class);
						}
					}
					else if(!checkStatusAndUser(value[0],value[6],value[8]))
					{
						query = "UPDATE MyWork SET Status='" + value[6] + "',InsertUser='" + value[8] + "' WHERE Code='" + value[0]+ "'";
						db.execSQL(query);
						if(notifi) {
							NotificationClass notifi = new NotificationClass();
							notifi.Notificationm(this.activity, activity.getString(R.string.app_name), "کد سرویس: " + value[0] + "\n" + "عنوان:" + value[2], value[0], i, MyWorkList.class);
						}
					}
				}
				catch (Exception e)
				{

				}
			}
		}
		db.close();
		dbh.close();
	}
	public boolean checkStatusAndUser(String codeStr,String StatusStr,String User)
	{
		db=dbh.getReadableDatabase();
		String query = "SELECT * FROM MyWork WHERE Code='" + codeStr + "' AND Status = '" + StatusStr + "' AND InsertUser = '" + User + "'";
		Cursor cursor= db.rawQuery(query,null);
		if(cursor.getCount()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean checkCode(String codeStr)
	{
		db=dbh.getReadableDatabase();
		String query = "SELECT * FROM MyWork WHERE Code='" + codeStr + "'";
		Cursor cursor= db.rawQuery(query,null);
		if(cursor.getCount()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
