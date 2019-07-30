package com.kisai.bt.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BTService {

	private BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;

	public BTService(Context context, Handler handler,
			BluetoothAdapter mBluetoothAdapter) {
		// mAdapter = BluetoothAdapter.getDefaultAdapter();
		mAdapter = mBluetoothAdapter;
		mState = AppData.STATE_NONE;
		mHandler = handler;
	}

	private synchronized void setState(int state) {
		mState = state;
		mHandler.obtainMessage(AppData.MESSAGE_STATE_CHANGE, state, -1)
				.sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(AppData.STATE_LISTEN);
	}

	public synchronized void connect(BluetoothDevice device) {

		// if (mState == AppData.STATE_CONNECTING) {
		// if (mConnectThread != null) {
		// mConnectThread.cancel();
		// mConnectThread = null;
		// }
		// }

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(AppData.STATE_CONNECTING);

	}

	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		Message msg = mHandler.obtainMessage(AppData.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(AppData.DEVICE_NAME, device.getName());
		bundle.putString(AppData.DEVICE_MAC, device.getAddress());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(AppData.STATE_CONNECTED);
	}

	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		setState(AppData.STATE_NONE);
	}

	public synchronized void nullify() {
		if (mAdapter != null) {
			mAdapter.disable();
			mAdapter = null;
		}
	}

	public void write(byte[] out) {
		ConnectedThread r;
		synchronized (this) {
			if (mState != AppData.STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		r.write(out);
	}

	private void connectionFailed() {
		setState(AppData.STATE_LISTEN);

		Message msg = mHandler.obtainMessage(AppData.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(AppData.TOAST,
				"Warning! Connection will be terminated");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(AppData.STATE_LISTEN);

		Message msg = mHandler.obtainMessage(AppData.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(AppData.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(AppData.NAME,
						AppData.MY_UUID);
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			setName("AcceptThread");
			BluetoothSocket socket = null;

			while (mState != AppData.STATE_CONNECTED) {
				try {

					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(AppData.K_M_TAG, "accept() failed", e);
					break;
				}

				if (socket != null) {
					synchronized (BTService.this) {
						switch (mState) {
						case AppData.STATE_LISTEN:
						case AppData.STATE_CONNECTING:
							connected(socket, socket.getRemoteDevice());
							break;
						case AppData.STATE_NONE:
						case AppData.STATE_CONNECTED:
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(AppData.K_M_TAG,
										"Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
		}

		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "close() of server failed", e);
			}
		}
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			try {
				tmp = device.createRfcommSocketToServiceRecord(AppData.MY_UUID);
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			setName("ConnectThread");
			mAdapter.cancelDiscovery();

			try {
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(AppData.K_M_TAG,
							"unable to close() socket during connection failure",
							e2);
				}
				BTService.this.start();
				return;
			}

			synchronized (BTService.this) {
				mConnectThread = null;
			}

			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "close() of connect socket failed", e);
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(AppData.K_M_TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(AppData.K_M_TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;
			while (true) {
				try {
					bytes = mmInStream.read(buffer);

					mHandler.obtainMessage(AppData.MESSAGE_READ, bytes, -1,
							buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(AppData.K_M_TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				mHandler.obtainMessage(AppData.MESSAGE_WRITE, -1, -1, buffer)
						.sendToTarget();
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(AppData.K_M_TAG, "close() of connect socket failed", e);
			}
		}
	}
}
