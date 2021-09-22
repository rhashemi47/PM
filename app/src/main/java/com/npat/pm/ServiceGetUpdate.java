package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by hashemi on 02/18/2018.
 */

public class ServiceGetUpdate extends Service {
    Handler mHandler;
    boolean continue_or_stop = true;
    boolean createthread=true;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;
    private String UserCode;
    private String PersonCode;
    Runnable runnable;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e("Service","Start Service");
        continue_or_stop=true;
        dbh = new DatabaseHelper(getApplicationContext());
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
        try { if(!db.isOpen()) { db = dbh.getReadableDatabase();}}	catch (Exception ex){	db = dbh.getReadableDatabase();	}
        if(createthread) {
            mHandler = new Handler();
            new Thread(new Runnable() {
                @SuppressLint("Range")
                @Override
                public void run() {
                    while (continue_or_stop) {
                        try {
                             runnable =new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Service","Run Service");
                                    try { if(!db.isOpen()) { db = dbh.getReadableDatabase();}}	catch (Exception ex){	db = dbh.getReadableDatabase();	}
                                    Cursor coursors = db.rawQuery("SELECT * FROM login", null);
                                    for (int i = 0; i < coursors.getCount(); i++) {
                                        coursors.moveToNext();
                                        UserCode = coursors.getString(coursors.getColumnIndex("Usercode"));
                                        PersonCode = coursors.getString(coursors.getColumnIndex("Personcode"));
                                    }
                                    if(db.isOpen()) {
                                        db.close();
                                    }
                                    PublicUpdate.Update(true,false,false,getApplicationContext(),UserCode,PersonCode);
                                }
                            };
                            mHandler.post(runnable);

                            Thread.sleep(60000); // every 60 seconds
                        } catch (Exception e) {
                            throw new Error(e.getMessage());
                        }
                    }
                }
            }).start();
            createthread=false;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service","Stop Service");
        continue_or_stop=false;
    }
}
