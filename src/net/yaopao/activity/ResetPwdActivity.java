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
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CommonDialog;
import cn.smssdk.gui.CountryPage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class ResetPwdActivity extends BaseActivity implements OnTouchListener {
//	public static final String closeAction = "resetpwd_close.action";
	public TextView reset;
	public TextView goBack;
	public TextView getCodeV;
	
	public EditText codeV;
	public EditText phoneNumV;
	public EditText pwdV;
	
	public TextView setCountryV;
	public TextView countryCodeV;
	private String currentCode;//当前国家或区域区号
	private String currentId;//当前国家或区域id
	private String currentCountry;
	private boolean isVerified=false;
	// 国家号码规则
	private HashMap<String, String> countryRules;
//	private Dialog pd;
	private LoadingDialog dialog;
	public  Handler updateDataHandler;
	public  EventHandler eh;
	
	public String phoneNumStr;
	public String pwdStr;
	public String resetJson;
	public String codeStr;
	public String verifyCodeJson;
	
	private String code;// 验证码
	private Timer timer;// 计时器
	private int time = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset);
		initSMS();
		initLayout();
	}

	private void initSMS() {
		SMSSDK.initSDK(this, Constants.SMS_KEY,Constants.SMS_SECRET);
		currentCode = Constants.SMS_CN_CODE;
		currentCountry = Constants.SMS_DEF_CONUTRY;
		currentId=Constants.SMS_CN_ID;
		updateDataHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					String [] data = (String[]) msg.obj;
					
					currentCode=data[0];
					currentCountry=data[1];
					currentId=data[2];
					setCountryV.setText(currentCountry);
					countryCodeV.setText("+"+currentCode);
				} 
				super.handleMessage(msg);
			}
		};
		 eh=new EventHandler(){
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
		
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.reset_goback);
		reset = (TextView) this.findViewById(R.id.reset_go);
		phoneNumV = (EditText) this.findViewById(R.id.reset_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		pwdV = (EditText) this.findViewById(R.id.reset_pwd);
		getCodeV = (TextView) this.findViewById(R.id.reset_get_code);
		
		setCountryV =(TextView) this.findViewById(R.id.reset_country);
		countryCodeV =(TextView) this.findViewById(R.id.reset_country_num);
		setCountryV.setText(currentCountry);
		countryCodeV.setText("+"+currentCode);
//		pd = CommonDialog.ProgressDialog(ResetPwdActivity.this);
		
		dialog = new LoadingDialog(this);
		
		codeV = (EditText) this.findViewById(R.id.reset_veri_code);
		codeV.setInputType(InputType.TYPE_CLASS_NUMBER);
		goBack.setOnTouchListener(this);
		reset.setOnTouchListener(this);
		getCodeV.setOnTouchListener(this);
		setCountryV.setOnTouchListener(this);
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
				Intent intent = new Intent(ResetPwdActivity.this,LoginActivity.class);
				startActivity(intent);
				ResetPwdActivity.this.finish();
				break;
			}
			break;
		case R.id.reset_country:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				setCountryV.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				setCountryV.setBackgroundResource(R.color.white);
