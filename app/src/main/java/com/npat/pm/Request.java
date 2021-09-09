package com.npat.pm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Request extends AppCompatActivity {

    EditText edtSubject;
    EditText edtDescription;
    SearchableSpinner spnLocation;
    SearchableSpinner spnRade;
    SearchableSpinner spnRequestType;
    Button btnRequset;
    Button btnTakePic;
    ImageView  imgRequest;
    DatabaseHelper dbh;
    SQLiteDatabase db;
    String Mobile;
    String Usercode;
    String Personcode;
    String WorkCode;
    String RequestTypeCode;
    String LocationCode;
    String RadeCode;
    String base64Str="";
    Bitmap imageBitmap = null;
    File destination = null;
    InputStream inputStreamImg;
    String imgPath = null;
    Bitmap bitmap;
    InternetConnection IC;
    static final int REQUEST_IMAGE_CAPTURE = 123;
    static final int REQUEST_IMAGE_Gallery = 124;
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private List<String> labelsRequetType;
    private List<String> labelLocation;
    private List<String> labelRade;

    private HashMap<String,String > mapRequestType=new HashMap<String, String>();
    private HashMap<String,String > mapLocation=new HashMap<String, String>();
    private HashMap<String,String > mapRade=new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.request);
        edtSubject=(EditText) findViewById(R.id.edtSubject);
        edtDescription=(EditText) findViewById(R.id.edtDescription);
        spnRequestType=(SearchableSpinner) findViewById(R.id.spnRequestType);
        spnLocation=(SearchableSpinner) findViewById(R.id.spnLocatio);
        spnRade=(SearchableSpinner) findViewById(R.id.spnRade);
        btnRequset=(Button) findViewById(R.id.btnRequset);
        btnTakePic=(Button) findViewById(R.id.btnTakePic);
        imgRequest=(ImageView) findViewById(R.id.imgRequest);

        //***********************
        spnRequestType.setTitle("نوع درخواست");
        spnRequestType.setPositiveButton("تایید");
        spnLocation.setTitle("موقعیت");
        spnLocation.setPositiveButton("تایید");
        spnRade.setTitle("گروه بندی");
        spnRade.setPositiveButton("تایید");
        //***********************

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
            WorkCode = getIntent().getStringExtra("Code").toString();
        }
        catch (Exception e) {
            WorkCode = "0";
        }
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
        FillSpinnerReqquestType();
        FillSpinnerLocaion();
        FillSpinnerRade();
        if (PublicVariable.buttonRequset) {
            btnRequset.setEnabled(true);
            btnRequset.setBackgroundResource(R.drawable.rounded_button);
        }
        else
        {
            btnRequset.setEnabled(false);
            btnRequset.setBackgroundResource(R.drawable.rounded_button_disable);
        }
        spnRequestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp=spnRequestType.getSelectedItem().toString();
                RequestTypeCode=mapRequestType.get(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp=spnLocation.getSelectedItem().toString();
                LocationCode=mapLocation.get(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnRade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp=spnRade.getSelectedItem().toString();
                RadeCode=mapRade.get(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
            }
        });
        btnRequset.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (PublicVariable.buttonRequset) {
                     if (edtSubject.getText().toString().compareTo("") > 0 && edtDescription.getText().toString().compareTo("") > 0) {
                         IC = new InternetConnection(Request.this);
                         if (IC.isConnectingToInternet()) {
                             try {
                                 btnRequset.setEnabled(false);
                                 btnRequset.setBackgroundResource(R.drawable.rounded_button_disable);
                                 PublicVariable.buttonRequset=false;
                                 WsRequestWork LWsRequestWork = new WsRequestWork(Request.this,
                                         Usercode,
                                         Personcode,
                                         edtSubject.getText().toString(),
                                         LocationCode,
                                         RadeCode,
                                         RequestTypeCode,
                                         edtDescription.getText().toString(),
                                         base64Str);
                                 LWsRequestWork.AsyncExecute();
                             } catch (Exception e) {

                                 e.printStackTrace();
                             }
                         } else {
                             Toast.makeText(Request.this, "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
                         }
                     } else {
                         Toast.makeText(Request.this, "عنوان درخواست و توضیحات را وارد فرمایید.", Toast.LENGTH_LONG).show();
                     }
                 }
             }
         });
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //take_photo();
            selectImage();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(Request.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                selectImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                selectImage();
            }
        }
    }
    @SuppressLint("Range")
    public void FillSpinnerReqquestType(){

        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("SELECT * FROM RequsetType  Order by Code ASC", null);
        if (cursors.getCount() > 0) {
            labelsRequetType  = new ArrayList<String>();
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                mapRequestType.put(cursors.getString(cursors.getColumnIndex("Title")),cursors.getString(cursors.getColumnIndex("Code")));
                labelsRequetType.add(cursors.getString(cursors.getColumnIndex("Title")));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsRequetType){
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);

                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }

                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                    View v =super.getDropDownView(position, convertView, parent);


                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnRequestType.setAdapter(dataAdapter);
        }
        cursors.close();
        db.close();
				dbh.close();
    }
    @SuppressLint("Range")
    public void FillSpinnerLocaion(){

        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("SELECT * FROM Location  Order by Code ASC", null);
        if (cursors.getCount() > 0) {
            labelLocation  = new ArrayList<String>();
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                mapLocation.put(cursors.getString(cursors.getColumnIndex("Title")),cursors.getString(cursors.getColumnIndex("Code")));
                labelLocation.add(cursors.getString(cursors.getColumnIndex("Title")));
            }

            db.close();
				dbh.close();
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelLocation){
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);

                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }

                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                    View v =super.getDropDownView(position, convertView, parent);


                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLocation.setAdapter(dataAdapter);
        }
        cursors.close();
        db.close();
				dbh.close();
    }
    @SuppressLint("Range")
    public void FillSpinnerRade(){

        db = dbh.getReadableDatabase();
        Cursor cursors = db.rawQuery("SELECT * FROM Rade Order by Code ASC", null);
        if (cursors.getCount() > 0) {
            labelRade  = new ArrayList<String>();
            for (int i = 0; i < cursors.getCount(); i++) {
                cursors.moveToNext();
                mapRade.put(cursors.getString(cursors.getColumnIndex("Title")),cursors.getString(cursors.getColumnIndex("Code")));
                labelRade.add(cursors.getString(cursors.getColumnIndex("Title")));
            }

            db.close();
				dbh.close();
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelRade){
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);

                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }

                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                    View v =super.getDropDownView(position, convertView, parent);


                    Typeface typeface=Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
                    ((TextView) v).setTypeface(typeface);

                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnRade.setAdapter(dataAdapter);
        }
        cursors.close();
        db.close();
				dbh.close();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                imgRequest.setVisibility(View.VISIBLE);
                    imgRequest.setImageBitmap(imageBitmap);
                    base64Str = ImageConvertor.BitmapToBase64(imageBitmap);
            }
        }
        else if (requestCode == REQUEST_IMAGE_Gallery && resultCode == RESULT_OK)
        {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                imgRequest.setVisibility(View.VISIBLE);
                imgRequest.setImageBitmap(bitmap);
                base64Str = ImageConvertor.BitmapToBase64(bitmap);

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
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            LoadActivity(MainActivity.class, "Mobile", Mobile,"Usercode", Usercode,"Personcode", Personcode);
            return true;
        }

        return super.onKeyDown( keyCode, event );
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Request.this);
                builder.setTitle("انتخاب گزینه");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item==0) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            } catch (ActivityNotFoundException e) {

                                Toast.makeText(Request.this, "برنامه دوربین پیدا نشد", Toast.LENGTH_SHORT).show();

                            }
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


    public void LoadActivity(Class<?> Cls
            ,String VariableName1,String VariableValue1
            ,String VariableName2,String VariableValue2
            ,String VariableName3,String VariableValue3
    )
    {
        Intent intent = new Intent(this,Cls);
        intent.putExtra(VariableName1, VariableValue1);
        intent.putExtra(VariableName2, VariableValue2);
        intent.putExtra(VariableName3, VariableValue3);
        startActivity(intent);
        this.finish();
    }

}