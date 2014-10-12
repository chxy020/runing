package net.yaopao.activity;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportParaBean;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

/**
 * 显示起始画面
 */
public class SplashActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig( this );
	
			//测试代码
			Constants.endpoints=Constants.endpoints1;
			Constants.endpoints_img=Constants.endpoints2;
			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			Toast.makeText(this, "Max memory is " + maxMemory + "KB", Toast.LENGTH_LONG).show();
			//测试代码
			
//			Constants.endpoints = MobclickAgent.getConfigParams( this, "mainurl" );
//			Constants.endpoints_img = MobclickAgent.getConfigParams( this, "imgurl" );
//			Log.v("wyuser", "在线参数1="+Constants.endpoints);
//			Log.v("wyuser", "在线参数2="+Constants.endpoints_img);
//			if ("".equals(Constants.endpoints)||Constants.endpoints==null) {
//				Constants.endpoints=Constants.endpoints1;
//				Constants.endpoints_img=Constants.endpoints2;
//			}else{
//				Constants.endpoints+="chSports";
//			}

		
		Log.v("wyuser", "Constants.endpoints="+Constants.endpoints);
		Log.v("wyuser", "Constants.endpoints_img="+Constants.endpoints_img);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {
//				Intent mainIntent = new Intent(SplashActivity.this,
//						MainActivity.class);
				
				Intent mainIntent = new Intent(SplashActivity.this,
						MatchGiveRelayActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				
				SplashActivity.this.finish();
			}
		}, Constants.SPLASH_DISPLAY_LENGHT);
		// 利用开头动画这几秒时间可以初始化变量和自动登录
		Variables.uid = DataTool.getUid();
		
		if (NetworkHandler.isNetworkAvailable(this)) {
			Variables.network = 1;
			if (Variables.uid != 0) {
				new AutoLogin().execute("");
			}

		} else {
			//Toast.makeText(this, "请检查网络", Toast.LENGTH_LONG).show();
		}
		 Log.v("wynet", "Variables.network0000="+Variables.network);
		initSportParam();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("wy", "SplashActivity destroy");
	}


	private class AutoLogin extends AsyncTask<String, Void, Boolean> {
		private String loginJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Log.v("wyuser", "自动登录中");
			loginJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.autoLogin, "uid="+Variables.uid);
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
					// 登录成功，初始化用户信息,比赛信息
					Variables.islogin = 1;
					CNAppDelegate.match_isLogin = 1;
					Variables.userinfo =  rt.getJSONObject("userinfo");
					Variables.matchinfo =  rt.getJSONObject("match");
					// 下载头像
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
					
					
					break;
				case -7:
					//设备已在其他设备登陆
					Variables.islogin = 3;
					DataTool.setUid(0);
					break;
				default:
					break;
				}

			} else {
				// Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试",
				// Toast.LENGTH_LONG).show();
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
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	/**
	 * 初始化运动参数
	 * 
	 */
	private void initSportParam() {
		SportParaBean param = YaoPao01App.db.querySportParam(Variables.uid);
		if (param.getTargetdis()!=0) {
			Variables.runtarDis=param.getTargetdis();
		}
		if (param.getTargettime()!=0) {
			Variables.runtarTime=param.getTargettime();
		}
		if (param.getTypeIndex()!=0) {
			Variables.runty=param.getTypeIndex();
		}
		Variables.switchTime=param.getCountDown();
		Variables.switchVoice=param.getVioce();
		Variables.runtar =param.getTargetIndex();
		
	}
}
