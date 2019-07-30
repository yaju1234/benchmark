package com.kisai.bt.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensolaris.hub.libmicro.NativeCaseMicro;
import org.zeroxlab.byteunix.NativeCaseUbench;
import org.zeroxlab.utils.BenchUtil;
import org.zeroxlab.utils.Util;
import org.zeroxlab.zeroxbenchmark.Benchmark;
import org.zeroxlab.zeroxbenchmark.Case;
import org.zeroxlab.zeroxbenchmark.CaseArithmetic;
import org.zeroxlab.zeroxbenchmark.CaseCanvas;
import org.zeroxlab.zeroxbenchmark.CaseDrawArc;
import org.zeroxlab.zeroxbenchmark.CaseDrawCircle;
import org.zeroxlab.zeroxbenchmark.CaseDrawCircle2;
import org.zeroxlab.zeroxbenchmark.CaseDrawImage;
import org.zeroxlab.zeroxbenchmark.CaseDrawRect;
import org.zeroxlab.zeroxbenchmark.CaseDrawText;
import org.zeroxlab.zeroxbenchmark.CaseGC;
import org.zeroxlab.zeroxbenchmark.CaseGLCube;
import org.zeroxlab.zeroxbenchmark.CaseJavascript;
import org.zeroxlab.zeroxbenchmark.CaseNeheLesson08;
import org.zeroxlab.zeroxbenchmark.CaseNeheLesson16;
import org.zeroxlab.zeroxbenchmark.CaseScimark2;
import org.zeroxlab.zeroxbenchmark.CaseTeapot;
import org.zeroxlab.zeroxbenchmark.R;
import org.zeroxlab.zeroxbenchmark.Report;

import com.dean.PieColumar;
import com.dean.PieColumarBlankGraph;
import com.dean.PieColumarClientGraph;
import com.dean.PieColumarServerGraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

public class Kisai_BT_Main extends TabActivity implements OnCheckedChangeListener, OnClickListener {

	public static BluetoothDevice device;
	//////////////////////KAran
	public final static String TAG     = "Benchmark";
    public final static String PACKAGE = "org.zeroxlab.zeroxbenchmark";

    public static String mOutputFile = "0xBenchmark";
    public static String finalResult = "";
    private final static int GROUP_DEFAULT = 0;
    private final static int SETTINGS_ID = Menu.FIRST;

    private static String mXMLResult;
    public static String mJSONResult;
    private final static String mOutputXMLFile = "0xBenchmark.xml";
    private final static String mOutputJSONFile = "0xBenchmark.bundle";

    private Button   mRun;
    private Button   mShow;
    private CheckBox mCheckList[];
    private TextView mDesc[];
    private TabHost mTabHost;
    LinkedList<Case> mCases;
    boolean mTouchable = true;
    private int orientation = Configuration.ORIENTATION_UNDEFINED;

    private WakeLock mWakeLock;

    private final String MAIN = "Main";
    private final String D2 = "2D";
    private final String D3 = "3D";
    private final String MATH = "Math";
    private final String VM = "VM";
    private final String NATIVE = "Native";
    private final String MISC = "Misc";

    private CheckBox d2CheckBox;
    private CheckBox d3CheckBox;
    private CheckBox mathCheckBox;
    private CheckBox vmCheckBox;
    private CheckBox nativeCheckBox;
    private CheckBox miscCheckBox;

    private HashMap< String, HashSet<Case> > mCategory = new HashMap< String, HashSet<Case> >();

    private final String trackerUrl = "http://0xbenchmark.appspot.com/static/MobileTracker.html";

    boolean mAutoRun = false;
    boolean mCheckMath = false;
    boolean mCheck2D = false;
    boolean mCheck3D = true;
    boolean mCheckVM = false;
    boolean mCheckNative = false;
    boolean mCheckMisc = false;
    boolean mAutoUpload = false;
	/////////////
	public boolean isAdd = true;

