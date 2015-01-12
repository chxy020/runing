package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.sms.CountryActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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

@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity implements OnTouchListener {
	private TextView to_reg;
	private TextView to_reset;
	private TextView login;
	private TextView goBack;
	private TextView serviceV;
	private RelativeLayout countryL;
	private ImageView serviceSelectV;
	private EditText phoneNumV;
	private EditText pwdV;

	private String phoneNumStr;
	private String pwdStr;
	private String loginJson;
	private LoadingDialog dialog;
	private boolean service=true;//是否同意服务条款
	
	public TextView getCodeV;
	public EditText codeV;
	public String codeStr;
	public TextView setCountryV;
	public TextView countryCodeV;
	private String currentCode;//当前国家或区域区号
	private String currentId;//当前国家或区域id
	private String currentCountry;
	private boolean isVerified=false;
	// 国家号码规则
	private HashMap<String, String> countryRules;
	public  Handler updateDataHandler;
	public  EventHandler eh;
	private boolean ready;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		registerReceiver(leftTimeReceiver, new IntentFilter(YaoPao01App.verifyCode));
		if (Variables.islogin==3) {
			DialogTool dialog = new DialogTool(this);
			dialog.alertLoginOnOther();
		}
		initSMS();
		initLayout();
		
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.login_goback);
		login = (TextView) this.findViewById(R.id.login_go);
		to_reset = (TextView) this.findViewById(R.id.to_reset);
		to_reg = (TextView) this.findViewById(R.id.to_reg);
		phoneNumV = (EditText) this.findViewById(R.id.login_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		pwdV = (EditText) this.findViewById(R.id.login_pwd);
		serviceV = (TextView) this.findViewById(R.id.term_of_service);
		serviceV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		serviceSelectV = (ImageView) this.findViewById(R.id.term_of_service_select);
		
		getCodeV = (TextView) this.findViewById(R.id.login_get_code);
		if (Variables.verifyTime<60) {
			getCodeV.setEnabled(false);
		}
		setCountryV =(TextView) this.findViewById(R.id.login_country);
		countryCodeV =(TextView) this.findViewById(R.id.login_country_num);
		setCountryV.setText(currentCountry);
		countryCodeV.setText("+"+currentCode);
		countryL=(RelativeLayout) this.findViewById(R.id.country_layout);
		
		codeV = (EditText) this.findViewById(R.id.login_veri_code);
		codeV.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		getCodeV.setOnTouchListener(this);
		setCountryV.setOnTouchListener(this);
		
		dialog = new LoadingDialog(this);
		goBack.setOnTouchListener(this);
		to_reset.setOnTouchListener(this);
		to_reg.setOnTouchListener(this);
		login.setOnTouchListener(this);
		serviceV.setOnTouchListener(this);
		serviceSelectV.setOnTouchListener(this);
		
		
	}
	private void initSMS() {
		
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
		ready=true;
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
		case R.id.login_country:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				countryL.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				countryL.setBackgroundResource(R.color.white);
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
		case R.id.to_reset:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				to_reset.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				to_reset.setBackgroundResource(R.color.red);
				Intent myIntent = new Intent();
				myIntent = new Intent(LoginActivity.this,
						ResetPwdActivity.class);
				startActivity(myIntent);
				LoginActivity.this.finish();
				break;
			}
			break;
		case R.id.to_reg:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				to_reg.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				to_reg.setBackgroundResource(R.color.red);
				Intent myIntent = new Intent();
				myIntent = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(myIntent);
				LoginActivity.this.finish();
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
				if (service) {
					if (verifyParam()) {

						if (isVerified) {
							if (dialog != null && !dialog.isShowing()) {
								dialog.show();
							}
							new loginAsyncTask().execute("");
						} else {
							if (!TextUtils.isEmpty(codeV.getText().toString())) {
								SMSSDK.submitVerificationCode(currentCode,
										phoneNumV.getText().toString(), codeV
												.getText().toString());
								if (dialog != null && !dialog.isShowing()) {
									dialog.show();
								}
							} else {
								Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_LONG).show();
							}
						}
					}
