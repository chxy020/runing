package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.sms.CountryActivity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CommonDialog;
import cn.smssdk.gui.CountryPage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class VerifyPhoneActivity extends BaseActivity implements
		OnTouchListener {
	public TextView goBack;
	public TextView getCodeV;
	public TextView verifyV;

	public EditText codeV;
	public EditText phoneNumV;
	private RelativeLayout countryL;
	public TextView setCountryV;
	public TextView countryCodeV;
	private String currentCode;// 当前国家或区域区号
	private String currentId;// 当前国家或区域id
	private String currentCountry;
	// 国家号码规则
	private HashMap<String, String> countryRules;
	// private Dialog pd;
	private LoadingDialog dialog;
	public Handler updateDataHandler;
	public EventHandler eh;

	public String phoneNumStr;
	public String codeStr;
	private boolean ready;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_phone);

		registerReceiver(leftTimeReceiver, new IntentFilter(
				YaoPao01App.verifyCode));

		initSMS();
		initLayout();
	}

	private void initSMS() {

		currentCode = Constants.SMS_CN_CODE;
		currentCountry = Constants.SMS_DEF_CONUTRY;
		currentId = Constants.SMS_CN_ID;
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
		goBack = (TextView) this.findViewById(R.id.verify_goback);
		phoneNumV = (EditText) this.findViewById(R.id.verify_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		getCodeV = (TextView) this.findViewById(R.id.verify_get_code);
		if (Variables.verifyTime < 60) {
			getCodeV.setEnabled(false);
		}
		verifyV = (TextView) this.findViewById(R.id.verify_go);

		setCountryV = (TextView) this.findViewById(R.id.verify_country);
		countryCodeV = (TextView) this.findViewById(R.id.verify_country_num);
		setCountryV.setText(currentCountry);
		countryCodeV.setText("+" + currentCode);
		countryL = (RelativeLayout) this.findViewById(R.id.country_layout);
		dialog = new LoadingDialog(this);

		codeV = (EditText) this.findViewById(R.id.verify_veri_code);
		codeV.setInputType(InputType.TYPE_CLASS_NUMBER);
		goBack.setOnTouchListener(this);
		setCountryV.setOnTouchListener(this);
		getCodeV.setOnTouchListener(this);
		verifyV.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.verify_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				goBack.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				goBack.setBackgroundResource(R.color.red);
				Variables.uid = 0;
				DataTool.setUid(0);
				Intent intent = new Intent(VerifyPhoneActivity.this,
						MainActivity.class);
				startActivity(intent);
				VerifyPhoneActivity.this.finish();
				break;
			}
			break;
		case R.id.verify_country:
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
		case R.id.verify_go:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				verifyV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				verifyV.setBackgroundResource(R.color.blue_dark);
				if (verifyPhone()) {
					if (!TextUtils.isEmpty(codeV.getText().toString())) {
						SMSSDK.submitVerificationCode(currentCode, phoneNumV
								.getText().toString(), codeV.getText()
								.toString());
						if (dialog != null && !dialog.isShowing()) {
							dialog.show();
						}
					} else {
						Toast.makeText(this, "请输入正确的验证码", 1).show();
					}
				}
				// if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
				// }
				// DataTool.setIsPhoneVerfied(1);
				// new AutoLogin().execute("");
				// Intent mainIntent = new Intent(VerifyPhoneActivity.this,
				// MainActivity.class);
				// VerifyPhoneActivity.this.startActivity(mainIntent);
				// VerifyPhoneActivity.this.finish();
			}
			break;

		case R.id.verify_get_code:
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
				// getCodeV.setEnabled(false);
				// YaoPao01App.instance.sendTimerBroadcast();
				break;
			}
			break;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Variables.uid = 0;
			DataTool.setUid(0);
			Intent myIntent = new Intent();
			myIntent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
			startActivity(myIntent);
			VerifyPhoneActivity.this.finish();
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
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					DataTool.setIsPhoneVerfied(1);
					new AutoLogin().execute("");
					Intent mainIntent = new Intent(VerifyPhoneActivity.this,
							MainActivity.class);
					VerifyPhoneActivity.this.startActivity(mainIntent);
					VerifyPhoneActivity.this.finish();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					getCodeV.setEnabled(false);
					YaoPao01App.instance.sendTimerBroadcast();
					Toast.makeText(getApplicationContext(), "验证码已经发送",
							Toast.LENGTH_SHORT).show();
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
						Toast.makeText(VerifyPhoneActivity.this, des,
								Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	};

	private class AutoLogin extends AsyncTask<String, Void, Boolean> {
		private String loginJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Variables.islogin = 2;
			Log.v("wyuser", "自动登录中");

			loginJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.autoLogin, "uid=" + Variables.uid);
			Log.v("zc", "自动登陆返回" + loginJson);
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
				Log.v("wypho", "rtCode=" + rtCode);
				switch (rtCode) {
				case 0:
					// 登录成功，通知主界面取消等待动画
					if (MainActivity.loginHandler != null) {
						MainActivity.loginHandler.obtainMessage(1)
								.sendToTarget();
					}

					// 登录成功，初始化用户信息,比赛信息

					Variables.islogin = 1;
					CNAppDelegate.match_isLogin = 1;
					Variables.userinfo = rt.getJSONObject("userinfo");
					Variables.matchinfo = rt.getJSONObject("match");
					// 下载头像
					try {
						if (Variables.userinfo.getString("imgpath") != null) {
							Variables.headUrl = Constants.endpoints_img
									+ Variables.userinfo.getString("imgpath");
							Variables.avatar = BitmapFactory
									.decodeStream(getImageStream(Variables.headUrl));
						}
					} catch (Exception e) {
						Log.v("wyuser", "下载头像异常=" + e.toString());
						e.printStackTrace();
					}
					// 登陆成功判断比赛信息
					JSONObject dic = rt.getJSONObject("match");
					if (dic != null) {
						CNAppDelegate.matchDic = dic;
						// 比赛信息就不保存到本地了，因为认为比赛前必须要经历登录或者手动登录这个过程
						CNAppDelegate.uid = Variables.userinfo.getString("uid");
						CNAppDelegate.gid = dic.getString("gid");
						CNAppDelegate.mid = dic.getString("mid");
						CNAppDelegate.isMatch = dic.getIntValue("ismatch");
						CNAppDelegate.isbaton = dic.getIntValue("isbaton");
						CNAppDelegate.gstate = dic.getIntValue("gstate");
						CNAppDelegate.loginSucceedAndNext = true;
					}

					break;
				case -7:
					// 设备已在其他设备登陆
					Variables.islogin = 3;
					DataTool.setUid(0);
					break;
				default:
					Variables.islogin = 0;
					break;
				}

			} else {
				Variables.islogin = 0;
				Toast.makeText(YaoPao01App.getAppContext(), "验证失败，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// 接收verifyCode再次发送剩余时间的
	private BroadcastReceiver leftTimeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Variables.verifyTime > 0) {
				getCodeV.setText(Variables.verifyTime + "秒");
			} else {
				Variables.verifyTime = 60;
				getCodeV.setEnabled(true);
				getCodeV.setText("获取验证码");
			}

		}
	};
}
