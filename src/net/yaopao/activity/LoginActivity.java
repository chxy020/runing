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
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity implements OnTouchListener {
	private TextView to_reset;
	private TextView login;
	private TextView goBack;

	private EditText phoneNumV;
	private EditText pwdV;

	private String phoneNumStr;
	private String pwdStr;
	private String loginJson;
	private LoadingDialog dialog;
	private int loginStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		IntentFilter filter = new IntentFilter(ResetPwdActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);
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
				goBack.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				goBack.setBackgroundResource(R.color.red);
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
				break;
			}
			break;
		case R.id.login_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				login.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				login.setBackgroundResource(R.color.blue_dark);
				if (verifyParam()) {
					dialog.show();
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
			pwdStr = pwdV.getText().toString().trim();
			phoneNumStr = phoneNumV.getText().toString().trim();
			loginJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.login, "phone=" + phoneNumStr + "&passwd="
					+ pwdStr);
			Log.v("wyuser", "loginJson=" + loginJson);
			if (loginJson != null && !"".equals(loginJson)) {

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
					Variables.headUrl  = Constants.endpoints
							+ rt.getJSONObject("userinfo").getString("imgpath");
					try {
						Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
					} catch (Exception e) {
						Log.v("wyuser", "eeeeee="+e.toString());
						e.printStackTrace();
					}
					DataTool.setUserInfo(loginJson);
					
					break;
				case -8:
					loginStatus=-8;
					return false;
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
			dialog.dismiss();
			if (result) {
				setResult(Activity.RESULT_OK);
				LoginActivity.this.finish();
				Toast.makeText(LoginActivity.this, "登录成功",
						Toast.LENGTH_LONG).show();
			} else {
				if (loginStatus==-8) {
					Toast.makeText(LoginActivity.this, "密码错误",
							Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(LoginActivity.this, "登录失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}


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
	// 如果重置密码成功，关闭此页面
			BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					if ("close".equals(intent.getExtras().getString("data"))) {
						LoginActivity.this.finish();
					}
				}
			};
			
			public void onResume() {
				super.onResume();
				MobclickAgent.onResume(this);
			}

			public void onPause() {
				super.onPause();
				MobclickAgent.onPause(this);
			}
}