//					new loginAsyncTask().execute("");
				} else {
					Toast.makeText(LoginActivity.this, "您需要同意要跑服务协议才能进行后续操作",
							Toast.LENGTH_LONG).show();
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
				Intent intent = new Intent(LoginActivity.this,ServiceActivity.class);
				startActivity(intent);
				break;
			}
			break;
		case R.id.login_get_code:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				getCodeV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				getCodeV.setBackgroundResource(R.color.blue_dark);
				
				//请求发送短信验证码
				if(!TextUtils.isEmpty(phoneNumV.getText().toString())){
					SMSSDK.getVerificationCode(currentCode,phoneNumV.getText().toString());
					phoneNumStr=phoneNumV.getText().toString();
					
					if (dialog != null && !dialog.isShowing()) {
						dialog.show();
					}

				}else {
					Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				}
//				getCodeV.setEnabled(false);
//				YaoPao01App.instance.sendTimerBroadcast();
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

	private class loginAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(String... params) {
			pwdStr = pwdV.getText().toString().trim();
			phoneNumStr = phoneNumV.getText().toString().trim();
			loginJson = NetworkHandler.httpPost(Constants.endpoints1+ Constants.login, "phone=" + phoneNumStr + "&passwd="	+ pwdStr+ "&country=" + currentCountry);
			Log.v("wyuser", "登录请求参数==" + Constants.endpoints	+ Constants.login+" phone=" + phoneNumStr + "&passwd="+ pwdStr+ "&country=" + currentCountry);
			Log.v("wyuser", "登录请求返回loginJson=" + loginJson);
			Log.e("", "chxy loginJson=" + loginJson);
			if (loginJson != null && !"".equals(loginJson)) {
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
				
				JSONObject rt = JSON.parseObject(loginJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				
				if (rtCode==0) {
					//登录成功之后保存跳转web页面需要的参数 chenxy add
				//	saveLoginStatus(rt);
					//chenxy end
					
					Variables.islogin = 1;
					CNAppDelegate.match_isLogin = 1;
					Variables.uid = rt.getJSONObject("userinfo").getInteger("uid");
					DataTool.setUid(Variables.uid);
					// 登录成功，初始化用户信息,比赛信息
					Variables.userinfo =  rt.getJSONObject("userinfo");
					Variables.matchinfo =  rt.getJSONObject("match");
					// 下载头像
					try {
						if (Variables.userinfo.getString("imgpath")!=null) {
							Variables.headUrl  = Constants.endpoints_img +Variables.userinfo.getString("imgpath");
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
						CNAppDelegate.uid = Variables.userinfo.getString("uid");
						CNAppDelegate.gid = dic.getString("gid");
						CNAppDelegate.mid = dic.getString("mid");
						CNAppDelegate.isMatch = dic.getIntValue("ismatch");
						CNAppDelegate.isbaton = dic.getIntValue("isbaton");
						CNAppDelegate.gstate = dic.getIntValue("gstate");
						CNAppDelegate.loginSucceedAndNext = true;
		            }
					setResult(Activity.RESULT_OK);
					LoginActivity.this.finish();
				}else {
					Toast.makeText(LoginActivity.this,  rt.getJSONObject("state").getString("desc"),	Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(LoginActivity.this, "登录失败，请稍后重试",
						Toast.LENGTH_LONG).show();
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
	protected void onDestroy() {
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterEventHandler(eh);
		}
		super.onDestroy();
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					
					isVerified=true;
					DataTool.setIsPhoneVerfied(1);
					new loginAsyncTask().execute("");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					isVerified=false;
//					startVerifyTimer();
					getCodeV.setEnabled(false);
					YaoPao01App.instance.sendTimerBroadcast();
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
						Toast.makeText(LoginActivity.this, des, Toast.LENGTH_SHORT).show();
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
