package net.yaopao.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {
	private Bitmap bitmap;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			Log.v("wy", "unconnect");
			Variables.network = 0;
		} else {
			Log.v("wy", "connect");
			Variables.network = 1;
			if (Variables.uid != 0) {
				new AutoLogin().execute("");
			}
		}
	}

	private class AutoLogin extends AsyncTask<String, Void, Boolean> {
		private String loginJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			loginJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.autoLogin, "uid="
					+ YaoPao01App.sharedPreferences.getInt("uid", 0));
			if (loginJson != null && !"".equals(loginJson)) {
				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {

				JSONObject rt = JSON.parseObject(loginJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				switch (rtCode) {
				case 0:
					Variables.islogin = 1;
					Variables.uid = rt.getJSONObject("userinfo").getInteger(
							"uid");
					Variables.utype = rt.getJSONObject("userinfo").getInteger(
							"utype");
					// 下载头像
					Variables.headUrl = Constants.endpoints
							+ rt.getJSONObject("userinfo").getString("imgpath");
					if (Variables.headUrl != null
							&& !"".equals(Variables.headUrl)) {
						messageHandler.obtainMessage(0).sendToTarget();
					}
					DataTool.setUserInfo(loginJson);
					Log.v("wyuser", "loginJson = " + loginJson);
					break;
				default:
					break;
				}
			} else {
//				Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试",
//						Toast.LENGTH_LONG).show();
			}
		}

	}

	public Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				new Thread(connectNet).start();
			}

		}
	};
	/*
	 * 连接网络 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
	 */
	private Runnable connectNet = new Runnable() {
		@Override
		public void run() {
			try {
				bitmap = BitmapFactory
						.decodeStream(getImageStream(Variables.headUrl));
				saveFile();
			} catch (Exception e) {
//				Toast.makeText(YaoPao01App.getAppContext(), "无法链接网络！",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		}

	};

	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public void saveFile() throws IOException {
		File dirFile = new File(Constants.avatarPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(new File(Constants.avatarPath
						+ Constants.avatarName)));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}
}