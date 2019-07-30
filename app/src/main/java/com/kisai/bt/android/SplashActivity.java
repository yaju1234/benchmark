package com.kisai.bt.android;

import org.zeroxlab.zeroxbenchmark.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		startService();
		
		 new Handler().postDelayed(new Runnable() {
			 
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	            	setBluetooth(true);
	                Intent i = new Intent(SplashActivity.this, Kisai_BT_Main.class);
	                startActivity(i);
	 
	                // close this activity
	                finish();
	            }
	        }, 2000);

	}

	private void startService() {
		Intent intentService = new Intent(this, MyService.class);
		this.startService(intentService);
	}
	
	public static boolean setBluetooth(boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    if (enable && !isEnabled) {
	        return bluetoothAdapter.enable(); 
	    }
	    else if(!enable && isEnabled) {
	        return bluetoothAdapter.enable();
	    }else{
	    	 return bluetoothAdapter.enable();
	    }
	    // No need to change bluetooth state
	 
	}
}
