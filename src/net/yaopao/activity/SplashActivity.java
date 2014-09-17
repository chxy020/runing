package net.yaopao.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportParaBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

/**
 * 显示起始画面
 */
public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig( this );
		
		Constants.endpoints = MobclickAgent.getConfigParams( this, "mainurl" );
		Log.v("wyuser", "在线参数="+Constants.endpoints);
		if ("".equals(Constants.endpoints)||Constants.endpoints==null) {
			Constants.endpoints=Constants.endpoints1;
		}else{
			Constants.endpoints+="chSports";
		}
		Log.v("wyuser", "Constants.endpoints="+Constants.endpoints);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, Constants.SPLASH_DISPLAY_LENGHT);
		// 利用开头动画这几秒时间可以初始化变量和自动登录
		Variables.uid = YaoPao01App.sharedPreferences.getInt("uid", 0);
		// Log.v("wy", "uid"+Variables.uid+"");
		if (NetworkHandler.isNetworkAvailable(this)) {
			Variables.network = 1;
			if (Variables.uid != 0) {
				// dialog = new LoadingDialog(this);
				// dialog.show();
				new AutoLogin().execute("");
			}

		} else {
			//Toast.makeText(this, "请检查网络", Toast.LENGTH_LONG).show();
		}
		initSportParam();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("wy", "SplashActivity destroy");
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
				Log.v("wyuser", "保存下载的图片");
				Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
				Log.v("wyuser ", "下载headUrl="+Variables.headUrl);
				//saveFile(bitmap);

			} catch (Exception e) {
				e.printStackTrace();
				Log.v("wyuser", e.toString());
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
	public void saveFile(Bitmap bm) throws IOException {
		File dirFile = new File(Constants.avatarPath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(new File(Constants.avatarPath
						+ Constants.avatarName)));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	private class AutoLogin extends AsyncTask<String, Void, Boolean> {
		private String loginJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Log.v("wyuser", "自动登录中");
			loginJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.autoLogin, "uid="
					+ YaoPao01App.sharedPreferences.getInt("uid", 0));
			if (loginJson != null && !"".equals(loginJson)) {
				JSONObject rt = JSON.parseObject(loginJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				Log.v("wypho", "rtCode="+rtCode);
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
						// Looper.prepare();
						messageHandler.obtainMessage(0).sendToTarget();
						// Looper.loop();
					}
					DataTool.setUserInfo(loginJson);
					Log.v("wyuser", "loginJson = " + loginJson);
					break;
				case -7:
					Variables.islogin = 3;
					break;
				default:
					break;
				}

				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// dialog.dismiss();
			if (result) {

				/*JSONObject rt = JSON.parseObject(loginJson);
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
				}*/
			} else {
				Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	/**
	 * 初始化运动参数
	 * 
	 */
	private void initSportParam() {
		SportParaBean param = YaoPao01App.db.querySportParam(Variables.uid);
		if (param.getTargetdis()!=0) {
			Variables.runtarDis=param.getTargetdis();
		}
		if (param.getTargettime()!=0) {
			Variables.runtarTime=param.getTargettime();
		}
		if (param.getTypeIndex()!=0) {
			Variables.runty=param.getTypeIndex();
		}
		Variables.switchTime=param.getCountDown();
		Variables.switchVoice=param.getVioce();
		Variables.runtar =param.getTargetIndex();
		Log.v("wysport", " runtarDis ="+param.getTargetdis());
		Log.v("wysport", " runtarTime ="+param.getTargettime());
		Log.v("wysport", " runty ="+param.getTypeIndex());
		Log.v("wysport", " runtar ="+param.getTargetIndex());
		
	}
}
