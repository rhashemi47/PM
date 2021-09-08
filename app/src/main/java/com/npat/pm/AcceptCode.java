package com.npat.pm;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AcceptCode extends Activity {
	InternetConnection IC;
	EditText EditTextAcceptCode;
	Button btnLogin;
	String Mobile;
	String Usercode;
	String Personcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptcode);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
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
        
        Typeface FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        EditTextAcceptCode = (EditText)findViewById(R.id.EditTextAcceptCode);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        EditTextAcceptCode.setTypeface(FontMitra);
        btnLogin.setTypeface(FontMitra);

		if (ActivityCompat.checkSelfPermission(com.npat.pm.AcceptCode.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(com.npat.pm.AcceptCode.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
		}
        
        btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(IC.isConnectingToInternet()==true)
				{
					if(EditTextAcceptCode.getText().length() > 0)
					{
						WsAcceptCode L = new WsAcceptCode(com.npat.pm.AcceptCode.this,Mobile,Usercode,Personcode, EditTextAcceptCode.getText().toString());
						L.AsyncExecute();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "لطفا کد تایید را وارد کنید", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "شما به اینترنت دسترسی ندارید", Toast.LENGTH_SHORT).show();
				}
			}
		});
    }
}
