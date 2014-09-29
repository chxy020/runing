package net.yaopao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class RegisterActivity extends BaseActivity implements OnTouchListener {
	public TextView reg;
	public TextView getCodeV;
	public TextView serviceV;
	public ImageView serviceSelectV;
	
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
	public boolean service=true;//是否同意服务条款

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		IntentFilter filter = new IntentFilter(ResetPwdActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);
		initLayout();
	}
	
	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.reg_goback);
		toLogin = (TextView) this.findViewById(R.id.to_login);
		getCodeV = (TextView) this.findViewById(R.id.reg_get_code);
		reg = (TextView) this.findViewById(R.id.reg_go);
		toLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		phoneNumV = (EditText) this.findViewById(R.id.reg_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		pwdV = (EditText) this.findViewById(R.id.reg_pwd);
		codeV = (EditText) this.findViewById(R.id.reg_veri_code);
		codeV.setInputType(InputType.TYPE_CLASS_NUMBER);
		serviceV = (TextView) this.findViewById(R.id.term_of_service);
		serviceV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		serviceSelectV = (ImageView) this.findViewById(R.id.term_of_service_select);

		getCodeV.setOnTouchListener(this);
		goBack.setOnTouchListener(this);
		toLogin.setOnTouchListener(this);
		reg.setOnTouchListener(this);
		serviceV.setOnTouchListener(this);
		serviceSelectV.setOnTouchListener(this);

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
				goBack.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				goBack.setBackgroundResource(R.color.red);
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
				startActivityForResult(myIntent, 0);
				break;
			}
			break;
		case R.id.reg_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				reg.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				reg.setBackgroundResource(R.color.blue_dark);
				if (service) {
				if (verifyParam()) {
					new regAsyncTask().execute("");
				}
				}else{
					Toast.makeText(RegisterActivity.this, "您需要同意要跑服务协议才能进行后续操作",Toast.LENGTH_LONG).show();
				}
				break;
			}
			break;
		case R.id.reg_get_code:
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
		case R.id.term_of_service_select:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (service) {
					serviceSelectV.setBackgroundResource(R.drawable.service_uncheck);
					service=false;
				}else {
					serviceSelectV.setBackgroundResource(R.drawable.service_checked);
					service=true;
				}
				break;
			}
			break;
		case R.id.term_of_service:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(RegisterActivity.this,ServiceActivity.class);
				startActivity(intent);
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
					+ Constants.reg, "phone=" + phoneNumStr + "&passwd="
					+ pwdStr + "&vcode=" + codeStr);
			if (regJson != null && !"".equals(regJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.v("wy", regJson);
				JSONObject rt = JSON.parseObject(regJson);
				int uid = 0;
				int rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Variables.uid = rt.getJSONObject("userinfo").getInteger("uid");
					Variables.utype = rt.getJSONObject("userinfo").getInteger("utype");
					Variables.islogin = 1;
					DataTool.setUserInfo(regJson);
					Log.v("wy", "save info =" + regJson);
					Log.v("wy", "save info =" + DataTool.getUserInfo());
					Toast.makeText(RegisterActivity.this, "注册成功",
							Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent();
					myIntent = new Intent(RegisterActivity.this,
							UserInfoActivity.class);
					Variables.toUserInfo=0;
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

			} else {
				Toast.makeText(RegisterActivity.this, "网络异常，请稍后重试",
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

			verifyCodeJson = NetworkHandler.httpPost(Constants.endpoints+ Constants.vcode, "phone=" + phoneNumStr);
			Log.v("wyuser","Constants.endpoints+ Constants.vcode = "+"&phone=" + phoneNumStr );
			Log.v("wyuser","verifyCodeJson = "+verifyCodeJson );
			if (verifyCodeJson != null && !"".equals(verifyCodeJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				JSONObject rt = JSON.parseObject(verifyCodeJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Toast.makeText(RegisterActivity.this, "验证码已发送，请查收短信",
							Toast.LENGTH_LONG).show();
				}else if(rtCode == -1){
					Toast.makeText(RegisterActivity.this, "手机号码已经注册过，请直接登录",
							Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(RegisterActivity.this, "验证码获取失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(RegisterActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==Activity.RESULT_OK) {
			RegisterActivity.this.finish();
		}
	}
	// 如果重置密码成功，关闭此页面
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if ("close".equals(intent.getExtras().getString("data"))) {
					RegisterActivity.this.finish();
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
