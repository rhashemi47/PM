package com.npat.pm;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
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

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

public class AcceptCode extends Activity {
	InternetConnection IC;
	EditText EditTextAcceptCode;
	Button btnLogin;
	String Mobile;
	String Usercode;
	String Personcode;
	private IntentFilter intentFilter;
	private AppSMSBroadcastReceiver appSMSBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptcode);
		smsListener();
		initBroadCast();
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
	private void initBroadCast() {
		intentFilter = new IntentFilter("com.google.android.gms.auth.api.phone.SMS_RETRIEVED");
		appSMSBroadcastReceiver = new AppSMSBroadcastReceiver();
		appSMSBroadcastReceiver.setOnSmsReceiveListener(new AppSMSBroadcastReceiver.OnSmsReceiveListener() {
			@Override
			public void onReceive(String code) {
				//Toast.makeText(AcceptCode.this, code, Toast.LENGTH_SHORT).show();
				if(IC.isConnectingToInternet()==true)
				{
					EditTextAcceptCode.setText(code);
					WsAcceptCode L = new WsAcceptCode(com.npat.pm.AcceptCode.this,Mobile,Usercode,Personcode, code);
					L.AsyncExecute();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "شما به اینترنت دسترسی ندارید", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void smsListener() {
		SmsRetrieverClient client = SmsRetriever.getClient(this);
		client.startSmsRetriever();
	}



	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(appSMSBroadcastReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(appSMSBroadcastReceiver);
	}
}
