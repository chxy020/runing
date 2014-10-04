package net.yaopao.activity;

import net.yaopao.assist.Constants;
import net.yaopao.assist.Variables;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class WebViewActivity extends BaseActivity {
	

	/** webview */
	private WebView mWebView = null;
	private String mPageUrl = "";

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		mWebView = (WebView) this.findViewById(R.id.local_webview);
		// 获取页面URL
		Intent intent = getIntent();
		mPageUrl = intent.getStringExtra("net.yaopao.activity.PageUrl");

		// web设置
		WebSettings setting = mWebView.getSettings();
		setting.setSupportZoom(false);
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true);
		setting.setBuiltInZoomControls(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setHorizontalScrollbarOverlay(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setVerticalScrollbarOverlay(false);
		mWebView.setWebViewClient(new UserWebClient());
		
		
		//设置本地接口给js调用
		mWebView.addJavascriptInterface(new Object(){
			//回到前一页
			public void gotoPrePage(){
				//Log.e("","chxy _______prepage");
				WebViewActivity.this.finish();
			}
			//显示第三方web页面
			public void showThirdWeb(String url){
				Intent intent = new Intent(WebViewActivity.this,ThirdWebActivity.class);
				intent.putExtra("net.yaopao.activity.ThirdUrl",url);
				startActivity(intent);
				//Log.e("","chxy _______showThirdWeb" + url);
			}
		}, "JSAndroidBridge");

		// 加载web页面
		if (mPageUrl.equals("message_index.html")) {
			mWebView.loadUrl("file:///android_asset/web/" + mPageUrl);
		}
		else if(mPageUrl.equals("team_index.html")){
			mWebView.loadUrl("file:///android_asset/web/" + mPageUrl);
		}
		// 初始化
		this.initLoad();
	}

	/**
	 * 页面初始化,获取页面元素
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initLoad() {

	}
	
	
	
	private void jsCallbackMethod(String param){
		//Log.e("","chxy _______javascript" + param);
		mWebView.loadUrl("javascript:" + param + ";");
	}
	
	class UserWebClient extends WebViewClient{
		/* 设置webview的跳转始终在自己的activity,而不调用浏览器
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		*/
		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.v("wymatch", "match onPageFinished");
			// if(!firstComeIn)return;
			// firstComeIn = false;

			// int isLogin = MeController.isLogin;
			// String userInfoJSON = MeController.getUserInfoJSON();
			// String ua = GetXMLByHTTP.clientInfo;
			// String pid = GetXMLByHTTP.pid;
			// DisplayMetrics dm = new DisplayMetrics();
			// getWindowManager().getDefaultDisplay().getMetrics(dm);
			// int nowWidth = dm.widthPixels; // 当前分辨率 宽度
			// int nowHeight = dm.heightPixels; // 当前分辨率高度

			String userInfo = "";
			String playInfo = "";
			String deviceInfo = "";
			//Log.e("","chxy ____url" + url);
			if(-1 != url.indexOf("message_index.html")){
				//消息首页
				userInfo = "{\"uid\":\"" + Variables.uid + "\"}";
				playInfo = "{}";
				deviceInfo = "{\"deviceid\":\"" + Variables.pid + "\"}";
//				String param = "window.callbackInit('" + userInfo + "','" + playInfo + "'," + "'" + deviceInfo + "','" + Constants.endpoints3 + "')";
				String param = "window.callbackInit('" + userInfo + "','" + playInfo + "'," + "'" + deviceInfo + "','" + Constants.endpoints3 + "','" + Constants.endpoints2+ "')";

				jsCallbackMethod(param);
			}
			else if(-1 != url.indexOf("team_index.html")){
				//消息首页
				JSONObject user = new JSONObject();
				//未注册给空串""
				user.put("uid", Variables.uid==0?"":Variables.uid);
				//Log.e("","chxy_____uid" + Variables.uid);
				
				JSONObject localUserinfo = new JSONObject();
				user.put("username", localUserinfo.getString("uname")!=null?localUserinfo.getString("uname"):"");
				user.put("nickname", localUserinfo.getString("nikeName")!=null?localUserinfo.getString("nikeName"):localUserinfo.get("phone"));
				user.put("userphoto",localUserinfo.getString("imgpath")!=null?localUserinfo.getString("imgpath"):"");
				
				//未报名给空串""
				JSONObject loacalMatchInfo = Variables.matchinfo;
				user.put("bid", "1".equals(loacalMatchInfo.getString("issign"))?"l1":"");
				//未组队给空串""
				user.put("gid", "1".equals(loacalMatchInfo.getString("isgroup"))?loacalMatchInfo.getString("gid"):"");
				user.put("groupname", "1".equals(loacalMatchInfo.getString("isgroup"))?loacalMatchInfo.getString("groupname"):"");
				user.put("isleader",loacalMatchInfo.getString("isleader"));
				user.put("isbaton",loacalMatchInfo.getString("isbaton"));
				
				JSONObject play = new JSONObject();
				play.put("mid","1".equals(loacalMatchInfo.getString("ismatch"))?loacalMatchInfo.getString("mid"):"");
				play.put("stime","");
				play.put("etime","");
				
				JSONObject device = new JSONObject();
				device.put("deviceid",Variables.pid);
				device.put("platform","android");
				
				userInfo = user.toJSONString();
				playInfo = play.toJSONString();
				deviceInfo = device.toJSONString();
				
				Log.v("wymatch", "match window.callbackInit");
				String param = "window.callbackInit('" + userInfo + "','" + playInfo + "'," + "'" + deviceInfo + "','" + Constants.endpoints3 + "','" + Constants.endpoints2+ "')";
				Log.v("wymatch", "param="+param);
				//Log.e("","chxy_____________" + param);
				jsCallbackMethod(param);
			}
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
