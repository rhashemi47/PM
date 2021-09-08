package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("NewApi")
public class AdapterListLink extends BaseAdapter {


    private ArrayList<HashMap<String, String>> list;
    private Activity activity;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;
    public AdapterListLink(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    // @Override
    public int getCount() {
        return list.size();
    }

    // @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtValues;
        ImageView imgEdit;
        ImageView imgDelete;
        CheckBox chbSelect;
    }

    // @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        HashMap<String, String> map = list.get(position);
        if (convertView == null) {
            Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
            convertView = inflater.inflate(R.layout.item_list_link, null);
            holder = new ViewHolder();
            holder.txtValues = (TextView) convertView.findViewById(R.id.txtTile);
            holder.txtValues.setTypeface(faceh);
            holder.txtValues.setTextSize(18);
            holder.imgEdit = (ImageView) convertView.findViewById(R.id.imgEdit);
            holder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);
            holder.chbSelect = (CheckBox) convertView.findViewById(R.id.chbSelect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = map.get("Title");
        String code = map.get("Code");
        String def = map.get("Def");
        String StrUrl = map.get("Url");
        holder.txtValues.setText(name);
        holder.txtValues.setTag(code);
        holder.txtValues.setOnClickListener(TextViewItemOnclick);
        if(def!= null)
        {
            if(def.compareTo("1")==0) {
                holder.chbSelect.setChecked(true);

                PublicVariable.URL = PublicVariable.URL.replace("##",StrUrl);
            }
            else
            {
                holder.chbSelect.setChecked(false);
            }
        }
        else
        {
            holder.chbSelect.setChecked(false);
        }
        holder.imgEdit.setTag(code);
        holder.imgDelete.setTag(code);
        holder.chbSelect.setTag(code);
        holder.chbSelect.setOnClickListener(TextViewItemOnclick);
        holder.imgEdit.setOnClickListener(EditImageClick);
        holder.imgDelete.setOnClickListener(DeleteImageClick);

        return convertView;
    }


    private View.OnClickListener DeleteImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dbh = new DatabaseHelper(activity);
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
            String item = ((ImageView) v).getTag().toString();
            db = dbh.getWritableDatabase();
            db.execSQL("Delete FROM Links where id=" + item);
            db.close();
				dbh.close();
            dbh.close();
        }
    };
    private View.OnClickListener TextViewItemOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dbh = new DatabaseHelper(activity);
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
            String item = ((TextView)v).getTag().toString();
            db = dbh.getWritableDatabase();
            db.execSQL("UPDATE Links SET Def = 0");
            db.execSQL("UPDATE Links SET Def = 1 where id="+ item);
            db.close();
				dbh.close();
            dbh.close();
        }
    };
    private View.OnClickListener EditImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String item = ((ImageView)v).getTag().toString();
            Intent intent = new Intent(activity.getApplicationContext(),EditLink.class);
            intent.putExtra("Code",item);
            activity.startActivity(intent);
        }
    };
}

