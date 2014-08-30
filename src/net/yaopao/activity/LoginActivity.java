package net.yaopao.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class LoginActivity extends Activity implements OnTouchListener {
	private TextView to_reset;
	private TextView login;
	private TextView goBack;

	private EditText phoneNumV;
	private EditText pwdV;

	private String phoneNumStr;
	private String pwdStr;
	private String loginJson;
	private Bitmap bitmap;
	private String headUrl;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initLayout();
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.login_goback);
		login = (TextView) this.findViewById(R.id.login_go);
		to_reset = (TextView) this.findViewById(R.id.to_reset);
		to_reset.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		phoneNumV = (EditText) this.findViewById(R.id.login_phoneNum);
		pwdV = (EditText) this.findViewById(R.id.login_pwd);
		dialog = new LoadingDialog(this);
		// dialog.setCanceledOnTouchOutside(false);
		goBack.setOnTouchListener(this);
		to_reset.setOnTouchListener(this);
		login.setOnTouchListener(this);

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.login_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				LoginActivity.this.finish();
				break;
			}
			break;
		case R.id.to_reset:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent myIntent = new Intent();
				myIntent = new Intent(LoginActivity.this,
						ResetPwdActivity.class);
				startActivity(myIntent);
				LoginActivity.this.finish();
				break;
			}
			break;
		case R.id.login_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (verifyParam()) {
					dialog.show();
					Log.v("wyuser", "login0");
					new loginAsyncTask().execute("");
				}
				break;
			}
			break;
		}
		return true;
	}

	private boolean verifyParam() {
		if (!verifyPhone()) {
			return false;
		}
		if (!verifyPwd()) {
			return false;
		}
		return true;
	}

	public boolean verifyPhone() {
		phoneNumStr = phoneNumV.getText().toString().trim();
		Log.v("wy", "phone=" + phoneNumStr);
		if (phoneNumStr != null && !"".equals(phoneNumStr)) {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(phoneNumStr);
			if (m.matches()) {
				return true;
			} else {
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return false;
			}

		} else {
			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public boolean verifyPwd() {

		pwdStr = pwdV.getText().toString().trim();
		Log.v("wy", "pwdStr=" + pwdStr);
		if (pwdStr != null && !"".equals(pwdStr)) {
			Pattern p = Pattern.compile("^[a-zA-z0-9]{6,16}$");
			Matcher m = p.matcher(pwdStr);
			if (m.matches()) {
				return true;
			} else {
				Toast.makeText(this, "请输入正确的密码", Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			Toast.makeText(this, "请输入正确的密码", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private class loginAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(String... params) {
			dialog.dismiss();
			Log.v("wyuser", "login1");
			pwdStr = pwdV.getText().toString().trim();
			phoneNumStr = phoneNumV.getText().toString().trim();
			loginJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.login, "phone=" + phoneNumStr + "&passwd="
					+ pwdStr);
			Log.v("wyuser", "login2");
			Log.v("wyuser", "loginJson" + loginJson);
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
					headUrl = Constants.endpoints
							+ rt.getJSONObject("userinfo").getString("imgpath");
					new Thread(connectNet).start();
					DataTool.setUserInfo(loginJson);
					Toast.makeText(LoginActivity.this, "登录成功",
							Toast.LENGTH_LONG).show();
					// Intent myIntent = new Intent();
					// myIntent = new Intent(LoginActivity.this,
					// MainActivity.class);
					// startActivity(myIntent);
					LoginActivity.this.finish();
					break;
				case -8:
					Toast.makeText(LoginActivity.this, "密码错误",
							Toast.LENGTH_LONG).show();
					break;
				default:
					Toast.makeText(LoginActivity.this, "登录失败，请稍后重试",
							Toast.LENGTH_LONG).show();
					break;
				}

			} else {
				Toast.makeText(LoginActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/*
	 * 连接网络 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
	 */
	private Runnable connectNet = new Runnable() {
		@Override
		public void run() {
			try {
				bitmap = BitmapFactory.decodeStream(getImageStream(headUrl));
				saveFile();
			} catch (Exception e) {
				Toast.makeText(YaoPao01App.getAppContext(), "无法链接网络！",
						Toast.LENGTH_SHORT).show();
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
	public InputStream getImageStream(String path) throws Exception {
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
