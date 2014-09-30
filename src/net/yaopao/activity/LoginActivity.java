package net.yaopao.activity;

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
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements OnTouchListener {
	private TextView to_reset;
	private TextView login;
	private TextView goBack;
	private TextView serviceV;
	private ImageView serviceSelectV;
	private EditText phoneNumV;
	private EditText pwdV;

	private String phoneNumStr;
	private String pwdStr;
	private String loginJson;
	private LoadingDialog dialog;
	private int loginStatus;
	private boolean service=true;//是否同意服务条款
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		IntentFilter filter = new IntentFilter(ResetPwdActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);
		if (Variables.islogin==3) {
			DialogTool dialog = new DialogTool(this,null);
			WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			dialog.alertLoginOnOther(d);
		}
		initLayout();
	}

	private void initLayout() {
		goBack = (TextView) this.findViewById(R.id.login_goback);
		login = (TextView) this.findViewById(R.id.login_go);
		to_reset = (TextView) this.findViewById(R.id.to_reset);
		to_reset.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		phoneNumV = (EditText) this.findViewById(R.id.login_phoneNum);
		phoneNumV.setInputType(InputType.TYPE_CLASS_NUMBER);
		pwdV = (EditText) this.findViewById(R.id.login_pwd);
		serviceV = (TextView) this.findViewById(R.id.term_of_service);
		serviceV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		serviceSelectV = (ImageView) this.findViewById(R.id.term_of_service_select);
		dialog = new LoadingDialog(this);
		// dialog.setCanceledOnTouchOutside(false);
		goBack.setOnTouchListener(this);
		to_reset.setOnTouchListener(this);
		login.setOnTouchListener(this);
		serviceV.setOnTouchListener(this);
		serviceSelectV.setOnTouchListener(this);
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
				if (service) {
					if (verifyParam()) {
						dialog.show();
						new loginAsyncTask().execute("");
					}
					}else{
						Toast.makeText(LoginActivity.this, "您需要同意要跑服务协议才能进行后续操作",Toast.LENGTH_LONG).show();
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
			loginJson = NetworkHandler.httpPost(Constants.endpoints1
					+ Constants.login, "phone=" + phoneNumStr + "&passwd="
					+ pwdStr);
			Log.v("wyuser", "登录请求参数==" + Constants.endpoints	+ Constants.login+" phone=" + phoneNumStr + "&passwd="+ pwdStr);
			Log.v("wyuser", "登录请求返回loginJson=" + loginJson);
			Log.e("", "chxy loginJson=" + loginJson);
			if (loginJson != null && !"".equals(loginJson)) {

				JSONObject rt = JSON.parseObject(loginJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				switch (rtCode) {
				case 0:
					//登录成功之后保存跳转web页面需要的参数 chenxy add
					saveLoginStatus(rt);
					//chenxy end
					
					Variables.islogin = 1;
					Variables.uid = rt.getJSONObject("userinfo").getInteger(
							"uid");
					Variables.utype = rt.getJSONObject("userinfo").getInteger(
							"utype");
					// 下载头像
					Variables.headUrl  = Constants.endpoints_img + rt.getJSONObject("userinfo").getString("imgpath");
					try {
						if (rt.getJSONObject("userinfo").getString("imgpath")!=null) {
							Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
						}
					} catch (Exception e) {
						Log.v("wyuser", "下载头像异常="+e.toString());
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
	
	
	/****************** chenxy add ****************/
	private void saveLoginStatus(JSONObject rt){
		//保存其他登录数据chenxy add
		/*
		{"announcement":{"isann":"0"},"integral":{"integral":"300"},
		"match":{"gid":1,"groupname":"AAA","isbaton":"1","isgroup":"1",
		"isleader":"1","ismatch":"1","issign":"1","mid":1},
		"state":{"code":0,"desc":"请求成功"},
		"userinfo":{"gender":"F","imgpath":"","nickname":"13122233303",
			"phone":"13122233303","uid":3,"utype":1}}
		*/
		//是否报名
		Variables.bid = rt.getJSONObject("match").getString("issign");
		
		Variables.gid = "";
		if(null != rt.getJSONObject("match").getInteger("gid")){
			//组ID
			Variables.gid = rt.getJSONObject("match").getInteger("gid").toString();
		}
		//用户名称
		//Variables.userName = rt.getJSONObject("userinfo").getString("username");
		//昵称
		Variables.nikeName = rt.getJSONObject("userinfo").getString("nickname");
		//头像URL
		Variables.photoUrl = rt.getJSONObject("userinfo").getString("imgpath");
		//组名
		Variables.groupName = rt.getJSONObject("match").getString("groupname");
		//是否领队,"1"/"0"
		Variables.isLeader = rt.getJSONObject("match").getString("isleader");
		//是否第一棒,"1"/"0"
		Variables.isBaton = rt.getJSONObject("match").getString("isbaton");
		//比赛状态
		Variables.matchState = rt.getJSONObject("match").getString("ismatch");
	}
}
