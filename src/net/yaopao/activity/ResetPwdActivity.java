package net.yaopao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPwdActivity extends Activity implements OnTouchListener {
	public static final String closeAction = "resetpwd_close.action";
	public TextView reset;
	public TextView goBack;
	public TextView getCodeV;
	public EditText codeV;

	public EditText phoneNumV;
	public EditText pwdV;

	public String phoneNumStr;
	public String pwdStr;
	public String resetJson;
	public String codeStr;
	public String verifyCodeJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset);
		initLayout();
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.reset_goback);
		reset = (TextView) this.findViewById(R.id.reset_go);
		phoneNumV = (EditText) this.findViewById(R.id.reset_phoneNum);
		pwdV = (EditText) this.findViewById(R.id.reset_pwd);
		getCodeV = (TextView) this.findViewById(R.id.reset_get_code);
		codeV = (EditText) this.findViewById(R.id.reset_veri_code);

		goBack.setOnTouchListener(this);
		reset.setOnTouchListener(this);
		getCodeV.setOnTouchListener(this);

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.reset_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				goBack.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				goBack.setBackgroundResource(R.color.red);
				ResetPwdActivity.this.finish();
				break;
			}
			break;
		case R.id.reset_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				reset.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				reset.setBackgroundResource(R.color.blue_dark);
				if (verifyParam()) {
					new resetAsyncTask().execute("");
				}
				break;
			}
			break;
		case R.id.reset_get_code:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				getCodeV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				getCodeV.setBackgroundResource(R.color.blue_dark);
				Log.v("wy", "点击了获取验证码按钮");
				if (verifyPhone()) {
					new verifyCodAsyncTask().execute("");
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
		if (!verifyCode()) {
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

	public boolean verifyCode() {
		codeStr = codeV.getText().toString().trim();
		Log.v("wy", "codeStr=" + codeStr);
		if (codeStr != null && !"".equals(codeStr)) {
			Pattern p = Pattern.compile("^[0-9]{6}$");
			Matcher m = p.matcher(codeStr);
			if (m.matches()) {
				return true;
			} else {
				Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			Toast.makeText(this, "请输入正确的验证密码", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private class resetAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// 最先执行的就是这个。
		}

		@Override
		protected Boolean doInBackground(String... params) {
			pwdStr = pwdV.getText().toString().trim();
			phoneNumStr = phoneNumV.getText().toString().trim();
			codeStr = codeV.getText().toString().trim();
			try {
				resetJson = NetworkHandler.httpPost(Constants.endpoints
						+ Constants.modifyPwd, "phone=" + phoneNumStr
						+ "&passwd=" + pwdStr + "&vcode=" + codeStr);
			} catch (Exception e) {
				Toast.makeText(ResetPwdActivity.this, "网络错误", Toast.LENGTH_LONG)
						.show();
				e.printStackTrace();
			}
			if (resetJson != null && !"".equals(resetJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.v("wy", resetJson);
				JSONObject rt = JSON.parseObject(resetJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				switch (rtCode) {
				case 0:
					Variables.islogin = 1;
					Toast.makeText(ResetPwdActivity.this, "重置密码成功",
							Toast.LENGTH_LONG).show();
					//发广播通知注册和登录页面关闭
					Intent closeintent = new Intent(closeAction);
					closeintent.putExtra("data", "close");
					sendBroadcast(closeintent);
//					Intent myIntent = new Intent();
//					myIntent = new Intent(ResetPwdActivity.this,
//							MainActivity.class);
//					startActivity(myIntent);
					ResetPwdActivity.this.finish();
					break;
				case -2:
					Toast.makeText(ResetPwdActivity.this, "验证码错误",
							Toast.LENGTH_LONG).show();
					break;
				default:
					Toast.makeText(ResetPwdActivity.this, "重置密码失败，请稍后重试",
							Toast.LENGTH_LONG).show();
					break;
				}
			} else {
				Toast.makeText(ResetPwdActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class verifyCodAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// 最先执行的就是这个。
		}

		@Override
		protected Boolean doInBackground(String... params) {

			try {
				verifyCodeJson = NetworkHandler.httpPost(Constants.endpoints
						+ Constants.forgetPwdCode, "phone=" + phoneNumStr);
			} catch (Exception e) {
				Toast.makeText(ResetPwdActivity.this, "网络错误", Toast.LENGTH_LONG)
						.show();
				e.printStackTrace();
			}
			if (verifyCodeJson != null && !"".equals(verifyCodeJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.v("wy", verifyCodeJson);
				JSONObject rt = JSON.parseObject(verifyCodeJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Toast.makeText(ResetPwdActivity.this, "验证码获取成功",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ResetPwdActivity.this, "验证码获取失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ResetPwdActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
