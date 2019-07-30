/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zeroxlab.zeroxbenchmark;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.*;
import java.nio.*;

import java.util.LinkedList;

import com.kisai.bt.android.AppData;
import com.kisai.bt.android.Kisai_BT_Main;

/* Construct a basic UI */
public class Report extends Activity implements View.OnClickListener {

    public final static String TAG = "Repord";
    public final static String REPORT = "REPORT";
    public final static String XML = "XML";
    public final static String AUTOUPLOAD = "AUTOUPLOAD";
    private TextView mTextView;

    private Button mUpload;
    private Button mBack;
    private String mXMLResult;
    boolean mAutoUpload = false;
    String report;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.report);

        mTextView = (TextView)findViewById(R.id.report_text);

        mUpload = (Button)findViewById(R.id.btn_upload);
        mUpload.setOnClickListener(this);

        mBack = (Button)findViewById(R.id.btn_back);
        mBack.setOnClickListener(this);

        Intent intent = getIntent();
        report = intent.getStringExtra(REPORT);
        mXMLResult = intent.getStringExtra(XML);
        report=report+"\n"+mXMLResult;
        
        mAutoUpload = intent.getBooleanExtra(AUTOUPLOAD, false);

        if (report == null || report.equals("")) {
            mTextView.setText("oooops...report not found");
        } 
        else {
        	mTextView.setText("");
            mTextView.setText(report);
            report="";
        }
       /* if(!Kisai_BT_Main.finalResult.equalsIgnoreCase("")&&Kisai_BT_Main.finalResult!=null&&Kisai_BT_Main.finalResult.toString().length()>2)
        {
        	 mTextView.setText(Kisai_BT_Main.finalResult);
        }else{
        	
        }*/
        if (mXMLResult == null) {
            mUpload.setEnabled(false);
        }

        if (mAutoUpload) {
            onClick(mUpload);
        }
        if(AppData.isServer/*&&AppData.isClient*/){
        	mUpload.setVisibility(View.INVISIBLE);
        	mBack.setVisibility(View.INVISIBLE);
        	AppData.isClient=false;
        }
        
        else if(AppData.isClientDevice/*&&AppData.isClient*/){
        	mUpload.setVisibility(View.INVISIBLE);
        	mBack.setVisibility(View.INVISIBLE);
        	AppData.isClient=false;
        } else{
        	mUpload.setVisibility(View.INVISIBLE);
        	mBack.setVisibility(View.INVISIBLE);
        	
        	AppData.isClient=true;
            send_Message(Kisai_BT_Main.finalResult+"\n"+mXMLResult);
            //finish();
        }
       
    }

    public void onClick(View v) {
        if (v == mBack) {
        	AppData.isServer=false;
        	AppData.isClient=false;
        	AppData.isClientDevice=false;
            finish();
        } else if (v == mUpload) {
            Intent intent = new Intent();
            intent.putExtra(Upload.XML, mXMLResult);
            if (mAutoUpload) {
                intent.putExtra(Upload.AUTOUPLOAD, true);
            }
            intent.setClassName(Upload.packageName(), Upload.fullClassName());
            startActivity(intent);
        }
    }

    public static String fullClassName() {
        return "org.zeroxlab.zeroxbenchmark.Report";
    }

    
    public static String packageName() {
        return "org.zeroxlab.zeroxbenchmark";
    }
    private void send_Message(String message) {
		if (Kisai_BT_Main.mChatService.getState() != AppData.STATE_CONNECTED) {
			Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
			return;
		}

		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			Kisai_BT_Main.mChatService.write(send);

		}
	}
}
