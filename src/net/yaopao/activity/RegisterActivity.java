package net.yaopao.activity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.sms.CountryActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CountryPage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class RegisterActivity extends BaseActivity implements OnTouchListener {
	public TextView reg;
	public TextView getCodeV;
	public TextView serviceV;
	private RelativeLayout countryL;
	public ImageView serviceSelectV;

	public EditText phoneNumV;
	public EditText pwdV;
	public EditText codeV;
	public TextView goBack;
	public TextView setCountryV;
	public TextView countryCodeV;
	public String phoneNumStr;
	public String pwdStr;
	public String codeStr;
	public String regJson;
	public String verifyCodeJson;
	public boolean service = true;// 是否同意服务条款
	private LoadingDialog dialog;
	
	private String currentCode;// 当前国家或区域区号
	private String currentId;// 当前国家或区域id
	private String currentCountry;
	// 国家号码规则
	private HashMap<String, String> countryRules;
	public Handler updateDataHandler;
	private boolean isVerified = false;
	private boolean ready;
	public  EventHandler eh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		registerReceiver(leftTimeReceiver, new IntentFilter(YaoPao01App.verifyCode));
		
		currentCode = Constants.SMS_CN_CODE;
		currentCountry = Constants.SMS_DEF_CONUTRY;
		currentId = Constants.SMS_CN_ID;

		initLayout();
		
		updateDataHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					String[] data = (String[]) msg.obj;

					currentCode = data[0];
					currentCountry = data[1];
					currentId = data[2];
					setCountryV.setText(currentCountry);
					countryCodeV.setText("+" + currentCode);
				}
				super.handleMessage(msg);
			}
		};
		  
		eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}

		};
		SMSSDK.registerEventHandler(eh);
		ready = true;
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.reg_goback);
		getCodeV = (TextView) this.findViewById(R.id.reg_get_code);
		if (Variables.verifyTime<60) {
			getCodeV.setEnabled(false);
		}
		reg = (TextView) this.findViewById(R.id.reg_go);
		phoneNumV = (EditText) this.findViewById(R.id.reg_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		pwdV = (EditText) this.findViewById(R.id.reg_pwd);
		codeV = (EditText) this.findViewById(R.id.reg_veri_code);
		codeV.setInputType(InputType.TYPE_CLASS_NUMBER);
		serviceV = (TextView) this.findViewById(R.id.term_of_service);
		serviceV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		serviceSelectV = (ImageView) this
				.findViewById(R.id.term_of_service_select);
		setCountryV = (TextView) this.findViewById(R.id.reg_country);
		countryCodeV = (TextView) this.findViewById(R.id.reg_country_num);
		setCountryV.setText(currentCountry);
		countryCodeV.setText("+" + currentCode);
		countryL=(RelativeLayout) this.findViewById(R.id.country_layout);
		dialog = new LoadingDialog(this);
		getCodeV.setOnTouchListener(this);
		goBack.setOnTouchListener(this);
		reg.setOnTouchListener(this);
		serviceV.setOnTouchListener(this);
		serviceSelectV.setOnTouchListener(this);
		setCountryV.setOnTouchListener(this);

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
				return true;
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
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
				break;
			}
			break;

		case R.id.reg_country:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				countryL.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				countryL.setBackgroundResource(R.color.white);
				CountryActivity ca = new CountryActivity();
				ca.country = currentCountry;
				ca.code = currentCode;
				ca.handler = updateDataHandler;

				// 国家列表
				CountryPage countryPage = new CountryPage();
				countryPage.setCountryId(currentId);
				countryPage.setCountryRuls(countryRules);
				countryPage.showForResult(this, null, ca);
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
						if (isVerified) {
							new regAsyncTask().execute("");
						}else {
							if (!TextUtils.isEmpty(codeV.getText().toString())) {
								SMSSDK.submitVerificationCode(currentCode, phoneNumV.getText().toString(), codeV.getText().toString());
								if (dialog != null && !dialog.isShowing()) {
									dialog.show();
								}
							} else {
								Toast.makeText(this, "请输入正确的验证码", 1).show();
							}
						}
					}
