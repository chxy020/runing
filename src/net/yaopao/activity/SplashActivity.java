package net.yaopao.activity;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportParaBean;
import net.yaopao.engine.manager.CNCloudRecord;
import net.yaopao.engine.manager.RunManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 显示起始画面
 */
public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
	
			//测试代码
			Constants.endpoints=Constants.endpoints1;
			Constants.endpoints_img=Constants.endpoints2;
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
		//初始化缓存文件
		DataTool.initSharedPreferences();
		
		// 利用开头动画这几秒时间可以初始化变量和自动登录
		Variables.uid = DataTool.getUid();
		
		if (NetworkHandler.isNetworkAvailable(this)) {
			Variables.network = 1;
			if (Variables.uid != 0) {
				if (DataTool.getIsPhoneVerfied()==1) {
					new AutoLogin().execute("");
					startForward(Constants.SPLASH_DISPLAY_LENGHT,this,MainActivity.class);
				}else if (DataTool.getIsPhoneVerfied()==0) {
					//跳转到手机验证页面，验证成功后自动登录
					startForward(Constants.SPLASH_DISPLAY_LENGHT_SHORT,this,VerifyPhoneActivity.class);
				}
				//测试代码
//				new AutoLogin().execute("");
//				startForward(Constants.SPLASH_DISPLAY_LENGHT,this,MainActivity.class);
				//测试代码
			}else {
				// 同步时间
				YaoPao01App.cloudManager.synTimeWithServer();
				startForward(Constants.SPLASH_DISPLAY_LENGHT_SHORT,this,MainActivity.class);
			}
		}else {
			startForward(Constants.SPLASH_DISPLAY_LENGHT_SHORT,this,MainActivity.class);
		}
		
		 Log.v("wynet", "Variables.network="+Variables.network);
		 //初始化引擎 
		initSportParam();
		
//		if (Variables.uid != 0) {
//			startForward(Constants.SPLASH_DISPLAY_LENGHT_SHORT,this,VerifyPhoneActivity.class);
//		}else {
//			startForward(Constants.SPLASH_DISPLAY_LENGHT_SHORT,this,MainActivity.class);
//		}
	}
	
	void startForward(int length,Context thisActivity,final Class activity){
		new Handler().postDelayed(new Runnable() {
			public void run() {
		Intent mainIntent = new Intent(SplashActivity.this,	activity);
		SplashActivity.this.startActivity(mainIntent);
		SplashActivity.this.finish();
			}
		},length);
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
			Variables.islogin=2;
			Log.v("wyuser", "自动登录中");
		
			loginJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.autoLogin, "uid="+Variables.uid);
			Log.v("zc","自动登陆返回"+loginJson);
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
					//登录成功，通知主界面取消等待动画,开始同步记录
					if (MainActivity.loginHandler!=null) {
						MainActivity.loginHandler.obtainMessage(1).sendToTarget();
					}
					
					// 登录成功，初始化用户信息,比赛信息
					
					Variables.islogin = 1;
					//设置状态为自动登录，这样在主界面就会之后进行同步，之后将属性修改为false
					Variables.isAutoLogin=true;
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
					Variables.uid=0;
					DataTool.setUid(0);
					break;
				default:
					Variables.islogin=0;
					break;
				}

			} else {
				Variables.islogin=0;
				// Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试",
				// Toast.LENGTH_LONG).show();
			}
			//自动登录不论成功还是失败，都需要同步时间
			YaoPao01App.cloudManager.synTimeWithServer();
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
	 * 这里的初始化数据是用户上一次运动设置的运动参数
	 */
	private void initSportParam() {
		
		SportParaBean param = YaoPao01App.db.querySportParam(Variables.uid);
		Variables.switchTime=param.getCountDown();
		Variables.switchVoice=param.getVioce();
		Variables.runTargetType=param.getTargetIndex()==0?1:param.getTargetIndex();
		Variables.runType=param.getTypeIndex()==0?1:param.getTypeIndex();
		Variables.runTargetDis=param.getTargetdis()==0?5000:param.getTargetdis();
		Variables.runTargetTime=param.getTargettime()==0?1800000:param.getTargettime();
	}
}
