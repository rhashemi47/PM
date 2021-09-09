package com.npat.pm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("NewApi")
public class AdapterListHamkaran extends BaseAdapter {

    private ArrayList<HashMap<String, String>> list;
    private Activity activity;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;
    public AdapterListHamkaran(Activity activity, ArrayList<HashMap<String, String>> list) {
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
        TextView txtCode;
        TextView txtTitle;
    }

    // @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        HashMap<String, String> map = list.get(position);
        if (convertView == null) {
            Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
            convertView = inflater.inflate(R.layout.item_list_hamkaran, null);
            holder = new ViewHolder();
            holder.txtCode = (TextView) convertView.findViewById(R.id.txtCode);
            holder.txtCode.setTypeface(faceh);
            holder.txtCode.setTextSize(18);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String code = map.get("Code");
        String Name = map.get("Name");
        String usercode = map.get("UserCode");
        String workCode = map.get("WorkCode");
        String Description = map.get("Description");
        String ImgReport = map.get("ImgReport");
        String Mobile = map.get("Mobile");
        String Personcode = map.get("Personcode");
        holder.txtCode.setText(code);
        holder.txtCode.setTag(code + "##" + workCode + "##" + usercode + "##" + Description+ "##" + Mobile + "##" + Personcode + "##" + ImgReport + "##" );
        holder.txtCode.setOnClickListener(TextViewItemOnclick);
        holder.txtTitle.setTag(code + "##" + workCode + "##" + usercode + "##" + Description+ "##" + Mobile + "##" + Personcode +  "##" + ImgReport + "##");
        holder.txtTitle.setText(Name);
        holder.txtTitle.setOnClickListener(TextViewItemOnclick);

        return convertView;
    }

    private View.OnClickListener TextViewItemOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String item = ((TextView)v).getTag().toString();
            String[] spStr = item.split("##");
            WsWorkStatus LWorkStatus = new WsWorkStatus(activity,spStr[1],spStr[2],"3",spStr[0],spStr[3],spStr[4],spStr[5],spStr[6]);
            LWorkStatus.AsyncExecute();
        }
    };
}

