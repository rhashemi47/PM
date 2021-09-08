package com.npat.pm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WsDownLoadUpdate {

    //Primary Variable
//    DatabaseHelper dbh;
//    SQLiteDatabase db;
    PublicVariable PV;
    InternetConnection IC;
    private Activity activity;
    private String CuAppVersion,CuUpdateUrl,CuDownLoadUrl;
    private String WsResponse;
    private Boolean IsUpTodate = false;
    //Contractor
    public WsDownLoadUpdate(Activity activity, String AppVersion, String UpdateUrl, String DownLoadUrl) {
        this.activity = activity;
        this.CuAppVersion = AppVersion;
        this.CuUpdateUrl = UpdateUrl;
        this.CuDownLoadUrl = DownLoadUrl;
        IC = new InternetConnection(this.activity.getApplicationContext());
        PV = new PublicVariable();

//        dbh=new DatabaseHelper(this.activity.getApplicationContext());
//        try {
//
//            dbh.createDataBase();
//
//        } catch (IOException ioe) {
//
//            throw new Error("Unable to create database");
//
//        }
//
//        try {
//
//            dbh.openDataBase();
//
//        } catch (SQLException sqle) {
//
//            throw sqle;
//        }
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
            //Toast.makeText(this.activity.getApplicationContext(), "شما به اینترنت دسترسی ندارید", Toast.LENGTH_SHORT).show();
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
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localcontext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(CuUpdateUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet,localcontext);

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String VersionFromWeb = reader.readLine().toString();
               // String sp[]=CuAppVersion.split("\\.");
                if(CuAppVersion.compareTo(VersionFromWeb) >= 0)
                    IsUpTodate = false;
                else
                    IsUpTodate = true;
            }
            catch (Exception e) {
                result = e.getMessage().toString();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(IsUpTodate)
            {
                InsertDataFromWsToDb();
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
//            this.dialog.setMessage("در حال دریافت اطلاعات");
//            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }



    public void InsertDataFromWsToDb()
    {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        // set the message to display
        alertbox.setMessage("نسخه جدید برنامه در دسترس می باشد ، آیا میخواهید بروزرسانی کنید ؟");

        // set a negative/no button and create a listener
        alertbox.setPositiveButton("خیر", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        // set a positive/yes button and create a listener
        alertbox.setNegativeButton("بله", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //Declare Object From Get Internet Connection Status For Check Internet Status
                Uri uri = Uri.parse(PV.DownloadAppUpdateLinkAPK); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });

        alertbox.show();

//        db.close();
//        dbh.close();
    }


    public void LoadActivity(Class<?> Cls, String VariableName, String VariableValue)
    {
        Intent intent = new Intent(activity,Cls);
        intent.putExtra(VariableName, VariableValue);
        activity.startActivity(intent);
    }

}
