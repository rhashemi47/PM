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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("NewApi")
public class AdapterListMyWorks extends BaseAdapter {


    private ArrayList<HashMap<String, String>> list;
    private Activity activity;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;
    public AdapterListMyWorks(Activity activity, ArrayList<HashMap<String, String>> list) {
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
        TextView txtSubject;
        LinearLayout LinearIdHeader;
    }

    // @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        HashMap<String, String> map = list.get(position);
        if (convertView == null) {
            Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
            convertView = inflater.inflate(R.layout.item_list_accept_mywork, null);
            holder = new ViewHolder();
            holder.txtCode = (TextView) convertView.findViewById(R.id.txtCode);
            holder.txtCode.setTypeface(faceh);
            holder.txtCode.setTextSize(18);
            holder.txtSubject = (TextView) convertView.findViewById(R.id.txtSubject);
            holder.LinearIdHeader = (LinearLayout) convertView.findViewById(R.id.LinearIdHeader);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String code = map.get("Code");
        String Subject = map.get("Subject");
        String RequestType = map.get("RequestType");
        String Mobile = map.get("Mobile");
        String Usercode = map.get("Usercode");
        String Personcode = map.get("Personcode");
        try{
            if(RequestType.compareTo("2")==0 || RequestType.compareTo("????????")==0){
                holder.LinearIdHeader.setBackgroundResource(R.drawable.rounded_head_emergency);
            }
            /*else
            {
                holder.LinearIdHeader.setBackgroundResource(R.drawable.rounded_head_status_normal);
            }*/
        }catch (Exception ex)
        {
           // holder.LinearIdHeader.setBackgroundResource(R.drawable.rounded_head_status_normal);
        }
        holder.txtCode.setOnClickListener(TextViewItemOnclick);
        holder.txtCode.setText(code);
        holder.txtCode.setTag(code + "##" + Mobile + "##" + Usercode + "##" + Personcode);
        holder.txtSubject.setOnClickListener(TextViewItemOnclick);
        holder.txtSubject.setTag(code + "##" + Mobile + "##" + Usercode + "##" + Personcode);
        holder.txtSubject.setText(Subject);

        return convertView;
    }

    private View.OnClickListener TextViewItemOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String item = ((TextView)v).getTag().toString();
            String[] spStr = item.split("##");
            Intent intent = new Intent(activity.getApplicationContext(),MyWork.class);
            intent.putExtra("Code",spStr[0]);
            intent.putExtra("Mobile",spStr[1]);
            intent.putExtra("Usercode",spStr[2]);
            intent.putExtra("Personcode",spStr[3]);
            activity.startActivity(intent);
        }
    };
}

