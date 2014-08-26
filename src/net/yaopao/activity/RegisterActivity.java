package net.yaopao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnTouchListener {
	public TextView reg;
	public TextView getCodeV;
	public EditText phoneNumV;
	public EditText pwdV;
	public EditText codeV;
	public TextView toLogin;
	public TextView goBack;

	public String phoneNumStr;
	public String pwdStr;
	public String codeStr;
	public String regJson;
	public String verifyCodeJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initLayout();
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.reg_goback);
		toLogin = (TextView) this.findViewById(R.id.to_login);
		getCodeV = (TextView) this.findViewById(R.id.reg_get_code);
		reg = (TextView) this.findViewById(R.id.reg_go);
		toLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		phoneNumV = (EditText) this.findViewById(R.id.reg_phoneNum);
		pwdV = (EditText) this.findViewById(R.id.reg_pwd);
		codeV = (EditText) this.findViewById(R.id.reg_veri_code);

		getCodeV.setOnTouchListener(this);
		goBack.setOnTouchListener(this);
		toLogin.setOnTouchListener(this);
		reg.setOnTouchListener(this);

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
			Pattern p = Pattern.compile("^[0-9]{4,}$");
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.reg_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				RegisterActivity.this.finish();
				break;
			}
			break;

		case R.id.to_login:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent myIntent = new Intent();
				myIntent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(myIntent);
				RegisterActivity.this.finish();
				break;
			}
			break;
		case R.id.reg_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (verifyParam()) {
					new regAsyncTask().execute("");
				}
				break;
			}
			break;
		case R.id.reg_get_code:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
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

	private class regAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// 最先执行的就是这个。
		}

		@Override
		protected Boolean doInBackground(String... params) {
			codeStr = codeV.getText().toString().trim();
			pwdStr = pwdV.getText().toString().trim();
			phoneNumStr = phoneNumV.getText().toString().trim();
			DataTool.setPhone(phoneNumStr);
			regJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.reg, "phone=" + phoneNumStr + "&passwd="+ pwdStr + "&vcode=" + codeStr);
			if (regJson!=null&&!"".equals(regJson)) {
				return true;
			}else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.v("wy", regJson);
				JSONObject rt = JSON.parseObject(regJson);
				int rtCode = 0;
				int uid = 0;
				rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Variables.uid = rt.getInteger("uid");
					Variables.utype=rt.getInteger("utype");
					Variables.islogin = 1;
					DataTool.setUserInfo(regJson);
					Log.v("wy", "save info ="+regJson);
					Log.v("wy", "save info ="+DataTool.getUserInfo());
					Toast.makeText(RegisterActivity.this, "注册成功",
							Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent();
					myIntent = new Intent(RegisterActivity.this,
							UserInfoActivity.class);
					startActivity(myIntent);
					RegisterActivity.this.finish();

				} else if (rtCode == -1) {
					Toast.makeText(RegisterActivity.this, "手机号码已被注册",
							Toast.LENGTH_LONG).show();
				} else if (rtCode == -2) {
					Toast.makeText(RegisterActivity.this, "验证码错误",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}

			}else {
				Toast.makeText(RegisterActivity.this, "网络异常，请稍后重试", Toast.LENGTH_LONG).show();
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

			verifyCodeJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.vcode, "phone=" + phoneNumStr);
			if (verifyCodeJson!=null&&!"".equals(verifyCodeJson)) {
				return true;
			}else {
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
					Toast.makeText(RegisterActivity.this, "验证码获取成功",Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(RegisterActivity.this, "验证码获取失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}
			}else {
				Toast.makeText(RegisterActivity.this, "网络异常，请稍后重试", Toast.LENGTH_LONG).show();
			}
		}
	}

}