//				SMSSDK.getSupportedCountries();
				CountryActivity ca = new CountryActivity();
				ca.country=currentCountry;
				ca.code=currentCode;
				ca.handler=updateDataHandler;
				//国家列表
				CountryPage countryPage = new CountryPage();
				countryPage.setCountryId(currentId);
				countryPage.setCountryRuls(countryRules);
				countryPage.showForResult(this, null, ca);
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
					if (isVerified) {
						new resetAsyncTask().execute("");
					}else{
						if (!TextUtils.isEmpty(codeV.getText().toString())) {
							SMSSDK.submitVerificationCode(currentCode, phoneNumV
									.getText().toString(), codeV.getText()
									.toString());
							if (dialog != null && !dialog.isShowing()) {
								dialog.show();
							}
						} else {
							Toast.makeText(this, "验证码不能为空", 1).show();
						}
					}
				

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
//				Log.v("wy", "点击了获取验证码按钮");
//				if (verifyPhone()) {
//					new verifyCodAsyncTask().execute("");
//				}
				
				//请求发送短信验证码
				if(!TextUtils.isEmpty(phoneNumV.getText().toString())){
					SMSSDK.getVerificationCode(currentCode,phoneNumV.getText().toString());
					phoneNumStr=phoneNumV.getText().toString();
					
					if (dialog != null && !dialog.isShowing()) {
						dialog.show();
					}
					

				}else {
					Toast.makeText(this, "电话不能为空", 1).show();
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

//	public boolean verifyPhone() {
//		phoneNumStr = phoneNumV.getText().toString().trim();
//		Log.v("wy", "phone=" + phoneNumStr);
//		if (phoneNumStr != null && !"".equals(phoneNumStr)) {
//			Pattern p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
//			Matcher m = p.matcher(phoneNumStr);
//			if (m.matches()) {
//				return true;
//			} else {
//				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
//				return false;
//			}
//
//		} else {
//			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
//			return false;
//		}
//	}
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
						+ "&passwd=" + pwdStr + "&vcode=" + codeStr+"&country="+ currentCountry);
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
					Toast.makeText(ResetPwdActivity.this, "重置密码成功",
							Toast.LENGTH_LONG).show();
					Variables.uid = rt.getJSONObject("userinfo").getInteger("uid");
					DataTool.setUid(Variables.uid);
					Variables.islogin = 1;
					CNAppDelegate.match_isLogin = 1;
					
					
					Variables.userinfo =  rt.getJSONObject("userinfo");
					Variables.matchinfo =  rt.getJSONObject("match");
					
					// 下载头像
					Variables.headUrl  = Constants.endpoints_img + rt.getJSONObject("userinfo").getString("imgpath");
					try {
						if (Variables.userinfo.getString("imgpath")!=null) {
							Variables.headUrl  = Constants.endpoints_img +Variables.userinfo .getString("imgpath");
							Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
						}
					} catch (Exception e) {
						Log.v("wyuser", "下载头像异常="+e.toString());
						e.printStackTrace();
					}
					//登陆成功判断比赛信息
					JSONObject dic = rt.getJSONObject("match");
					if(dic != null){
						CNAppDelegate.matchDic = dic;
		                //比赛信息就不保存到本地了，因为认为比赛前必须要经历登录或者手动登录这个过程
						CNAppDelegate.uid = Variables.userinfo.getString("uid");
						CNAppDelegate.gid = dic.getString("gid");
						CNAppDelegate.mid = dic.getString("mid");
						CNAppDelegate.isMatch = dic.getIntValue("ismatch");
						CNAppDelegate.isbaton = dic.getIntValue("isbaton");
						CNAppDelegate.gstate = dic.getIntValue("gstate");
						CNAppDelegate.loginSucceedAndNext = true;
		            }
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
//		private void initUserInfo(JSONObject rt) {
//			JSONObject userInfo= rt.getJSONObject("userinfo");
//			JSONObject match= rt.getJSONObject("match");
//			Variables.islogin = 1;
//			Variables.uid =userInfo.getInteger("uid");
//			Variables.utype = userInfo.getInteger("utype");
//			Variables.userName = userInfo.getString("uname")!=null?userInfo.getString("uname"):"";
//			Variables.nikeName = userInfo.getString("nickname")!=null?userInfo.getString("nicknames"):"";
//			Variables.headPath=userInfo.getString("imgpath");
//			
//			if (Variables.headPath != null	&& !"".equals(Variables.headPath)) {
//				// 下载头像
//				Variables.headUrl = Constants.endpoints_img +Variables.headPath;
//				Log.v("wyuser", "头像======="+Variables.headUrl);
//				try {
//						Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
//				} catch (Exception e) {
//					Log.v("wyuser", "下载头像异常="+e.toString());
//					e.printStackTrace();
//				}
//			}
//			DataTool.setUserInfo(resetJson);
//			Log.v("wyuser", "loginJson = " + resetJson);
//			//是否有比赛
//			if ("1".equals(match.getString("ismatch"))) {
//				Variables.mid=match.getInteger("mid");
//			}
//			//是否报名
//			if ("1".equals(match.getString("issign"))) {
//				Variables.isSigned=true;
//			}
//			//是否组队
//			if ("1".equals(match.getString("isgroup"))) {
//				Variables.gid=match.getString("gid");
//			}
//			//是否队长
//			Variables.isLeader=match.getString("isleader");
//			//是否是头棒
//			if ("4".equals(match.getString("isgroup"))) {
//				Variables.isBaton="1";
//			}
//		}
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
				/*
				 * 0.9.1 代码
				 * 
				 * Log.v("wy", verifyCodeJson);
				JSONObject rt = JSON.parseObject(verifyCodeJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				if (rtCode == 0) {
					Toast.makeText(ResetPwdActivity.this, "验证码获取成功",
							Toast.LENGTH_LONG).show();
				} else if (rtCode == -3) {
					Toast.makeText(ResetPwdActivity.this, "手机号不存在，请重新输入",
							Toast.LENGTH_LONG).show();
				} else {
					Log.v("wyuser", "重置密码验证码获取返回=="+rt);
					Toast.makeText(ResetPwdActivity.this, "验证码获取失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}*/
				
				JSONObject rt=null;
				int rtCode =-999;
				String desc ="";
				try {
					 rt = JSON.parseObject(verifyCodeJson);
					 rtCode = rt.getJSONObject("state").getInteger("code");
					 desc= rt.getJSONObject("state").getString("desc");
					 
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (rtCode == 0) {
					Toast.makeText(ResetPwdActivity.this, desc,
							Toast.LENGTH_LONG).show();
				} else if (rtCode == -3) {
					Toast.makeText(ResetPwdActivity.this, desc,
							Toast.LENGTH_LONG).show();
				}else if (rtCode == -13) {
					Toast.makeText(ResetPwdActivity.this, desc,
							Toast.LENGTH_LONG).show();
				} else if (rtCode == -14) {
					Toast.makeText(ResetPwdActivity.this, desc,
							Toast.LENGTH_LONG).show();
				} else {
					Log.v("wyuser", "重置密码验证码获取返回=="+rt);
					Toast.makeText(ResetPwdActivity.this, "验证码获取失败，请稍后重试",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ResetPwdActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
		Intent myIntent = new Intent();
		myIntent = new Intent(ResetPwdActivity.this,LoginActivity.class);
		startActivity(myIntent);
		ResetPwdActivity.this.finish();
		}
		return false;
	}
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
//					Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
					isVerified=true;
					DataTool.setIsPhoneVerfied(1);
					
					new resetAsyncTask().execute("");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					isVerified=false;
					startVerifyTimer();
					Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				// 根据服务器返回的网络错误，给toast提示
				try {
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;

					JSONObject object = JSONObject.parseObject(throwable.getMessage());
					String des = object.getString("detail");
					if (!TextUtils.isEmpty(des)) {
						Toast.makeText(ResetPwdActivity.this, des, Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
	};
	Handler timerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				getCodeV.setEnabled(true);
				getCodeV.setText("获取验证码");
				timer.cancel();
			} else {
				getCodeV.setText(msg.what + "秒");
			}
		};
	};
	
	private void startVerifyTimer(){
		time = 60;
		getCodeV.setEnabled(false);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timerHandler.sendEmptyMessage(time--);
			}
		}, 0, 1000);
	}
}
