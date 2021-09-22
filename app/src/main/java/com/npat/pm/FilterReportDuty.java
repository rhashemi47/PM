package com.npat.pm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.npat.pm.Date.ChangeDate;

import org.jetbrains.annotations.NotNull;

import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;


public class FilterReportDuty extends AppCompatActivity {

    EditText etStartDate;
    EditText etEndDate;
    Button btnApply;
    String Mobile;
    String Usercode;
    String Personcode;
    String StartDateMiladi;
    String EndDateMiladi;
    ImageView imgExit;
    private PersianDatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_report_duty);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        btnApply = (Button) findViewById(R.id.btnApply);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        try
        {
            Mobile = getIntent().getStringExtra("Mobile").toString();
        }
        catch (Exception e) {
            Mobile = "0";
        }
        try
        {
            Usercode = getIntent().getStringExtra("Usercode").toString();
        }
        catch (Exception e) {
            Usercode = "0";
        }
        try
        {
            Personcode = getIntent().getStringExtra("Personcode").toString();
        }
        catch (Exception e) {
            Personcode = "0";
        }
        etStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    picker = new PersianDatePickerDialog(FilterReportDuty.this)
                            .setPositiveButtonString("باشه")
                            .setNegativeButton("بیخیال")
                            .setTodayButton("امروز")
                            .setTodayButtonVisible(true)
                            .setMinYear(1300)
                            //.setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                            //.setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
                            //.setMaxDay(PersianDatePickerDialog.THIS_DAY)
                            .setInitDate(1400, 3, 5)
                            .setActionTextColor(Color.GRAY)
                            //.setTypeFace(typeface)
                            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                            .setShowInBottomSheet(true)
                            .setListener(new PersianPickerListener() {
                                @Override
                                public void onDateSelected(@NotNull PersianPickerDate persianPickerDate) {
                                    StartDateMiladi =persianPickerDate.getGregorianYear() + "-" + persianPickerDate.getGregorianMonth() + "-" + persianPickerDate.getGregorianDay();
                                    etStartDate.setText(persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay());
                                    //Toast.makeText(FilterReportDuty.this, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onDismissed() {

                                }
                            });

                    picker.show();
                }

            }
        });
        etEndDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    picker = new PersianDatePickerDialog(FilterReportDuty.this)
                            .setPositiveButtonString("باشه")
                            .setNegativeButton("بیخیال")
                            .setTodayButton("امروز")
                            .setTodayButtonVisible(true)
                            .setMinYear(1300)

                           // .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                            //.setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
                           // .setMaxDay(PersianDatePickerDialog.THIS_DAY)
                            .setInitDate(1400, 3, 5)
                            .setActionTextColor(Color.GRAY)
                            //.setTypeFace(typeface)
                            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                            .setShowInBottomSheet(true)
                            .setListener(new PersianPickerListener() {
                                @Override
                                public void onDateSelected(@NotNull PersianPickerDate persianPickerDate) {
                                    EndDateMiladi =persianPickerDate.getGregorianYear() + "-" + persianPickerDate.getGregorianMonth() + "-" + persianPickerDate.getGregorianDay();
                                    etEndDate.setText(persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay());
                                    //Toast.makeText(FilterReportDuty.this, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onDismissed() {

                                }
                            });

                    picker.show();
                }

            }
        });
       btnApply.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if( etStartDate.getText().toString() !="" && etEndDate.getText().toString() !="") {

                   SyncWsGetMyWorkStatusReport LWsGetMyWorkStatusReport = new SyncWsGetMyWorkStatusReport(FilterReportDuty.this,
                           Personcode,
                           StartDateMiladi,
                           EndDateMiladi,
                           Mobile,
                           Usercode);
                   LWsGetMyWorkStatusReport.AsyncExecute();
               }
               else
               {
                   Toast.makeText(FilterReportDuty.this, "لطفا تاریخ شروع و پایان را وارد نمایید", Toast.LENGTH_LONG).show();
               }
           }
       });
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout();
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3)
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        startActivity(intent);
        this.finish();
    }
    public static String faToEn(String num) {
        return num
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
    }
    public static String EnToFa(String num) {
        return num
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
    }
}