	public String isFirstTime = "0";
	String temp_stat;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//mChatService.stop();
		//mChatService.nullify();
		finalResult="";
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.radio_add) {
			isAdd = true;
			radio_add.setChecked(true);
			radio_mul.setChecked(false);

		} else if (checkedId == R.id.radio_mul) {
			isAdd = false;
			radio_add.setChecked(false);
			radio_mul.setChecked(true);

		}
	}

	private TextView mTitle;
	TelephonyManager telephonyManager;
	private String mConnectedDeviceName = null, mConnectedDeviceMAC = null;
	private ArrayAdapter<String> mConversationArrayAdapter;
	StringBuffer mOutStringBuffer;
	private BluetoothAdapter mBluetoothAdapter = null;
	public static BTService mChatService = null;
	// MyBroadcastReceiver mReceiver;
	int MissedCallCount = 0;
	IntentFilter connected_intn;
	public static long systemTime;
	Context mContext = Kisai_BT_Main.this;
	boolean start = true;
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	EditText editText_first, editText_second, editText_server_res,
			editText_client_res;
	Button button_execute_server, button_execute_client, but_show_res;
	Button btn_connect, btn_discoverable, btn_disconnect;

	private RadioButton radio_add, radio_mul;
	private RadioGroup radiogroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 orientation = getResources().getConfiguration().orientation;
	     PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	     mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
	     mWakeLock.acquire();
		 requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		 setContentView(R.layout.main_act);
		 mTabHost = getTabHost();
		 getLocalBluetoothName();
		///////////Karan
		 mCases = new LinkedList<Case>();
	        Case arith  = new CaseArithmetic();
	        Case javascript = new CaseJavascript();
	        Case scimark2  = new CaseScimark2();
	        Case canvas = new CaseCanvas();
	        Case glcube = new CaseGLCube();
	        Case circle = new CaseDrawCircle();
	        Case nehe08 = new CaseNeheLesson08();
	        Case nehe16 = new CaseNeheLesson16();
	       // Case teapot = new CaseTeapot();
	        Case gc     = new CaseGC();
	        Case libMicro = new NativeCaseMicro();
	        Case libUbench = new NativeCaseUbench();

	        Case dc2 = new CaseDrawCircle2();
	        Case dr = new CaseDrawRect();
	        Case da = new CaseDrawArc();
	        Case di = new CaseDrawImage();
	        Case dt = new CaseDrawText();

	        mCategory.put(D2, new HashSet<Case>());
	        mCategory.put(D3, new HashSet<Case>());
	        mCategory.put(MATH, new HashSet<Case>());
	        mCategory.put(VM, new HashSet<Case>());
	        mCategory.put(NATIVE, new HashSet<Case>());
	        mCategory.put(MISC, new HashSet<Case>());

	        // mflops
	        mCases.add(arith);
	        mCases.add(scimark2);
	        mCases.add(javascript);
	        mCategory.get(MATH).add(arith);
	        mCategory.get(MATH).add(scimark2);
	        mCategory.get(MISC).add(javascript);

	        // 2d
	        mCases.add(canvas);
	        mCases.add(circle);
	        mCases.add(dc2);
	        mCases.add(dr);
	        mCases.add(da);
	        mCases.add(di);
	        mCases.add(dt);

	        mCategory.get(D2).add(canvas);
	        mCategory.get(D2).add(circle);
	        mCategory.get(D2).add(dc2);
	        mCategory.get(D2).add(dr);
	        mCategory.get(D2).add(da);
	        mCategory.get(D2).add(di);
	        mCategory.get(D2).add(dt);

	        // 3d
	        mCases.add(glcube);
	        mCases.add(nehe08);
	        mCases.add(nehe16);
	        
	      //  mCases.add(teapot);
	        mCategory.get(D3).add(glcube);
	        mCategory.get(D3).add(nehe08);
	        mCategory.get(D3).add(nehe16);
	       // mCategory.get(D3).add(teapot);

	        // vm
	        mCases.add(gc);
	        mCategory.get(VM).add(gc);

	        // native
	        mCases.add(libMicro);
	        mCases.add(libUbench);

	        mCategory.get(NATIVE).add(libMicro);
	        mCategory.get(NATIVE).add(libUbench);

	        initViews();

	        Intent intent = getIntent();
	        Bundle bundle = intent.getExtras();
	        if (bundle != null) {
	            mAutoRun = bundle.getBoolean("autorun");
	            mCheckMath = bundle.getBoolean("math");
	            mCheck2D = bundle.getBoolean("2d");
	            mCheck3D = bundle.getBoolean("3d");
	            mCheckVM = bundle.getBoolean("vm");
	            mCheckNative = bundle.getBoolean("native");
	            mAutoUpload = bundle.getBoolean("autoupload");
	            /*String hi=bundle.getString("hi");
	            if(hi!=null && !hi.equalsIgnoreCase("")&&hi.contains(bluethoothDeviceName)){
	            	String result = getResult();
					Log.i(TAG,"\n\n"+result+"\n\n");
					if(mOutputFile!=null){
						mOutputFile="";
					}
					writeResult(mOutputFile, result);
					Intent intent1 = new Intent();
					intent1.putExtra(Report.REPORT, result);
					intent1.putExtra(Report.XML, hi);
					if (mAutoUpload) {
					    intent1.putExtra(Report.AUTOUPLOAD, true);
					    mAutoUpload = false;
					}
					intent1.setClassName(Report.packageName(), Report.fullClassName());
					startActivity(intent);
	            }*/
	        }

	        if (mCheckMath && !mathCheckBox.isChecked()) {
	            mathCheckBox.performClick();
	        }

	        if (mCheck2D && !d2CheckBox.isChecked()) {
	            d2CheckBox.performClick();
	        }

	        if (mCheck3D && !d3CheckBox.isChecked()) {
	            d3CheckBox.performClick();
	        }

	        if (mCheckVM && !vmCheckBox.isChecked()) {
	            vmCheckBox.performClick();
	        }

	        if (mCheckNative && !nativeCheckBox.isChecked()) {
	            nativeCheckBox.performClick();
	        }

	        if (mCheckMisc && !miscCheckBox.isChecked()) {
	            miscCheckBox.performClick();
	        }
	        
	        /*
	        if (intent.getBooleanExtra("AUTO", false)) {
	            ImageView head = (ImageView)findViewById(R.id.banner_img);
	            head.setImageResource(R.drawable.icon_auto);
	            mTouchable = false;
	            initAuto();
	        }
	        */
	        if (mAutoRun) {
	            onClick(mRun);
	        }
		//////////
	        
		radio_add = (RadioButton) findViewById(R.id.radio_add);
		radio_mul = (RadioButton) findViewById(R.id.radio_mul);
		radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
		radiogroup.setOnCheckedChangeListener(this);
		radio_add.setChecked(true);
		radio_mul.setChecked(false);

		mTitle = (TextView) findViewById(R.id.kisai_txt_device_name);

		editText_first = (EditText) findViewById(R.id.editText_first);
		editText_second = (EditText) findViewById(R.id.editText_second);

		editText_server_res = (EditText) findViewById(R.id.editText_server_res);
		editText_client_res = (EditText) findViewById(R.id.editText_client_res);

		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent serverIntent = new Intent(Kisai_BT_Main.this,
						DeviceListActivity.class);
				startActivityForResult(serverIntent,
						AppData.REQUEST_CONNECT_DEVICE);

			}
		});
		btn_discoverable = (Button) findViewById(R.id.btn_discoverable);
		btn_discoverable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLocalBluetoothName();
				ensureDiscoverable();

			}
		});

		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_disconnect.setVisibility(View.GONE);
		btn_disconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChatService.stop();

			}
		});
		but_show_res = (Button) findViewById(R.id.button_show_result);
		but_show_res.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 String result = getResult();
		            Log.i(TAG,"\n\n"+result+"\n\n");
		            writeResult(mOutputFile, result);
		            Intent intent = new Intent();
		            intent.putExtra(Report.REPORT, result);
		            
		            intent.putExtra(Report.XML, mXMLResult);
		            if (mAutoUpload) {
		                intent.putExtra(Report.AUTOUPLOAD, true);
		                mAutoUpload = false;
		            }
		            intent.setClassName(Report.packageName(), Report.fullClassName());
		            startActivity(intent);
		            
		            if(Util.isServer
							&&Util.isClient){
					Intent intent4=new Intent(Kisai_BT_Main.this, PieColumar.class);
					intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent4);
					}
					else if(Util.isServer)
					{
					Intent intent1=new Intent(Kisai_BT_Main.this, PieColumarServerGraph.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent1);
					}
					else if(Util.isClient)
					{
						Intent intent2=new Intent(Kisai_BT_Main.this, PieColumarClientGraph.class);
						intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent2);
					}
					else
					{
						Intent intent3=new Intent(Kisai_BT_Main.this, PieColumarBlankGraph.class);
						intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent3);
					}
		           
			}
		});
		button_execute_server = (Button) findViewById(R.id.button_execute_server);
		button_execute_server.setTag("0");
		button_execute_server.setOnClickListener(new OnClickListener() {

			

			@Override
			public void onClick(View v) {

				
				AppData.isServer=true;
				AppData.isClientDevice=false;
				send_Message(AppData.bluethoothDeviceName + isFirstTime);
			}
		});

		button_execute_client = (Button) findViewById(R.id.button_execute_client);
		button_execute_client.setTag("0");
		button_execute_client.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				button_execute_client.setTag("1");
				AppData.isClient=true;
				AppData.isClientDevice=true;
				Util.isClient=true;

	            int numberOfCaseChecked = 0;
	            for (int i = 0; i < mCheckList.length; i++) {
	                if (mCheckList[i].isChecked()) {
	                    mCases.get(i).reset();
	                    numberOfCaseChecked++;
	                } else {
	                    mCases.get(i).clear();
	                }
	            }
	            if (numberOfCaseChecked > 0)
	                runCase(mCases);
	        	
			}
		});

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

	}
	 public String getLocalBluetoothName(){
		    if(mBluetoothAdapter == null){
		        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    }
		    AppData.bluethoothDeviceName = mBluetoothAdapter.getName();
		    if(AppData.bluethoothDeviceName == null){
		        System.out.println("Name is null!");
		        AppData.bluethoothDeviceName = mBluetoothAdapter.getAddress();
		    }
		    return AppData.bluethoothDeviceName;
		}
	private void initViews() {
        /*
        mRun = (Button)findViewById(R.id.btn_run);
        mRun.setOnClickListener(this);

        mShow = (Button)findViewById(R.id.btn_show);
        mShow.setOnClickListener(this);
        mShow.setClickable(false);

        mLinearLayout = (LinearLayout)findViewById(R.id.list_container);
        mMainView = (LinearLayout)findViewById(R.id.main_view);

        mBannerInfo = (TextView)findViewById(R.id.banner_info);
        mBannerInfo.setText("Hello!\nSelect cases to Run.\nUploaded results:\nhttp://0xbenchmark.appspot.com");
        */

        mTabHost = getTabHost();

        int length = mCases.size();
        mCheckList = new CheckBox[length];
        mDesc      = new TextView[length];
        for (int i = 0; i < length; i++) {
            mCheckList[i] = new CheckBox(this);
            mCheckList[i].setText(mCases.get(i).getTitle());
            mDesc[i] = new TextView(this);
            mDesc[i].setText(mCases.get(i).getDescription());
            mDesc[i].setTextSize(mDesc[i].getTextSize() - 2);
            mDesc[i].setPadding(42, 0, 10, 10);
        }

        TabContentFactory mTCF = new TabContentFactory() {
            public View createTabContent(String tag) {
                ViewGroup.LayoutParams fillParent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                ViewGroup.LayoutParams fillWrap = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams wrapContent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                wrapContent.gravity = Gravity.CENTER;
                LinearLayout.LayoutParams weightedFillWrap = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                weightedFillWrap.weight = 1;

                if (tag.equals(MAIN)) {
                    LinearLayout mMainView = new LinearLayout(Kisai_BT_Main.this);
                    mMainView.setOrientation(1);
                    ScrollView mListScroll = new ScrollView(Kisai_BT_Main.this);

                    LinearLayout mMainViewContainer = new LinearLayout(Kisai_BT_Main.this);
                    mMainViewContainer.setOrientation(1);
                    ImageView mIconView = new ImageView(Kisai_BT_Main.this);
                    mIconView.setImageResource(R.drawable.icon);
                    mIconView.setVisibility(View.GONE);
                    //TextView mBannerInfo = new TextView(Kisai_BT_Main.this);
                   // mBannerInfo.setText("0xbench\nSelect benchmarks in the tabs,\nor batch select:");

                    d2CheckBox = new CheckBox(Kisai_BT_Main.this);
                    d2CheckBox.setText(D2);
                    d2CheckBox.setOnClickListener(Kisai_BT_Main.this);

                    d3CheckBox = new CheckBox(Kisai_BT_Main.this);
                    d3CheckBox.setText(D3);
                    d3CheckBox.setOnClickListener(Kisai_BT_Main.this);

                    mathCheckBox = new CheckBox(Kisai_BT_Main.this);
                    mathCheckBox.setText(MATH);
                    mathCheckBox.setOnClickListener(Kisai_BT_Main.this);

                    vmCheckBox = new CheckBox(Kisai_BT_Main.this);
                    vmCheckBox.setText(VM);
                    vmCheckBox.setOnClickListener(Kisai_BT_Main.this);

                    nativeCheckBox = new CheckBox(Kisai_BT_Main.this);
                    nativeCheckBox.setText(NATIVE);
                    nativeCheckBox.setOnClickListener(Kisai_BT_Main.this);

                    miscCheckBox = new CheckBox(Kisai_BT_Main.this);
                    miscCheckBox.setText(MISC);
                    miscCheckBox.setOnClickListener(Kisai_BT_Main.this);

                   // TextView mWebInfo = new TextView(Kisai_BT_Main.this);
                   // mWebInfo.setText("Uploaded results:\nhttp://0xbenchmark.appspot.com");

                    LinearLayout mButtonContainer = new LinearLayout(Kisai_BT_Main.this);
                    mRun = new Button(Kisai_BT_Main.this);
                    mRun.setVisibility(View.GONE);
                    mShow = new Button(Kisai_BT_Main.this);
                    mShow.setVisibility(View.GONE);
                    mRun.setText("Run");
                    mShow.setText("Show");
                    mRun.setOnClickListener(Kisai_BT_Main.this);
                    mShow.setOnClickListener(Kisai_BT_Main.this);
                    mButtonContainer.addView(mRun, weightedFillWrap);
                    mButtonContainer.addView(mShow, weightedFillWrap);
                    WebView mTracker = new WebView(Kisai_BT_Main.this);
                    mTracker.clearCache(true);
                    mTracker.setWebViewClient(new WebViewClient () {
                        public void onPageFinished(WebView view, String url) {
                            Log.i(TAG, "Tracker: " + view.getTitle() + " -> " + url);
                        }
                        public void onReceivedError(WebView view, int errorCode,
                                                    String description, String failingUrl) {
                            Log.e(TAG, "Track err: " + description);
                        }
                    });
                    mTracker.loadUrl(trackerUrl);
                    mMainViewContainer.addView(mIconView,wrapContent);
                   // mMainViewContainer.addView(mBannerInfo);
                    mMainViewContainer.addView(mathCheckBox);
                    mMainViewContainer.addView(d2CheckBox);
                    mMainViewContainer.addView(d3CheckBox);
                    mMainViewContainer.addView(vmCheckBox);
                    mMainViewContainer.addView(nativeCheckBox);
                    mMainViewContainer.addView(miscCheckBox);
                   // mMainViewContainer.addView(mWebInfo);
                    mMainViewContainer.addView(mButtonContainer, fillWrap);
                    mMainViewContainer.addView(mTracker, 0,0);
                    mListScroll.addView(mMainViewContainer, fillParent);
                    mMainView.addView(mListScroll, fillWrap);

                    return mMainView;

                }

                LinearLayout mMainView = new LinearLayout(Kisai_BT_Main.this);
                mMainView.setOrientation(1);
                ScrollView mListScroll = new ScrollView(Kisai_BT_Main.this);
                LinearLayout mListContainer = new LinearLayout(Kisai_BT_Main.this);
                mListContainer.setOrientation(1);
                mListScroll.addView(mListContainer, fillParent);
                mMainView.addView(mListScroll, fillWrap);

                boolean gray = true;
                int length = mCases.size();
                Log.i(TAG, "L: " + length);
                Log.i(TAG, "TCF: " + tag);
                for (int i = 0; i < length; i++) {
                    if (!mCategory.get(tag).contains(mCases.get(i)))
                        continue;
                    Log.i(TAG, "Add: " + i); 
                    mListContainer.addView(mCheckList[i], fillWrap);
                    mListContainer.addView(mDesc[i], fillWrap);
                    if (gray) {
                        int color = 0xFF333333; //ARGB
                        mCheckList[i].setBackgroundColor(color);
                        mDesc[i].setBackgroundColor(color);
                    }
                    gray = !gray;
                }
                return mMainView;
            }
        };

        mTabHost.addTab(mTabHost.newTabSpec(MAIN).setIndicator(MAIN, getResources().getDrawable(R.drawable.ic_eye)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(D2).setIndicator(D2, getResources().getDrawable(R.drawable.ic_2d)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(D3).setIndicator(D3, getResources().getDrawable(R.drawable.ic_3d)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(MATH).setIndicator(MATH, getResources().getDrawable(R.drawable.ic_pi)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(VM).setIndicator(VM, getResources().getDrawable(R.drawable.ic_vm)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(NATIVE).setIndicator(NATIVE, getResources().getDrawable(R.drawable.ic_c)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(MISC).setIndicator(MISC, getResources().getDrawable(R.drawable.ic_misc)).setContent(mTCF));
    }
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        MenuItem item1 = menu.add(GROUP_DEFAULT, SETTINGS_ID, Menu.NONE, R.string.menu_settings);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem menu) {
	        if (menu.getGroupId() == GROUP_DEFAULT && menu.getItemId() == SETTINGS_ID) {
	            org.zeroxlab.utils.Util.launchActivity(this, "org.zeroxlab.zeroxbenchmark.ActivitySettings");
	        }
	        return true;
	    }

	    @Override
	    public boolean dispatchTouchEvent(MotionEvent event) {
	        if (mTouchable) {
	            return super.dispatchTouchEvent(event);
	        } else {
	            return true;
	        }
	    }

	    @Override
	    public boolean dispatchKeyEvent(KeyEvent event) {
	        if (mTouchable) {
	            return super.dispatchKeyEvent(event);
	        } else {
	            return true;
	        }
	    }

	    @Override
	    public boolean dispatchTrackballEvent(MotionEvent event) {
	        if (mTouchable) {
	            return super.dispatchTrackballEvent(event);
	        } else {
	            return true;
	        }
	    }

	    private void _checkTagCase(String [] Tags) {
	        Arrays.sort(Tags);
	        for (int i = 0; i < mCheckList.length; i++) {
	            String [] caseTags = mCases.get(i).mTags;
	            for (String t: caseTags) {
	                int search = Arrays.binarySearch(Tags, t);
	                if (search >= 0)
	                    mCheckList[i].setChecked(true);
	            }
	        }
	    }

	    private void _checkCatCase(String [] Cats) {
	        Arrays.sort(Cats);
	        for (int i = 0; i < mCheckList.length; i++) {
	            int search = Arrays.binarySearch(Cats, mCases.get(i).mType);
	            if (search  >= 0)
	                mCheckList[i].setChecked(true);
	        }
	    }

	    private void _checkAllCase(boolean check) {
	        for (int i = 0; i < mCheckList.length; i++)
	            mCheckList[i].setChecked(check);
	    }

	    private void initAuto() {
	        Intent intent = getIntent();
	        String TAG = intent.getStringExtra("TAG");
	        String CAT = intent.getStringExtra("CAT");


	        _checkAllCase(false);
	        if (TAG != null)
	            _checkTagCase( TAG.split(",") );
	        if (CAT != null)
	            _checkCatCase( CAT.split(",") );
	        if (TAG == null && CAT == null)
	            _checkAllCase(true);
	        final Handler h = new Handler() {
	            public void handleMessage(Message msg) {
	                if (msg.what == 0x1234)
	                    onClick(mRun);
	            }
	    };
	    
	    final ProgressDialog dialog = new ProgressDialog(this).show(this, "Starting Benchmark", "Please wait...", true, false);
	    new Thread() {
	            public void run() {
	                SystemClock.sleep(1000);
	                dialog.dismiss();
	                Message m = new Message();
	                m.what = 0x1234;
	                h.sendMessage(m);
	            }
	        }.start();
	        mTouchable = true;
	    }
	@Override
	public void onStart() {
		super.onStart();

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, AppData.REQUEST_ENABLE_BT);
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		if (mChatService != null) {
			if (mChatService.getState() == AppData.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(AppData.K_M_TAG, "setupChat()");
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_2);

		mChatService = new BTService(this, mHandler, mBluetoothAdapter);
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatService != null)
			mChatService.stop();
		 	mWakeLock.release();
	}

	private void ensureDiscoverable() {
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppData.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case AppData.STATE_CONNECTED:
					mTitle.setText(mConnectedDeviceName);
					mConversationArrayAdapter.clear();

					break;
				case AppData.STATE_CONNECTING:
					mTitle.setText("Connecting...");
					break;
				case AppData.STATE_LISTEN:
				case AppData.STATE_NONE:
					mTitle.setText("Not connected");
					break;
				}
				break;
			case AppData.MESSAGE_WRITE:

				break;
			case AppData.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				String readMessage = new String(readBuf, 0, msg.arg1);

				if(readMessage.contains(AppData.bluethoothDeviceName)){
					
					/*String result = getResult();
					Log.i(TAG,"\n\n"+result+"\n\n");
					writeResult(mOutputFile, result);
					Intent intent = new Intent();
					intent.putExtra(Report.REPORT, result);
					intent.putExtra(Report.XML, readMessage);
					if (mAutoUpload) {
					    intent.putExtra(Report.AUTOUPLOAD, true);
					    mAutoUpload = false;
					}
					intent.setClassName(Report.packageName(), Report.fullClassName());
					startActivity(intent);*/
				}
				else if(AppData.isServer&&!readMessage.contains(AppData.bluethoothDeviceName)){
					
					//String result = getResult();
					//Log.i(TAG,"\n\n"+result+"\n\n");
					writeResult(mOutputFile, finalResult);
					Intent intent = new Intent();
					intent.putExtra(Report.REPORT, finalResult);
					intent.putExtra(Report.XML, readMessage);
					if (mAutoUpload) {
					    intent.putExtra(Report.AUTOUPLOAD, true);
					    mAutoUpload = false;
					}
					intent.setClassName(Report.packageName(), Report.fullClassName());
					startActivity(intent);
				}
				else{
					/*d3CheckBox.setChecked(true);
					d3CheckBox.performClick();*/
					int numberOfCaseChecked = 0;
		            for (int i = 0; i < mCheckList.length; i++) {
		                if (mCheckList[i].isChecked()) {
		                    mCases.get(i).reset();
		                    numberOfCaseChecked++;
		                } else {
		                    mCases.get(i).clear();
		                }
		            }
		            if (numberOfCaseChecked > 0)
		                runCase(mCases);
		        
				}

				new CountDownTimer(800, 800) {

					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						/*if (temp_stat.equals("0")) {

							temp_stat = "1";
							send_Message("" +"," + temp_stat);
						}*/
						send_Message(mConnectedDeviceName +"," + "");
					}
				}.start();
				break;
			case AppData.MESSAGE_DEVICE_NAME:

				mConnectedDeviceMAC = msg.getData().getString(
						AppData.DEVICE_MAC);

				mConnectedDeviceName = msg.getData().getString(
						AppData.DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case AppData.MESSAGE_TOAST:

				break;
			}
		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AppData.REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {

				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				device = mBluetoothAdapter
						.getRemoteDevice(address);
				mChatService.connect(device);
			}
			break;
		case AppData.REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				setupChat();
			} else {
				Toast.makeText(this, "bt_not_enabled_leaving",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		 default:
			 if (data == null) {
		            Log.i(TAG, "oooops....Intent is null");
		            return;
		        }

		        Case mycase;
		        for (int i = 0; i < mCases.size(); i++) {
		            mycase = mCases.get(i);
		            if (mycase.realize(data)) {
		                mycase.parseIntent(data);
		                break;
		            }
		        }
		        runCase(mCases);
		        
		        
           break;
		}
	}

	public void ShowDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				Kisai_BT_Main.this);
		alertDialogBuilder.setTitle("Your Title");

		alertDialogBuilder
				.setMessage("Click yes to exit!")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void send_Message(String message) {
		if (mChatService.getState() != AppData.STATE_CONNECTED) {
			Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
			return;
		}

		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
			button_execute_server.setTag("1");
			Util.isServer=true;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

        if (v == mRun) {
            int numberOfCaseChecked = 0;
            for (int i = 0; i < mCheckList.length; i++) {
                if (mCheckList[i].isChecked()) {
                    mCases.get(i).reset();
                    numberOfCaseChecked++;
                } else {
                    mCases.get(i).clear();
                }
            }
            if (numberOfCaseChecked > 0)
                runCase(mCases);
        } else if (v == mShow) {
           // String result = getResult();
           // Log.i(TAG,"\n\n"+result+"\n\n");
            writeResult(mOutputFile, finalResult);
            Intent intent = new Intent();
            intent.putExtra(Report.REPORT, finalResult);
            intent.putExtra(Report.XML, mXMLResult);
            if (mAutoUpload) {
                intent.putExtra(Report.AUTOUPLOAD, true);
                mAutoUpload = false;
            }
            intent.setClassName(Report.packageName(), Report.fullClassName());
            startActivity(intent);
        } else if (v == d3CheckBox) {
            int length = mCases.size();
            String tag = ((CheckBox)v).getText().toString();
            for (int i = 0; i < length; i++) {
                if (!mCategory.get(tag).contains(mCases.get(i)))
                    continue;
                mCheckList[i].setChecked(((CheckBox)v).isChecked());
            }
        }
    
	}
	public void runCase(LinkedList<Case> list) {
        Case pointer = null;
        boolean finish = true;
        for (int i = 0; i < list.size(); i++) {
            pointer = list.get(i);
            if (!pointer.isFinish()) {
                finish = false;
                break;
            }
        }

        if (finish) {
//            mBannerInfo.setText("Benchmarking complete.\nClick Show to upload.\nUploaded results:\nhttp://0xbenchmark.appspot.com");
            String result = getResult();
            writeResult(mOutputFile, result);

            final ProgressDialog dialogGetXml = new ProgressDialog(this).show(this, "Generating XML Report", "Please wait...", true, false);
            new Thread() {
                public void run() {
                    mJSONResult = getJSONResult();
                    mXMLResult = getXMLResult();
                    Log.d(TAG, "XML: " + mXMLResult);
                    writeResult(mOutputXMLFile, mXMLResult);
                    Log.d(TAG, "JSON: " + mJSONResult);
                    writeResult(mOutputJSONFile, mOutputFile);
                    mShow.setClickable(true);
                    onClick(mShow);
                    mTouchable = true;
                    dialogGetXml.dismiss();
                }
            }.start();
        } else {
            Intent intent = pointer.generateIntent();
            if (intent != null) {
                startActivityForResult(intent, 0);
            }
        }
    }

    public String getXMLResult() {
        if (mCases.size() == 0)
            return "";

        Date date = new Date();
        //2010-05-28T17:40:25CST
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        String xml = "";
        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml += "<result";
        xml += " executedTimestamp=\"" + sdf.format(date) + "\"";
        xml += " manufacturer=\"" + Build.MANUFACTURER.replace(' ', '_') + "\"";
        xml += " model=\"" + Build.MODEL.replace(' ', '_') + ":" + Build.DISPLAY + "\"";
        xml += " buildTimestamp=\"" + sdf.format(new Date(Build.TIME)) + "\"";
        xml += " orientation=\"" + Integer.toString(orientation) + "\"";

        try { // read kernel version
            BufferedReader procVersion = new BufferedReader( new FileReader("/proc/version") );
            StringBuffer sbuff = new StringBuffer();
            String tmp;
            while ((tmp = procVersion.readLine()) != null)
                sbuff.append(tmp);
            procVersion.close();
            tmp = sbuff.toString().replace("[\n\r]+", " ").replace(" +", ".");
            xml += " version=\"" + tmp + "\"";
        } catch (IOException e){
            Log.e(TAG, "opening /proc/version failed: " + e.toString());
        }

        try { // read and parse cpu info
            BufferedReader procVersion = new BufferedReader(new FileReader("/proc/cpuinfo") );
            StringBuffer sbuff = new StringBuffer();
            String tmp;
            while ((tmp = procVersion.readLine()) != null)
                sbuff.append(tmp + "\n");
            procVersion.close();

            tmp = sbuff.toString();

            sbuff = new StringBuffer();

            Pattern p1 = Pattern.compile("(Processor\\s*:\\s*(.*)\\s*[\n\r]+)");
            Matcher m1 = p1.matcher(tmp);
            if (m1.find()) sbuff.append(m1.group(2));

            Pattern p2 = Pattern.compile("(Hardware\\s*:\\s*(.*)\\s*[\n\r]+)");
            Matcher m2 = p2.matcher(tmp);
            if (m2.find()) sbuff.append(":"+m2.group(2));

            Pattern p3 = Pattern.compile("(Revision\\s*:\\s*(.*)\\s*[\n\r]+)");
            Matcher m3 = p3.matcher(tmp);
            if (m3.find()) sbuff.append(":"+m3.group(2));

            Log.e(TAG, sbuff.toString());
            xml += " cpu=\"" + sbuff.toString() + "\"";
        } catch (IOException e) {
            Log.e(TAG, "opening /proc/version failed: " + e.toString());
        }

        xml += ">";

        Case mycase;
        for (int i = 0; i < mCases.size(); i++) {
            mycase = mCases.get(i);
            xml += mycase.getXMLBenchmark();
        }

        xml += "</result>";
        return xml;
    }

    /*
     * Add Linaro Dashboard Bundle's JSON format support
     * https://launchpad.net/linaro-python-dashboard-bundle/trunk
     */
    public String getJSONResult() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        JSONObject result = new JSONObject();
        try {
            JSONArray testRunsArray = new JSONArray();
            JSONObject testRunsObject = new JSONObject();
            testRunsObject.put("analyzer_assigned_date", sdf.format(date));
            testRunsObject.put("time_check_performed", false);
            // TODO: should be UUID version 1
            testRunsObject.put("analyzer_assigned_uuid", UUID.randomUUID().toString());
            testRunsObject.put("test_id", "0xbench");

            JSONArray testResultsList = new JSONArray();
            Case myCase;
            for (int i = 0; i < mCases.size(); i++) {
                myCase = mCases.get(i);
                JSONArray caseResultList = myCase.getJSONBenchmark();
                for (int j = 0; j < caseResultList.length(); j++) {
                    testResultsList.put(caseResultList.get(j));
                }
            }
            testRunsObject.put("test_results", testResultsList);

            testRunsArray.put(testRunsObject);
            result.put("test_runs", testRunsArray);
            result.put("format", "Dashboard Bundle Format 1.2");
        }
        catch (JSONException jsonE) {
            jsonE.printStackTrace();
        }
       
        return result.toString();
    }

    public String getResult() {
    	
        String result = "";
        Case mycase;
        for (int i = 0; i < mCases.size(); i++) {
            mycase = mCases.get(i);
            if ( !mycase.couldFetchReport() ) continue;
            result += "============================================================\n";
           // finalResult+=finalResult+result;
            result += mycase.getTitle() + "\n";
         //   finalResult+=finalResult+result;
            result += "------------------------------------------------------------\n";
           // finalResult+=finalResult+result;
            result += mycase.getResultOutput().trim() + "\n";
           // finalResult+=finalResult+result;
        }
        result += "============================================================\n";
        
        	  Kisai_BT_Main.finalResult=result;
        
      
        return result;
    }
    
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.i(TAG, "oooops....Intent is null");
            return;
        }

        Case mycase;
        for (int i = 0; i < mCases.size(); i++) {
            mycase = mCases.get(i);
            if (mycase.realize(data)) {
                mycase.parseIntent(data);
                break;
            }
        }
        runCase(mCases);
    }*/

    private boolean writeResult(String filename, String output) {
        File writeDir = new File(BenchUtil.getResultDir(this));
        if (!writeDir.exists()) {
            writeDir.mkdirs();
        }

        File file = new File(writeDir, filename);
        if (file.exists()) {
            Log.w(TAG, "File exists, delete " + writeDir.getPath() + filename);
            file.delete();
        }

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(output.getBytes());
            fos.flush();
        } catch (Exception e) {
            Log.i(TAG, "Write Failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}