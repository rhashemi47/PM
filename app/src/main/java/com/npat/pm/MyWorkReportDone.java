package com.npat.pm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MyWorkReportDone extends AppCompatActivity {

    EditText etdReportDesc;
    ImageView imgReport;
    Button btnSend;
    Button btnTakePic;
    String Mobile;
    String Usercode;
    String Personcode;
    String Code;
    String Subject;
    String WorkType;
    String Location;
    String Rade;
    String Description;
    String DescriptionReport;
    String RequestType;
    String Status;
    String base64Str="";
    Bitmap imageBitmap = null;
    File destination = null;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    InputStream inputStreamImg;
    String imgPath = null;
    Bitmap bitmap;
    ImageView imgExit;
    static final int REQUEST_IMAGE_CAPTURE = 123;
    static final int REQUEST_IMAGE_Gallery = 124;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mywork_report_done);
        etdReportDesc=(EditText) findViewById(R.id.etdReportDesc);

        btnSend=(Button) findViewById(R.id.btnSend);
        btnTakePic=(Button) findViewById(R.id.btnTakePic);
        imgReport=(ImageView) findViewById(R.id.imgReport);
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
        try
        {
            Code = getIntent().getStringExtra("Code").toString();
        }
        catch (Exception e) {
            Code = "0";
        }
        try
        {
            Status = getIntent().getStringExtra("Status").toString();
        }
        catch (Exception e) {
            Status = "-1";
        }
        try
        {
            DescriptionReport = getIntent().getStringExtra("DescriptionReport").toString();
        }
        catch (Exception e) {
            DescriptionReport = "";
        }
        try
        {
            base64Str = getIntent().getStringExtra("base64Str").toString();
        }
        catch (Exception e) {
            base64Str = "";
        }
        //******************************************
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
        if(db != null) {
            if (db.isOpen()) {
                db.close();
				dbh.close();
            }
        }
        etdReportDesc.setText(DescriptionReport);
        if(base64Str.compareTo(" ")!=0)
        {
            imgReport.setImageBitmap(ImageConvertor.Base64ToBitmap(base64Str));
        }
        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("select * from MyWork WHERE Code= '" + Code + "' Order by Code ASC", null);
        if(cursors.getCount() > 0) {
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                Subject=cursors.getString(cursors.getColumnIndex("Subject"));
                WorkType=cursors.getString(cursors.getColumnIndex("WorkType"));
                Location=cursors.getString(cursors.getColumnIndex("Location"));
                Rade=cursors.getString(cursors.getColumnIndex("Rade"));
                RequestType=cursors.getString(cursors.getColumnIndex("RequestType"));
                Description=cursors.getString(cursors.getColumnIndex("Description"));
            }
        }
        cursors.close();
        db.close();
		dbh.close();
        //******************************************
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(etdReportDesc.getText().toString().compareTo("") > 0 && etdReportDesc.getText().toString().compareTo("") > 0)
                {
                    if(base64Str == null || base64Str.compareTo("")==0)
                    {
                        base64Str = "ERROR";
                    }
                        if (Status.compareTo("2") == 0) {
                            WsWorkStatus LWorkStatus = new WsWorkStatus(MyWorkReportDone.this, Code, Usercode, "2", "0", etdReportDesc.getText().toString(), Mobile, Personcode, base64Str);
                            LWorkStatus.AsyncExecute();
                        } else if (Status.compareTo("3") == 0) {
                            LoadActivity2(ListHamkaran.class, "WorkCode", Code, "Usercode", Usercode, "Description", etdReportDesc.getText().toString(), "Mobile", Mobile, "Personcode", Personcode, "ImgReport", " ");
                        } else if (Status.compareTo("4") == 0) {
                            WsWorkStatus LWorkStatus = new WsWorkStatus(MyWorkReportDone.this, Code, Usercode, "4", "0", etdReportDesc.getText().toString(), Mobile, Personcode, base64Str);
                            LWorkStatus.AsyncExecute();
                        } else {
                            Toast.makeText(MyWorkReportDone.this, "هنگام ثبت مشکلی رخ داده است!", Toast.LENGTH_LONG).show();
                        }
                }
                else
                {
                    Toast.makeText(MyWorkReportDone.this, "لطفا فیلد گزارش کار را تکمیل فرمایید.", Toast.LENGTH_LONG).show();
                }
            }
        });
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadActivity(ShowImage.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"Code", Code,"ActivityClass", "MyWorkReportDone","ImageReport", base64Str,"Status",Status,"DescriptionReport",etdReportDesc.getText().toString());
            }
        });
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
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
            LoadActivity2(MyWork.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode,"WorkCode", Code,"ActivityClass", "MyWork","Table", "MyWork");
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
            ,String VariableName5,String VariableValue5
            ,String VariableName6,String VariableValue6
            ,String VariableName7,String VariableValue7
            ,String VariableName8,String VariableValue8
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        intent.putExtra(VariableName5, VariableValue5);
        intent.putExtra(VariableName6, VariableValue6);
        intent.putExtra(VariableName7, VariableValue7);
        intent.putExtra(VariableName8, VariableValue8);
        startActivity(intent);
        this.finish();
    }
    public void LoadActivity2(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
            ,String VariableName4,String VariableValue4
            ,String VariableName5,String VariableValue5
            ,String VariableName6,String VariableValue6
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        intent.putExtra(VariableName4, VariableValue4);
        intent.putExtra(VariableName5, VariableValue5);
        intent.putExtra(VariableName6, VariableValue6);
        startActivity(intent);
        this.finish();
    }
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            take_photo();
            selectImage();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MyWorkReportDone.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }
    public void take_photo()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);
                return;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, "data");
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    // Select image from camera and gallery
    public void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"عکس گرفتن", "انتخاب از گالری","انصراف"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MyWorkReportDone.this);
                builder.setTitle("انتخاب گزینه");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item==0) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        } else if (item==1) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_IMAGE_Gallery);
                        } else if (item==2) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "مجوز دسترسی به دوربین وجود ندارد", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "خطای مجوز دسترسی به دوربین", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                imgReport.setVisibility(View.VISIBLE);
                imgReport.setImageBitmap(imageBitmap);
                base64Str = ImageConvertor.BitmapToBase64(imageBitmap);
                if(!db.isOpen())
                {
                    db=dbh.getWritableDatabase();
                    if(!db.isOpen())
                    {
                        db=dbh.getWritableDatabase();
                        if(!checkCode(Code)) {
                            db.execSQL("INSERT INTO TempPic VALUES ('" + Code + "','" + base64Str + "')");
                        }
                        else {
                            db.execSQL("UPDATE TempPic SET Pic ='" + base64Str + "' WHERE Code='"+ Code + "'");
                        }
                        db.close();
                    }
                    db.close();
                }
            }
        }
        else if (requestCode == REQUEST_IMAGE_Gallery && resultCode == RESULT_OK)
        {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                imgReport.setVisibility(View.VISIBLE);
                imgReport.setImageBitmap(bitmap);
                base64Str = ImageConvertor.BitmapToBase64(bitmap);
                if(!db.isOpen())
                {
                    if(!checkCode(Code)) {
                        db=dbh.getWritableDatabase();
                        db.execSQL("INSERT INTO TempPic VALUES ('" + Code + "','" + base64Str + "')");
                    }
                    else {
                        db=dbh.getWritableDatabase();
                        db.execSQL("UPDATE TempPic SET Pic ='" + base64Str + "' WHERE Code='"+ Code + "'");
                    }
                    db.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public boolean checkCode(String codeStr)
    {
        db=dbh.getReadableDatabase();
        String query = "SELECT * FROM TempPic WHERE Code='" + codeStr + "'";
        Cursor cursor= db.rawQuery(query,null);
        if(cursor.getCount()>0)
        {
            db.close();
            return true;
        }
        else
        {
            db.close();
            return false;
        }
    }
}