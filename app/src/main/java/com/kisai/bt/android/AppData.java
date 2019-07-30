package com.kisai.bt.android;

import java.util.UUID;

public class AppData {

	static final String K_M_TAG = "Kisai Main Activity";
	static final boolean D = true;
	static final String NAME = "BluetoothChat";
	static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static  boolean isServer = false;
	public static  boolean isClient = false;
	public static  boolean isClientDevice = false;
	public static  String bluethoothDeviceName = "";
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	static final int REQUEST_CONNECT_DEVICE = 1;
	static final int REQUEST_ENABLE_BT = 2;
	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_MAC = "device_mac";
	public static final String TOAST = "toast";

}