//					new regAsyncTask().execute("");
				} else {
					Toast.makeText(RegisterActivity.this,
							"您需要同意要跑服务协议才能进行后续操作", Toast.LENGTH_LONG).show();
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
				// 请求发送短信验证码
				if (!TextUtils.isEmpty(phoneNumV.getText().toString())) {
					SMSSDK.getVerificationCode(currentCode, phoneNumV.getText()
							.toString());
					phoneNumStr = phoneNumV.getText().toString();

					if (dialog != null && !dialog.isShowing()) {
						dialog.show();
					}
				} else {
					Toast.makeText(this, "请输入正确的手机号码", 1).show();
				}
				
//				getCodeV.setEnabled(false);
//				YaoPao01App.instance.sendTimerBroadcast();
				break;
			}
			break;
		case R.id.term_of_service_select:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (service) {
					serviceSelectV
							.setBackgroundResource(R.drawable.service_uncheck);
					service = false;
				} else {
					serviceSelectV
							.setBackgroundResource(R.drawable.service_checked);
					service = true;
				}
				break;
			}
			break;
		case R.id.term_of_service:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(RegisterActivity.this,
						ServiceActivity.class);
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
			regJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.reg, "phone=" + phoneNumStr + "&passwd="
					+ pwdStr + "&country=" + currentCountry);
			Log.v("sms", Constants.endpoints + Constants.reg + "?phone="+ phoneNumStr + "&passwd=" + pwdStr + "&country="
					+ currentCountry);
			Log.v("sms", "regJson=" + regJson);
			if (regJson != null && !"".equals(regJson)) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (result) {
				Log.v("wy", regJson);
				JSONObject rt = JSON.parseObject(regJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Variables.uid = rt.getJSONObject("userinfo").getInteger(
							"uid");
					DataTool.setUid(Variables.uid);
					Variables.islogin = 1;

					Variables.userinfo = rt.getJSONObject("userinfo");
					Variables.matchinfo = rt.getJSONObject("match");

					Log.v("wy", "save info =" + regJson);
					Toast.makeText(RegisterActivity.this, "注册成功",
							Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent();
					myIntent = new Intent(RegisterActivity.this,
							UserInfoActivity.class);
					Variables.toUserInfo = 0;
					startActivity(myIntent);
					RegisterActivity.this.finish();

				}else {
					Toast.makeText(RegisterActivity.this,  rt.getJSONObject("state").getString("desc"),	Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			RegisterActivity.this.finish();
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront = this.getClass().getSimpleName();
		Variables.activityOnFront = this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	protected void onDestroy() {
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterEventHandler(eh);
		}
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent myIntent = new Intent();
			myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(myIntent);
			RegisterActivity.this.finish();
		}
		return false;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event=" + event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					
					isVerified = true;
					DataTool.setIsPhoneVerfied(1);
					new regAsyncTask().execute("");
					
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "验证码已经发送",
							Toast.LENGTH_SHORT).show();
					isVerified = false;
//					startVerifyTimer();
					getCodeV.setEnabled(false);
					YaoPao01App.instance.sendTimerBroadcast();
				}
			} else {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				// 根据服务器返回的网络错误，给toast提示
				try {
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;

					JSONObject object = JSONObject.parseObject(throwable
							.getMessage());
					String des = object.getString("detail");
					if (!TextUtils.isEmpty(des)) {
						Toast.makeText(RegisterActivity.this, des,
								Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	};

	
	//接收verifyCode再次发送剩余时间的
    private BroadcastReceiver leftTimeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Variables.verifyTime>0) {
				getCodeV.setText(Variables.verifyTime+ "秒");
			}else {
				Variables.verifyTime = 60;
				getCodeV.setEnabled(true);
				getCodeV.setText("获取验证码");
			}
			
		}
	};	
}
