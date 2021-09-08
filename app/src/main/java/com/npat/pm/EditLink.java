package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class EditLink  extends Activity {
    DatabaseHelper dbh;
    SQLiteDatabase db;
    EditText TitleLink;
    EditText UrlLink;
    Button btnSave;
    String Code="0";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_link);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try
        {
            Code = getIntent().getStringExtra("Code").toString();
        }
        catch (Exception e) {
            Code = "0";
        }


        TitleLink=(EditText)findViewById(R.id.TitleLink);
        UrlLink=(EditText)findViewById(R.id.UrlLink);
        btnSave=(Button)findViewById(R.id.btnSave);


        dbh=new DatabaseHelper(getApplicationContext());
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

        FillData();

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                db = dbh.getWritableDatabase();
                if(Code.compareTo("0")==0)
                {
                    String query="INSERT INTO Links (Title,Url,Def) VALUES('" + TitleLink.getText().toString() + "','" + UrlLink.getText().toString()+"' , 0 )";
                    db.execSQL(query);
                }
                else
                {
                    String query="UPDATE Links SET Title ='" + TitleLink.getText().toString() + "', Url='" + UrlLink.getText().toString()+"' WHERE id=" + Code;
                    db.execSQL(query);
                }
                LoadActivity(Setting.class, "", "");
                db.close();
				dbh.close();
                dbh.close();
            }
        });
    }

    @SuppressLint("Range")
    private void FillData() {
        String query="SELECT * FROM  Links WHERE id=" + Code;
        try {
            if(!db.isOpen()) {
                db = dbh.getReadableDatabase();
            }
        }
        catch (Exception ex){
            db = dbh.getReadableDatabase();
        }
        Cursor cursors = db.rawQuery(query, null);
        for (int i = 0; i < cursors.getCount(); i++) {
            cursors.moveToNext();
            TitleLink.setText(cursors.getString(cursors.getColumnIndex("Title")));
            UrlLink.setText(cursors.getString(cursors.getColumnIndex("Url")));
        }
        cursors.close();
        db.close();
				dbh.close();
    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(Setting.class, "", "");
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName, VariableValue);
        startActivity(intent);
        this.finish();
    }
}
